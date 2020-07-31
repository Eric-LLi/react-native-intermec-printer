package com.intermecprinter.usb;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

public class USBDeviceDef {

	/**
	 * Bluetooth scanner UUID
	 */
	public final static UUID uuid_serial = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	//  public final static int scanner = 0;
	public final static int printer = 1;

	/**
	 * USB Printer vendor id
	 */
	public final static int vendorId = 3118;
	public final static int vendorIdPrinter = 1662;

	@IntDef(value = {
			STATE_CONNECTED_FAILED,
			STATE_DISCONNECTED,
			STATE_LISTENING,
			STATE_CONNECTING,
			STATE_CONNECTED
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface PrinterState {
	}


	/**
	 * The Peripheral/connection is in disconnecting state
	 */
	public static final int STATE_CONNECTED_FAILED = 0x0001;

	/**
	 * The Peripheral/connection is in disconnected state
	 */
	public static final int STATE_DISCONNECTED = 0x0002;

	/**
	 * The Peripheral/connection is in listening state
	 */
	public static final int STATE_LISTENING = 0x0004;

	/**
	 * The Peripheral/connection is in connecting state
	 */
	public static final int STATE_CONNECTING = 0x0008;

	/**
	 * The Peripheral/connection is in connected state
	 */
	public static final int STATE_CONNECTED = 0x0010;


	@IntDef(value = {
			CODE_SUCCESS,
			CODE_FAILED,
			CODE_EXISTS,
			CODE_NOT_EXISTS,
			CODE_ERROR_PARAMETER,
			CODE_NOT_SUPPORTED,

	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ReturnCode {
	}


	/**
	 * Operation success
	 */
	public static final int CODE_SUCCESS = 0x0001;

	/**
	 * Operation failure.
	 */
	public static final int CODE_FAILED = 0x0002;

	/**
	 * The object exist
	 */
	public static final int CODE_EXISTS = 0x0004;

	/**
	 * The object does not exist
	 */
	public static final int CODE_NOT_EXISTS = 0x0008;

	/**
	 * Parameter is not correct.
	 */
	public static final int CODE_ERROR_PARAMETER = 0x0010;

	/**
	 * Operation is not supported.
	 */
	public static final int CODE_NOT_SUPPORTED = 0x0020;


	@IntDef(value = {
			MODE_DATA_PROCESSED,
			MODE_DATA_NOT_PROCESSED,
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface DataMode {
	}

	/**
	 * The received data will be processed, once choose this mode, the received data will be parsed
	 * by SDK and notify listener by callback, such as onBarcodeDataReceived, OnButtonPressedReceived,
	 * onMenuCommandReceived and so on. this mode is default mode.
	 */
	public static final int MODE_DATA_PROCESSED = 0x0001;

	/**
	 * The received data will not be processed, once choose this mode, the received data will be transmitted
	 * without parsing, notify listener by call back,OnUnProcessedDataReceived.
	 */
	public static final int MODE_DATA_NOT_PROCESSED = 0x0002;

	@Retention(RetentionPolicy.SOURCE)
	public @interface CommandType {
	}

	@IntDef(value = {
			TYPE_CLASSIC_BLUETOOTH,
			TYPE_SERIAL_USB,
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface SupportType {
	}

	/**
	 * Bluetooth low energy scanner
	 */
	public static final int TYPE_CLASSIC_BLUETOOTH = 0x0001;

	/**
	 * Serial mode usb scanner
	 */
	public static final int TYPE_SERIAL_USB = 0x0002;


	@IntDef(value = {
			MODE_BT_HONEYWELL,
			MODE_BT_INTERMEC,
			MODE_USB_HONEYWELL,
			MODE_NOT_SUPPORTED
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface USBDeviceMode {
	}

	/**
	 * The classic bluetooth scanner of honeywell
	 */
	public static final int MODE_BT_HONEYWELL = 0x0001;

	/**
	 * The classic bluetooth scanner of intermec
	 */
	public static final int MODE_BT_INTERMEC = 0x0002;

	/**
	 * The usb serial mode scanner of honeywell
	 */
	public static final int MODE_USB_HONEYWELL = 0x0004;

	/**
	 * Not supported mode of scanner.
	 */
	public static final int MODE_NOT_SUPPORTED = 0x0008;

	public static final int UP_FW_PREPARE_READY = 1;
	public static final int UP_FW_READY = 2;
	public static final int UP_FW_START = 3;
	public static final int UP_FW_PG_SUCCESS = 4;
	public static final int UP_FW_PG_ERROR = 5;
	public static final int UP_FW_COMPLETED = 6;
	public static final int UP_FW_REBOOT = 7;

}
