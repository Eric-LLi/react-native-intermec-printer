package com.intermecprinter.usb;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

public class USBDeviceEndpoint {

	private USBDeviceDataThread mReadThread = null;

	public USBDeviceEndpoint(UsbDevice device, UsbDeviceConnection connection, UsbInterface inf) {
		UsbEndpoint epIn = null;
		UsbEndpoint epOut = null;

		for (int i = 0; i < inf.getEndpointCount(); i++) {
			UsbEndpoint ep = inf.getEndpoint(i);
			if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
				epIn = ep;
			} else if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
				epOut = ep;
			}
		}

		if (epIn == null || epOut == null) {
			throw new IllegalArgumentException("not all endpoints found");
		}
		Thread thread = (USBDeviceDataThread) USBDeviceDataThreadManager.getInstance().getThreadByIdentifier(device.getProductName());
		if (thread == null) {
			mReadThread = new USBDeviceDataThread(connection, epIn, epOut, device.getProductName());
			mReadThread.start();
		}
	}

	public void stopReadData() {
		if (mReadThread != null) {
			mReadThread.cancelThread();
		}
	}
}
