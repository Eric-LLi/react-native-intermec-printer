export declare function sampleMethod(
	stringArgument: string,
	numberArgument: number,
	callback: (str: string, num: number) => void
): void;

export declare function print(
	printerID: string,
	macAddress: string,
	title: string,
	barcode: string,
	ticket_type: string
): Promise<boolean | Error>;

export declare function init(): Promise<boolean>;
