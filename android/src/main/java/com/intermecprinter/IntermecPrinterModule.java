package com.intermecprinter;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.honeywell.mobility.print.LabelPrinter;
import com.honeywell.mobility.print.LabelPrinterException;
import com.honeywell.mobility.print.PrintProgressEvent;
import com.honeywell.mobility.print.PrintProgressListener;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IntermecPrinterModule extends ReactContextBaseJavaModule {

	private static final String TAG = "HoneywellPrinter";
	private String jsonCmdAttribStr = null;
	private static Promise rPromise = null;

	private final ReactApplicationContext reactContext;

	public IntermecPrinterModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}

	@Override
	public String getName() {
		return "IntermecPrinter";
	}

	@ReactMethod
	public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
		// TODO: Implement some actually useful functionality
		callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
	}


	/*******************************/
	/** Methods Available from JS **/
	/*******************************/

	@ReactMethod
	public void init(Promise promise) {
		InputStream input = null;
		ByteArrayOutputStream output = null;
		AssetManager assetManager = reactContext.getAssets();

		try {
			input = assetManager.open("printer_profiles.JSON");
			output = new ByteArrayOutputStream(8000);

			byte[] buf = new byte[1024];
			int len;
			while ((len = input.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			input.close();
			input = null;

			output.flush();
			output.close();
			jsonCmdAttribStr = output.toString();
			output = null;

			promise.resolve(true);
		} catch (Exception ex) {
			promise.reject(ex);
		} finally {
			try {
				if (input != null) {
					input.close();
					input = null;
				}

				if (output != null) {
					output.close();
					output = null;
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) Log.d(TAG, e.getMessage());
			}
		}
	}

	@ReactMethod
	public void print(String profile, String printerID, String macAddress, String title,
	                  String barcode, String ticket_type,
	                  final Promise promise) {
		rPromise = promise;
		// Create a PrintTask to do printing on a separate thread.
		PrintTask task = new PrintTask();

		// Executes PrintTask with the specified parameter which is passed
		// to the PrintTask.doInBackground method.
		task.execute(profile, printerID, macAddress, title, barcode, ticket_type);
	}


	/**
	 * This class demonstrates printing in a background thread and updates
	 * the UI in the UI thread.
	 */
	public class PrintTask extends AsyncTask<String, Integer, String> {
		private static final String PROGRESS_CANCEL_MSG = "Printing cancelled\n";
		private static final String PROGRESS_COMPLETE_MSG = "Printing completed\n";
		private static final String PROGRESS_ENDDOC_MSG = "End of label printing\n";
		private static final String PROGRESS_FINISHED_MSG = "Printer connection closed\n";
		private static final String PROGRESS_NONE_MSG = "Unknown progress message\n";
		private static final String PROGRESS_STARTDOC_MSG = "Start printing label\n";

		/**
		 * Runs on the UI thread before doInBackground(Params...).
		 */
		@Override
		protected void onPreExecute() {
			//
		}

		/**
		 * This method runs on a background thread. The specified parameters
		 * are the parameters passed to the execute method by the caller of
		 * this task. This method can call publishProgress to publish updates
		 * on the UI thread.
		 */
		@Override
		protected String doInBackground(String... args) {
//			String profiles = jsonCmdAttribStr.trim();
			String profiles = args[0];
			String sPrinterID = args[1];
			String sPrinterURI = "bt://" + formatMacAddress(args[2]);

			String sTitle = args[3];
			String sBarcode = args[4];
			String sTicketType = args[5];

			LabelPrinter lp = null;
			String sResult = null;

			if (BuildConfig.DEBUG)
				Log.d(TAG, "Printing to printer id " + sPrinterID + " with uri " + sPrinterURI +
						" and itemName " + sTitle + " and itemNo " + sBarcode);

			LabelPrinter.ExtraSettings exSettings = new LabelPrinter.ExtraSettings();
			exSettings.setContext(reactContext);

			try {
				lp = new LabelPrinter(
						profiles,
						sPrinterID,
						sPrinterURI,
						exSettings);

				// Registers to listen for the print progress events.
				lp.addPrintProgressListener(new PrintProgressListener() {
					@Override
					public void receivedStatus(PrintProgressEvent aEvent) {
						// Publishes updates on the UI thread.
						publishProgress(aEvent.getMessageType());
					}
				});

				// A retry sequence in case the bluetooth socket is temporarily not ready
				int numtries = 0;
				int maxretry = 2;
				while (numtries < maxretry) {
					try {
						lp.connect();  // Connects to the printer
						break;
					} catch (LabelPrinterException ex) {
						numtries++;
						Thread.sleep(1000);
					}
				}
				if (numtries == maxretry) lp.connect();//Final retry

				// Sets up the variable dictionary.
				LabelPrinter.VarDictionary varDataDict = new LabelPrinter.VarDictionary();
				varDataDict.put("ItemName", sTitle);
				varDataDict.put("ItemNo", sBarcode);
				varDataDict.put("ItemTicket", sTicketType);

				// Prints the ItemLabel as defined in the printer_profiles.JSON file.
				lp.writeLabel("ItemLabel", varDataDict);

//				sResult = "Number of bytes sent to printer: " + lp.getBytesWritten();
			} catch (LabelPrinterException ex) {
				sResult = "LabelPrinterException: " + ex.getMessage();
			} catch (Exception ex) {
				if (ex.getMessage() != null)
					sResult = "Unexpected exception: " + ex.getMessage();
				else
					sResult = "Unexpected exception.";
			} finally {
				if (lp != null) {
					try {
						// Notes: To ensure the data is transmitted to the printer
						// before the connection is closed, both PB22_Fingerprint and
						// PB32_Fingerprint printer entries specify a PreCloseDelay setting
						// in the printer_profiles.JSON file included with this sample.
						lp.disconnect();  // Disconnects from the printer
						lp.close();  // Releases resources
					} catch (Exception ex) {
						if (BuildConfig.DEBUG) Log.d(TAG, ex.getMessage());
					}
				}
			}

			if (BuildConfig.DEBUG && sResult != null) Log.d(TAG, sResult);
			// The result string will be passed to the onPostExecute method
			// for display in the the Progress and Status text box.
			return sResult;
		}

		/**
		 * Runs on the UI thread after publishProgress is invoked. The
		 * specified values are the values passed to publishProgress.
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO
			// Access the values array.
			int progress = values[0];

			switch (progress) {
				case PrintProgressEvent.MessageTypes.CANCEL:
//					textMsg.append(PROGRESS_CANCEL_MSG);
					break;
				case PrintProgressEvent.MessageTypes.COMPLETE:
//					textMsg.append(PROGRESS_COMPLETE_MSG);
					break;
				case PrintProgressEvent.MessageTypes.ENDDOC:
//					textMsg.append(PROGRESS_ENDDOC_MSG);
					break;
				case PrintProgressEvent.MessageTypes.FINISHED:
//					textMsg.append(PROGRESS_FINISHED_MSG);
					break;
				case PrintProgressEvent.MessageTypes.STARTDOC:
//					textMsg.append(PROGRESS_STARTDOC_MSG);
					break;
				default:
//					textMsg.append(PROGRESS_NONE_MSG);
					break;
			}
		}

		/**
		 * Runs on the UI thread after doInBackground method. The specified
		 * result parameter is the value returned by doInBackground.
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				rPromise.reject(String.valueOf(1), result);
			} else {
				rPromise.resolve(true);
			}
		}
	} //endofclass PrintTask


	/**
	 * If the specified MAC address contains 12 characters without the ":"
	 * delimiters, it adds the delimiters; otherwise, it returns the original
	 * string.
	 *
	 * @param aMacAddress A string containing the MAC address.
	 * @return a formatted string or the original string.
	 */
	private static String formatMacAddress(String aMacAddress) {
		if (aMacAddress != null && !aMacAddress.contains(":") &&
				aMacAddress.length() == 12) {
			// If the MAC address only contains hex digits without the
			// ":" delimiter, then add ":" to the MAC address string.
			char[] cAddr = new char[17];

			for (int i = 0, j = 0; i < 12; i += 2) {
				aMacAddress.getChars(i, i + 2, cAddr, j);
				j += 2;
				if (j < 17) {
					cAddr[j++] = ':';
				}
			}

			return new String(cAddr);
		} else {
			return aMacAddress;
		}
	}
}
