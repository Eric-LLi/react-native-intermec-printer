import { NativeModules } from 'react-native';

const { IntermecPrinter } = NativeModules;

export const init = () => {
	return IntermecPrinter.init();
};

export const sampleMethod = (str, num, callback) => {
	IntermecPrinter.sampleMethod(str, num, callback);
};

export const print = (printerID, macAddress, title, barcode, ticket_type) => {
	return IntermecPrinter.print(printerID, macAddress, title, barcode, ticket_type);
};

export default IntermecPrinter;
