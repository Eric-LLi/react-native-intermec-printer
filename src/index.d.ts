export declare function sampleMethod(
	stringArgument: string,
	numberArgument: number,
	callback: (str: string, num: number) => void
): void;

export declare function print(
	printerID: string,
	macAddress: string,
	itemName: string,
	itemNo: string
): Promise<boolean>;
export declare function init(): Promise<boolean>;
