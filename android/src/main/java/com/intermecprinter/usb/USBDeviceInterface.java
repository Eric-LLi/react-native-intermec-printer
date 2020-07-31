package com.intermecprinter.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;

import java.util.Objects;

public class USBDeviceInterface {

	private @NonNull
	UsbManager mManager = null;
	private USBDeviceEndpoint mEndpoint = null;
	private UsbInterface mInterface = null;
	private UsbDeviceConnection mConnection = null;
	public UsbDevice usbDevice = null;

	public USBDeviceInterface(@NonNull UsbManager manager) throws NullPointerException {
		this.mManager = Objects.requireNonNull(manager, "UsbManager must not be null");
	}

	// searches for an adb interface on the given USB device
	public UsbInterface findInterface(UsbDevice device) {
		if (device == null) {
			return null;
		}

		if (!Utils.isHoneywellUsbDevice(device.getVendorId())) {
			return null;
		}

		int count = device.getInterfaceCount();

		for (int i = 0; i < count; i++) {
			UsbInterface inf = device.getInterface(i);
			//DLog.i("find interface: " + inf);


			if (inf.getEndpointCount() == 2) {
				//  DLog.e("filter interface " + inf);
				// use REM interface to send command to control scanner
				if ((device.getProductId() == 0x0e47) || (device.getProductId() == 0x0e41)) {
					if (inf.getName().equals("REM")) {
						//   if (inf.getName().equals("HID Keyboard Emulation"))
						//  {
						return inf;
					}
				} else {
					return inf;
				}
			}
		}
		return null;
	}

	public String getUsbDeviceIdentifer() {
		if (usbDevice == null) {
			return "";
		}
		return usbDevice.getProductName();
	}

	/**
	 * Create the connection with usb device and find the proper endpoint to receive
	 * data from usb device.
	 *
	 * @param device usb scanner
	 * @param inf    the interface of usb scanner.
	 * @return the result of connecting to usb scanner.
	 */
	public boolean setInterface(UsbDevice device, UsbInterface inf) {
		if (device == null || inf == null) {
			return false;
		}

		UsbDeviceConnection connection = mManager.openDevice(device);
		if (connection != null) {
			if (connection.claimInterface(inf, true)) {
				usbDevice = device;
				mConnection = connection;
				mInterface = inf;
				mEndpoint = new USBDeviceEndpoint(device, mConnection, inf);
				return true;
			} else {
				connection.close();
			}
		}
		return false;
	}

	public void removeInterface() {
		//DLog.e("removeInterface");

		if (mConnection != null) {
			mConnection.releaseInterface(mInterface);
			mConnection.close();
			mConnection = null;
			mInterface = null;
		}
		if (mEndpoint != null) {
			mEndpoint.stopReadData();
			mEndpoint = null;
		}
		usbDevice = null;
	}
}
