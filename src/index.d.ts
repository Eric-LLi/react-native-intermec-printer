export declare function init(): () => void;

export declare function disconnect(): () => void;

export declare function print(
	profile: string,
	printerID: string,
	macAddress: string,
	title: string,
	barcode: string,
): Promise<boolean | Error>;

export declare function printUSB(msg: Array<string>): void;

export declare function on(event: PRINTER_EVENTS, callback: (errorMsg: string) => void): void;
export declare function off(event: PRINTER_EVENTS, callback: (errorMsg: string) => void): void;

export declare function ConnectType(is_usb: boolean) : void;

export enum PRINTER_EVENTS {
	PRINTER_ERROR = 'printererror',
	PRINTER_STATUS = 'printerstatus',
}
