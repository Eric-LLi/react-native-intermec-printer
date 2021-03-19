import { NativeModules, NativeEventEmitter } from 'react-native';

const { IntermecPrinter } = NativeModules;

export const PRINTER_EVENTS = {
	PRINTER_ERROR: 'printererror',
	PRINTER_STATUS: 'printerstatus',
};

const events = {};

const eventEmitter = new NativeEventEmitter(IntermecPrinter);

IntermecPrinter.on = (event, handler) => {
	const eventListener = eventEmitter.addListener(event, handler);

	events[event] = eventListener;
};

IntermecPrinter.off = (event, handler) => {
	if (events.hasOwnProperty(event)) {
		const eventListener = events[event];

		eventListener.remove();

		delete events[event];
	}
};

export default IntermecPrinter;
