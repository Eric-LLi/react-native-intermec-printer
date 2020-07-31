package com.intermecprinter.usb;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

import androidx.annotation.NonNull;

import static android.util.Log.d;

public class USBDeviceDataThread extends Thread {
	private final int mReadMaxPacketSize;
	private final int mWriteMaxPacketSize;
	private final int mReadInterval;
	private final int mWriteInterval;

	private UsbDeviceConnection mUsbDeviceConnection = null;
	private UsbEndpoint mUsbEndpointIn = null;
	private UsbEndpoint mUsbEndpointOut = null;
	private USBDeviceDataThreadModel mModel = null;
	private boolean mStop = false;

	public USBDeviceDataThread(@NonNull UsbDeviceConnection connection, @NonNull UsbEndpoint epIn, @NonNull UsbEndpoint epOut, String identifier) {

		setName("USBDeviceDataThread-" + getId());

		mUsbDeviceConnection = connection;
		mUsbEndpointIn = epIn;
		mUsbEndpointOut = epOut;

		mReadMaxPacketSize = 4 * 1024;//epIn.getMaxPacketSize() > 0 ? epIn.getMaxPacketSize() : 64;
		mWriteMaxPacketSize = 4 * 1024;// epOut.getMaxPacketSize() > 0 ? epOut.getMaxPacketSize() : 64;
		mReadInterval = epIn.getInterval() > 0 ? epIn.getInterval() : 0;
		mWriteInterval = epOut.getInterval() > 0 ? epOut.getInterval() : 0;

		mModel = new USBDeviceDataThreadModel(this, identifier/*device.getSerialNumber()*/);

		USBDeviceDataThreadManager.getInstance().recordThread(mModel);
	}

	@SuppressLint("LongLogTag")
	@Override
	public void run() {
		super.run();
		byte[] buffer = new byte[mReadMaxPacketSize];

		synchronized (this) {
			while (!mStop) {
				int length = buffer.length;
				if (buffer.length != mReadMaxPacketSize) {
					buffer = new byte[mReadMaxPacketSize];
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (mUsbDeviceConnection != null) {

					int bytesRead = mUsbDeviceConnection.bulkTransfer(mUsbEndpointIn, buffer, buffer.length, mReadInterval);

					if (bytesRead > 0) {
						//  int length2 = buffer.length;
						String str = new String(buffer, 0, bytesRead);
						d("test", str);
						USBDeviceHandler.getInstance().onDataReceived(str);
						//      HCDHandler.getInstance().onDataReceived(buffer, bytesRead);
						//    java.util.Arrays.fill(buffer, (byte) 0);
					}
				}
			}
		}
	}

	/**
	 * Write to output stream
	 *
	 * @param buffer the buffer
	 */
	public void write(byte[] buffer) {
		write(buffer, mWriteMaxPacketSize);
	}

	/**
	 * Write to output stream
	 *
	 * @param buffer          the buffer
	 * @param writeBufferSize the output buffer size for sending the buffer data
	 */
	@SuppressLint("LongLogTag")
	public void write(byte[] buffer, int writeBufferSize) {
		int count = 0;
		int remainder = buffer.length;
		try {
			if (writeBufferSize > 0) {
				//  int length = buffer.length;
				count = buffer.length / writeBufferSize;
				remainder = buffer.length % writeBufferSize;
			}

			for (int i = 0; i < count; i++) {
				mUsbDeviceConnection.bulkTransfer(mUsbEndpointOut, buffer, buffer.length, mWriteInterval);
			}

			if (remainder > 0) {
				mUsbDeviceConnection.bulkTransfer(mUsbEndpointOut, buffer, buffer.length, mWriteInterval);
			}
		} catch (Exception e) {
			//   DLog.e("write failed");
		}
	}

	public void cancelThread() {
		//DLog.i(getName() + ", cancel");
		interrupt();
		USBDeviceDataThreadManager.getInstance().removeThread(mModel);
		mStop = true;
		mUsbDeviceConnection.close();
		mUsbDeviceConnection = null;
		mUsbEndpointIn = null;
		mUsbEndpointOut = null;
	}
}
