package com.intermecprinter.usb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class USBDeviceModel {
	private final String mIdentifier;
	private final String mUSBDeviceName;
	private final int mMode;
	private int mDataMode;
	private String mFwVersion = "UnKnown";

	/**
	 * Initialize the instance of USBDeviceModel
	 *
	 * @param identifier the unique identifier of the scanner.
	 *                   it will be set MAC address when the scanner is bt scanner.
	 *                   it will be set serialNumber when the scanner is usb scanner.
	 * @param name       the name of scanner
	 * @param mode       the mode {@link USBDeviceDef.USBDeviceMode} of scanner
	 */
	public USBDeviceModel(@NonNull final String identifier, @NonNull final String name, @USBDeviceDef.USBDeviceMode int mode) throws NullPointerException {
		Objects.requireNonNull(identifier, "Identifier must be not null");
		Objects.requireNonNull(name, "name must be not null");

		mIdentifier = identifier;
		mUSBDeviceName = name;
		mMode = mode;

		switch (mMode) {
			case USBDeviceDef.MODE_USB_HONEYWELL:
				mDataMode = USBDeviceDef.MODE_DATA_PROCESSED;
				break;
			default:
				mDataMode = USBDeviceDef.MODE_DATA_NOT_PROCESSED;

		}
	}

	/**
	 * Get the scanner name
	 *
	 * @return the name of the scanner
	 */
	@Nullable
	public String getName() {
		return mUSBDeviceName;
	}

	/**
	 * Get the unique identifier of the scanner.
	 * As the bt scanner, the identifier is MAC address,
	 * as the usb scanner, the identifier is serialNumber
	 *
	 * @return the identifier
	 */
	@NonNull
	public String getAddress() {
		return mIdentifier;
	}

	/**
	 * Get the printer type {@link USBDeviceDef.USBDeviceMode} of the scanner.
	 *
	 * @return
	 */
//    @NonNull
//    public int getPrinterComMode() {
//        return mMode;
//    }
//
//    public void setFwVersion(final String fw_version) {
//        mFwVersion = fw_version;
//    }
//
//    public String getFwVersion() {
//        return mFwVersion;
//    }

}
