export declare function sampleMethod(
	stringArgument: string,
	numberArgument: number,
	callback: (str: string, num: number) => void
): void;

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

export declare function on(event: PrinterEvents, callback: (errorMsg: string) => void): void;
export declare function off(event: PrinterEvents, callback: (errorMsg: string) => void): void;

export declare function ConnectType(is_usb: boolean) : void;

export enum PrinterEvents {
	PRINTER_ERROR = 'printererror',
	PRINTER_STATUS = 'printerstatus',
}
