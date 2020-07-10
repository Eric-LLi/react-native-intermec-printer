import { NativeModules } from 'react-native';

const { IntermecPrinter } = NativeModules;

export const sampleMethod = (str, num) => {
	IntermecPrinter.sampleMethod(str, num);
};

// export default IntermecPrinter;
