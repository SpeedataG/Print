package com.spd.print.jx.application;

import android.app.Application;

import com.spd.print.jx.impl.PrintImpl;

public class BaseApp extends Application {


    private static PrintImpl sPrinterImpl;
    public static Boolean isConnection = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sPrinterImpl = new PrintImpl();
    }

    public static PrintImpl getPrinterImpl() {
        return sPrinterImpl;
    }

}
