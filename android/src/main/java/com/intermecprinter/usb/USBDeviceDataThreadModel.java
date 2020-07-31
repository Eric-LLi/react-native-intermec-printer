package com.intermecprinter.usb;

public class USBDeviceDataThreadModel {

	private final Thread mThread;
	private final String mIdentifier;


	public USBDeviceDataThreadModel(Thread thread, String identifier) {
		mThread = thread;
		mIdentifier = identifier;
	}


	public Thread getThread() {
		return mThread;
	}

	/**
	 * Get the identifier of thread
	 *
	 * @return
	 */
	public String getIdentifier() {
		return mIdentifier;
	}
}

