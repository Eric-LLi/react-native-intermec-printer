package com.intermecprinter.usb;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Utils {

	private static final String SEYCHELLES_MAC_HEADER = "C0:EE:40";
	private static final String GRANIT_MAC_HEADER = "00:10:20";

	private static ArrayList<String> honeywellDevicesList = new ArrayList<>();

	static {
		honeywellDevicesList.add(SEYCHELLES_MAC_HEADER);
		honeywellDevicesList.add(GRANIT_MAC_HEADER);
	}

	public static boolean isHoneywellDevice(String btAddress) {
		boolean ret = false;
		if (!TextUtils.isEmpty(btAddress)) {
			String btUpperCase = btAddress.toUpperCase();
			for (String macHeader : honeywellDevicesList) {
				if (btUpperCase.startsWith(macHeader)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	public static boolean isHoneywellUsbDevice(int vendorId) {
		if ((vendorId == USBDeviceDef.vendorId) || (vendorId == USBDeviceDef.vendorIdPrinter) || (vendorId == 1060)) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyString(String string) {
		if (string == null || string.length() == 0) {
			return true;
		}

		return false;
	}

	public static boolean isEmptyByte(byte[] data) {
		if (data == null || data.length == 0) {
			return true;
		}

		return false;
	}

	public static long CrcCCITT(byte[] data) {
		long crc = 0;
		for (int pos = 0; pos < data.length; pos++) {
			crc = (crc >> 4) ^ (((data[pos] ^ crc) & 0xf) * 0x1081);
			crc = (crc >> 4) ^ ((((data[pos] >> 4) ^ crc) & 0xf) * 0x1081);
		}
		return (long) crc;
	}

	public static String GetFileName(String szPath) {
		int pos = szPath.lastIndexOf("\\");
		if (pos == -1) {
			pos = szPath.lastIndexOf("/");
		}
		String fileName = "";
		if (pos == -1) {
			fileName = szPath;
		} else {
			fileName = szPath.substring(pos + 1);
		}
		return fileName;
	}
}
