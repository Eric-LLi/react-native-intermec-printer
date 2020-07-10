import { NativeModules } from 'react-native';

const { IntermecPrinter } = NativeModules;

export const sampleMethod = (str, num, callback) => {
	IntermecPrinter.sampleMethod(str, num, callback);
};

export default IntermecPrinter;
