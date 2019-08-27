package com.spd.print.jx.application;

import android.app.Application;

import com.spd.print.jx.R;
import com.spd.print.jx.impl.PrintImpl;
import com.speedata.libutils.SharedXmlUtil;

/**
 * @author zzc
 */
public class BaseApp extends Application {

    private static PrintImpl sPrinterImpl;
    public static Boolean isConnection = false;
    public static String deviceName = "";
    public static String deviceAddress = "";
    public static SharedXmlUtil mSharedXmlUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        sPrinterImpl = new PrintImpl();
        deviceName = getResources().getString(R.string.status_disconnect);
        deviceAddress = getResources().getString(R.string.status_disconnect);
        mSharedXmlUtil = SharedXmlUtil.getInstance(this, "setting");
    }

    public static PrintImpl getPrinterImpl() {
        return sPrinterImpl;
    }

}
