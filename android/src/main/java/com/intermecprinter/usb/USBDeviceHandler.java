package com.intermecprinter.usb;
import android.content.Context;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class USBDeviceHandler {

    private static volatile USBDeviceHandler mInstance = null;
    private ArrayList<USBDeviceModel> mUSBDeviceList = new ArrayList<>();

    private USBDeviceDataThread mThread = null;
    private USBDeviceFinderManager mUSBDeviceFinderManager;
    private String m_Res_buf;

    public USBDeviceHandler() {

    }

    public static final USBDeviceHandler getInstance() {
        if (mInstance == null) {
            synchronized (USBDeviceHandler.class) {
                if (mInstance == null) {
                    mInstance = new USBDeviceHandler();
                }
            }
        }
        return mInstance;
    }

    public void initializeUsbFinder(Context context, final int usbType) {
        mUSBDeviceFinderManager = USBDeviceFinderManager.getInstance(context, usbType);
    }

    private void initialize(USBDeviceModel model) {

        mThread = (USBDeviceDataThread) USBDeviceDataThreadManager.getInstance().getThreadByIdentifier(model.getAddress());
    }

    public void updateUSBDeviceStatus(USBDeviceModel printer, int status) {
        Iterator<USBDeviceModel> iterator = mUSBDeviceList.iterator();

        if (status == USBDeviceDef.STATE_CONNECTED) {
            initialize(printer);
            Boolean isDuplicate = false;
            while (iterator.hasNext()) {
                USBDeviceModel temp = iterator.next();
                if (temp != null && temp.getName().equals(printer.getName())) {
                    isDuplicate = true;
                    break;
                }
            }
            m_Res_buf = "";
            if (!isDuplicate) {
                //DLog.e(printer.getName() + " connected!");
                mUSBDeviceList.add(printer);
                if (mCallback != null) {
                    mCallback.onConnectionStatus(status, printer);
                }
            }

        } else if (status == USBDeviceDef.STATE_DISCONNECTED) {

            while (iterator.hasNext()) {
                USBDeviceModel temp = iterator.next();
                if (temp != null && temp.getName().equals(printer.getName())) {
                    iterator.remove();
                    //   DLog.e(printer.getName() + "disconnected!");
                    if (mCallback != null) {
                        mCallback.onConnectionStatus(status, printer);
                    }
                    break;
                }
            }
        }
    }

    public void destory() {
        if (mUSBDeviceFinderManager != null) {
            mUSBDeviceFinderManager.onDestroy();
            mUSBDeviceFinderManager = null;
        }
    }

    public ArrayList<USBDeviceModel> getConnectedUSBDeviceList() {
        return mUSBDeviceList;
    }

    public void addCallback(final USBDeviceConCallback callback) {
        mCallback = callback;
    }

    private USBDeviceConCallback mCallback = null;

    public void removeCallback() {
        mCallback = null;
    }

    public void onDataReceived(String str) {
        m_Res_buf += str;

        if (m_Res_buf.length() > 12) {
            if (m_Res_buf.contains("?PRSTAT\n") && m_Res_buf.contains("Ok")) {
                int position = m_Res_buf.indexOf("?PRSTAT\n");
                if ((position >= 0) && (m_Res_buf.length() >= (position + 13))) {
                    String value = m_Res_buf.substring(position + 8, position + 13);
                    String s = "";
                    if (value.contains(" ")) {
                        int pos = value.indexOf(" ");
                        s = value.substring(0, pos);
                        m_Res_buf = "";
                        int errValue = Integer.valueOf(s).intValue();
                        Map<Integer, String> map = new HashMap<Integer, String>();
                        map.put(0,"Print head lifted");
                        map.put(1,"Label not removed");
                        map.put(2,"Label Stop Sensor (LSS) detects no label");
                        map.put(3,"Printer out of transfer ribbon (TTR) or ribbon installed");
                        map.put(4,"Printer out of transfer ribbon (TTR) or ribbon installed");
                        map.put(5,"Print head voltage too high");
                        map.put(6,"Printer is feeding");
                        map.put(7,"Printer out of media");
                        String tip = "";
                        boolean bError = false;
                        for (int i = 0; i < 8; i++) {
                            int value2 = (errValue >> i) & 1;
                            if (value2 == 1) {
                                bError = true;
                                tip += map.get(i) + "\r\n";
                            }
                        }
                        if(!bError)
                        {
                            tip = "Ok";
                        }
                        if (mCallback != null) {
                            mCallback.DeviceStatus(tip);
                        }
                    }
                }
            }
            if (m_Res_buf.contains("?SYSVAR(21)\n") && m_Res_buf.contains("Ok")) {

                String strs = "?SYSVAR(21)\n";
                int position = m_Res_buf.indexOf(strs);
                if ((position >= 0) && (m_Res_buf.length() >= (position + 18))) {
                    String value = m_Res_buf.substring(position + 12, position + 18);
                    String s = "";
                    int dpi = 0;
                    if (value.contains(" ")) {
                        int pos = value.indexOf(" ");
                        s = value.substring(0, pos);
                        m_Res_buf = "";
                        switch (s) {
                            case "12": {
                                dpi  = 1;
                            }
                            break;
                            case "8": {
                                dpi  = 0;
                            }
                            break;
                        }
                        if (mCallback != null) {
                            mCallback.DeviceDPI(dpi);
                        }
                    }
                }
            }
        }
    }

    public int sendRawData(byte[] data) {
        if (data.length == 0) {
            //DLog.e("data is invalid");
            return USBDeviceDef.CODE_ERROR_PARAMETER;
        }
        if (mThread != null) {
            mThread.write(data);
        } else {
            return USBDeviceDef.CODE_NOT_EXISTS;
        }
        return USBDeviceDef.CODE_SUCCESS;
    }
}
