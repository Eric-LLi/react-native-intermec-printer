package com.intermecprinter.usb;

import androidx.annotation.NonNull;

public interface USBDeviceConCallback {
	/**
	 * Invoked when connect to the device
	 *
	 * @param status The connect status described in class ConnectStatus
	 * @param device connected device.
	 */
	void onConnectionStatus(@USBDeviceDef.PrinterState final int status, @NonNull final USBDeviceModel usbDevice);

	void DeviceStatus(String str);

	void DeviceDPI(int Dpi);
}
