package com.intermecprinter.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;

import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class USBDeviceFinder {

	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

	private Context mContext = null;
	private boolean mReceiverTag = false;

	private UsbManager mUsbManager = null;
	private USBDeviceInterface mUsbDeviceInterface = null;
	private PendingIntent mPendingIntent = null;
	// private Map<String, USBDeviceInterface> mUSBInterfaceMap = new Map<String, USBDeviceInterface>();

	/**
	 * Initialize method and need to pass a valid context.
	 *
	 * @param context
	 */
	public USBDeviceFinder(@NonNull Context context, final int usbType) throws NullPointerException {
		this.mContext = Objects.requireNonNull(context, "context must not be null");
		mUsbManager = (UsbManager) mContext.getSystemService(mContext.USB_SERVICE);
		try {
			mUsbDeviceInterface = new USBDeviceInterface(mUsbManager);
		} catch (NullPointerException e) {
			throw new NullPointerException(e.toString());
		}
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
		addUSBActionListener();
		findExistingDevices(usbType);
	}

	protected UsbManager getUsbManager() {
		return mUsbManager;
	}

	/**
	 * To fetch the connected usb printer.l
	 */
	protected void findExistingDevices(final int usbType) {
		if (mUsbManager == null || mUsbDeviceInterface == null) {
			//DLog.e(mUsbManager + " " + mUsbDeviceInterface);
			return;
		}
		boolean bHave = false;
		// HashMap<String, UsbDevice> device1 = mUsbManager.getDeviceList();
		for (UsbDevice device : mUsbManager.getDeviceList().values()) {
			//0x0e47 HID POS mode, 0x0e41 keyboard mode  (device.getProductId() == 0x0e47)|| (device.getProductId() == 0x0e41)||
			if ((usbType == USBDeviceDef.printer) && ((device.getProductId() == 89) || (device.getProductId() == 63)))//printer
			//  || ((usbType == USBDeviceDef.scanner)&&((device.getProductId() == 0x0e47) || (device.getProductId() == 0x0e41)||(device.getProductId() == 0x0e53))))//scanner
			{
				int a = device.getVendorId();
				bHave = true;
				if (!mUsbManager.hasPermission(device)) {
					try {
						// if use following function, will pop up dialog to ask the right
						requestUsbPermission(device);
						// if use following function. will not pop up dialog but need to send the apk to honeywell for signing
						// grantAutomaticPermission(device);
						//   broadcastReceivedUSBDevice(device, USBDeviceDef.STATE_CONNECTED);
					} catch (NullPointerException e) {
						//DLog.e(e.toString());
					}
					//     break;
				}

				if (!mUsbDeviceInterface.getUsbDeviceIdentifer().equals(device.getProductName())) {
					UsbInterface inf = mUsbDeviceInterface.findInterface(device);
					if (mUsbDeviceInterface.setInterface(device, inf)) {
						try {
							broadcastReceivedUSBDevice(device, USBDeviceDef.STATE_CONNECTED);
						} catch (NullPointerException e) {
							//DLog.e(e.toString());
						}
						break;
					}
				}
			}
		}
		if (!bHave) {
			Toast toast = Toast.makeText(this.mContext, (String) "There is no connected usb device", Toast.LENGTH_SHORT);
			toast.show();
		}
	}


	public void onDestroy() {
		if (mContext != null && mReceiverTag) {
			mReceiverTag = false;
			mContext.unregisterReceiver(mReceiver);
		}

		if (mUsbDeviceInterface != null) {
			mUsbDeviceInterface.removeInterface();
			mUsbDeviceInterface = null;
		}
	}

	/**
	 * Register the usb detached、attached and permission event listener.
	 */
	protected void addUSBActionListener() {
		if (!mReceiverTag) {
			mReceiverTag = true;

			IntentFilter filter = new IntentFilter();
			filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
			filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
			filter.addAction(ACTION_USB_PERMISSION);
			mContext.registerReceiver(mReceiver, filter);
		}
	}

	/**
	 * Request the usb device permission.
	 *
	 * @param device
	 */
	protected void requestUsbPermission(UsbDevice device) throws NullPointerException {
		Objects.requireNonNull(device, "The device must not be null");

		Intent intent = new Intent();
		intent.setAction(ACTION_USB_PERMISSION);
		intent.putExtra(UsbManager.EXTRA_DEVICE, device);
		intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true);
		// DLog.e(device.getManufacturerName() + " request permission.");
		mUsbManager.requestPermission(device, mPendingIntent);
	}

	/**
	 * Remove the interface of usb device.
	 *
	 * @param device usb device
	 */
	protected void removeDeviceInterface(UsbDevice device) throws NullPointerException {
		Objects.requireNonNull(device, "The device must not be null");
		mUsbDeviceInterface.removeInterface();
	}

	public boolean hasPermission(UsbDevice device) {
		return mUsbManager.hasPermission(device);
	}

	public boolean grantAutomaticPermission(UsbDevice usbDevice) {
		try {
			PackageManager pkgManager = mContext.getPackageManager();
			ApplicationInfo appInfo = pkgManager.getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);

			Class serviceManagerClass = Class.forName("android.os.ServiceManager");
			Method getServiceMethod = serviceManagerClass.getDeclaredMethod("getService", String.class);
			getServiceMethod.setAccessible(true);
			android.os.IBinder binder = (android.os.IBinder) getServiceMethod.invoke(null, Context.USB_SERVICE);

			Class iUsbManagerClass = Class.forName("android.hardware.usb.IUsbManager");
			Class stubClass = Class.forName("android.hardware.usb.IUsbManager$Stub");
			Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", android.os.IBinder.class);
			asInterfaceMethod.setAccessible(true);
			Object iUsbManager = asInterfaceMethod.invoke(null, binder);

			//System.out.println("UID : " + appInfo.uid + " " + appInfo.processName + " " + appInfo.permission);
			final Method grantDevicePermissionMethod = iUsbManagerClass.getDeclaredMethod("grantDevicePermission", UsbDevice.class, int.class);
			grantDevicePermissionMethod.setAccessible(true);
			grantDevicePermissionMethod.invoke(iUsbManager, usbDevice, appInfo.uid);

			//  System.out.println("Method OK : " + binder + "  " + iUsbManager);
			return true;
		} catch (Exception e) {
			System.err.println("Error trying to assing automatic usb permission : ");
			e.printStackTrace();
			return false;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			//DLog.e("Received the actions: " + action);

			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							if (!mUsbDeviceInterface.getUsbDeviceIdentifer().equals(device.getProductName())) {
								UsbInterface inf = mUsbDeviceInterface.findInterface(device);
								if (inf != null) {
									if (mUsbDeviceInterface.setInterface(device, inf)) {
										try {
											int value = device.getVendorId();
											int value2 = device.getProductId();
											broadcastReceivedUSBDevice(device, USBDeviceDef.STATE_CONNECTED);
										} catch (NullPointerException e) {
											//DLog.e(e.toString());
										}
									}
								}
							}
						}
					} else {
						//     DLog.d( device.getManufacturerName() + " permission denied.");
					}
				}
			}

			//Received the notification when the usb device connected.
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

				if (!mUsbManager.hasPermission(device)) {
					if ((device.getProductId() == 89) || (device.getProductId() == 63)) {
						//   ||((device.getProductId() == 3655) || (device.getProductId() == 3649)|| (device.getProductId() == 0xe53))) {
						try {
							 requestUsbPermission(device);
							// grantAutomaticPermission(device);
						} catch (NullPointerException e) {
							//DLog.e(e.toString());
						}
						//  return;
					}
				}

				if (!mUsbDeviceInterface.getUsbDeviceIdentifer().equals(device.getProductName())) {
					UsbInterface inf = mUsbDeviceInterface.findInterface(device);
					if (inf != null) {
						//   mUsbDeviceInterface.removeInterface();
						if (mUsbDeviceInterface.setInterface(device, inf)) {
							try {
								broadcastReceivedUSBDevice(device, USBDeviceDef.STATE_CONNECTED);
							} catch (NullPointerException e) {
								//DLog.e(e.toString());
							}
						}
					}
				}
			}

			//Received the notification when the usb device disconnected.
			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				try {
					removeDeviceInterface(device);
					broadcastReceivedUSBDevice(device, USBDeviceDef.STATE_DISCONNECTED);
				} catch (NullPointerException e) {
					//DLog.e(e.toString());
				}
			}
		}
	};

	/**
	 * Send usb printer when the status has changed.
	 *
	 * @param device usb scanner
	 * @param status the connect status of usb scanner
	 */
	private void broadcastReceivedUSBDevice(UsbDevice device, int status) throws NullPointerException {
		Objects.requireNonNull(device, "The device must not be null");

		int type = USBDeviceDef.MODE_NOT_SUPPORTED;

		String name = "";
		if (Utils.isHoneywellUsbDevice(device.getVendorId())) {
			name = "Honeywell_" + device.getProductName();
			type = USBDeviceDef.MODE_USB_HONEYWELL;
		} else {
			name = device.getProductName();
		}

		try {
			int a = device.getVendorId();
			int b = device.getProductId();
			USBDeviceModel printer = new USBDeviceModel(device.getProductName()/*device.getSerialNumber()*/, name, type);
			USBDeviceHandler.getInstance().updateUSBDeviceStatus(printer, status);
		} catch (NullPointerException e) {
			//   DLog.e(e.toString());
		}
	}
}
