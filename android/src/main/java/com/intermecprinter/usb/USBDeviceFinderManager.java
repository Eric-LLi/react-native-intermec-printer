package com.intermecprinter.usb;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * The class is used to find printer.
 */
public class USBDeviceFinderManager {

	private USBDeviceFinder mUSBFinder = null;
	private ArrayList<Integer> mTypeList = null;

	private static volatile USBDeviceFinderManager mInstance = null;

	private USBDeviceFinderManager(Context context, final int usbType) {
		mTypeList = new ArrayList<>();
		mTypeList.add(USBDeviceDef.TYPE_SERIAL_USB);
		initWithSupportTypes(context, usbType);
	}

	private void initWithSupportTypes(Context context, final int usbType) {
		Iterator<Integer> iterator = mTypeList.iterator();
		while (iterator.hasNext()) {
			int type = iterator.next();
			switch (type) {
				case USBDeviceDef.TYPE_SERIAL_USB:
					try {
						mUSBFinder = new USBDeviceFinder(context, usbType);
					} catch (NullPointerException e) {
						//DLog.i(e.toString());
					}
					break;
			}
		}
	}

	/**
	 * Get the instance of USBDeviceFinderManager
	 *
	 * @param context it will not be allowed nonnull.
	 * @return
	 */
	public static USBDeviceFinderManager getInstance(@NonNull Context context, final int usbType) throws NullPointerException {
		Objects.requireNonNull(context, "context must be not null");
		if (mInstance == null) {
			synchronized (USBDeviceFinderManager.class) {
				if (mInstance == null) {
					mInstance = new USBDeviceFinderManager(context, usbType);
				}
			}
		}
		return mInstance;
	}

	/**
	 * Get the connected printer {@link USBDeviceModel}.
	 *
	 * @return a list of printer model
	 */
	public ArrayList<USBDeviceModel> getConnectedUSBDeviceList() {
		return USBDeviceHandler.getInstance().getConnectedUSBDeviceList();
	}

	/**
	 * To set the support printer type {@link USBDeviceDef.SupportType},
	 * if default, it will support TYPE_CLASSIC_BLUETOOTH and TYPE_SERIAL_USB.
	 *
	 * @param list
	 */
	public void setSupportPrinterType(final ArrayList<Integer> list) {
		mTypeList.clear();
		mTypeList = list;
	}


	/**
	 * Destroy the PrinterFinder
	 */
	public void onDestroy() {

		if (mUSBFinder != null) {
			mUSBFinder.onDestroy();
			mUSBFinder = null;
		}
		mInstance = null;
	}

	/**
	 * Get the support printer type list {@link USBDeviceDef.SupportType}
	 *
	 * @return must support one or more types.
	 */
	public List<Integer> getSupportTypes() {
		return mTypeList;
	}

}
