package com.spd.print.jx.demo;

import com.spd.print.jx.impl.PrintImpl;

/**
 * @author :Reginer in  2019/8/16 11:54.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class PrintInstant {

    private PrintImpl mPrintImpl;

    public static PrintInstant getInstance() {
        return PrintInstantHolder.INSTANCE;
    }

    private static class PrintInstantHolder {
        private static final PrintInstant INSTANCE = new PrintInstant();
    }

    public PrintImpl getPrintImpl() {
        return mPrintImpl;
    }

    public void setPrintImpl(PrintImpl mPrintImpl) {
        this.mPrintImpl = mPrintImpl;
    }
}
