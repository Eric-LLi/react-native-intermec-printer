import { NativeModules } from 'react-native';

const { IntermecPrinter } = NativeModules;

export const init = () => {
	return IntermecPrinter.init();
};

export const sampleMethod = (str, num, callback) => {
	IntermecPrinter.sampleMethod(str, num, callback);
};

export const print = (printerID, macAddress, itemName, itemNo) => {
	return IntermecPrinter.print(printerID, macAddress, itemName, itemNo);
};

export default IntermecPrinter;
