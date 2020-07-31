package com.intermecprinter.usb;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class USBDeviceDataThreadManager {

	private List<USBDeviceDataThreadModel> mList = null;
	private static volatile USBDeviceDataThreadManager mInstance = null;

	private USBDeviceDataThreadManager() {
		mList = new ArrayList<>();
	}

	/**
	 * The instance initialize method.
	 *
	 * @return
	 */
	public static final USBDeviceDataThreadManager getInstance() {
		if (mInstance == null) {
			synchronized (USBDeviceDataThreadManager.class) {
				if (mInstance == null) {
					mInstance = new USBDeviceDataThreadManager();
				}
			}
		}
		return mInstance;
	}

	public int recordThread(USBDeviceDataThreadModel model) {
		if (model == null || model.getThread() == null) {
			//  DLog.e("Record thread failed with invalid thread model");
			return USBDeviceDef.CODE_ERROR_PARAMETER;
		}

		boolean isExist = false;
//        for (USBDeviceDataThreadModel tmp : mList) {
//            if (tmp.getThread().getId() == model.getThread().getId()) {
//                isExist = true;
//            }
//        }
		for (USBDeviceDataThreadModel tmp : mList) {
			if (tmp.getIdentifier().equals(model.getIdentifier())) {
				isExist = true;
			}
		}
		if (!isExist) {
			//DLog.i("Record printer data thread: " + model.getThread().getName() + " - " + model.getIdentifier());
			mList.add(model);
			return USBDeviceDef.CODE_SUCCESS;
		} else {
			//  DLog.i("Duplicate printer data thread: " + model.getThread().getName() + " - " + model.getIdentifier());
			return USBDeviceDef.CODE_EXISTS;
		}
	}

	public int removeThread(USBDeviceDataThreadModel model) {
		if (model == null || model.getThread() == null) {
			//   DLog.e("Remove thread failed with invalid thread model");
			return USBDeviceDef.CODE_ERROR_PARAMETER;
		}

		Iterator<USBDeviceDataThreadModel> iterator = mList.iterator();
		while (iterator.hasNext()) {
			USBDeviceDataThreadModel tmp = iterator.next();
			if (tmp.getThread().getId() == model.getThread().getId()) {
				//DLog.i("Remove printer data thread: " + model.getThread().getName() + " - " + model.getIdentifier());
				iterator.remove();
				return USBDeviceDef.CODE_SUCCESS;
			}
		}
		return USBDeviceDef.CODE_NOT_EXISTS;
	}


	public Thread getThreadByIdentifier(@NonNull final String identifier) {
		if (Utils.isEmptyString(identifier)) {
			return null;
		}

		for (USBDeviceDataThreadModel model : mList) {
			if (model.getIdentifier().equals(identifier)) {
				//DLog.i("Find the data thread: " + model.getThread().getName() + " - " + model.getIdentifier());
				return model.getThread();
			}
		}
		return null;
	}
}
