package com.spd.print.jx.impl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.spd.print.jx.inter.IConnectCallback;
import com.spd.print.jx.inter.IPrint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :Reginer in  2019/8/16 10:24.
 * 联系方式:QQ:282921012
 * 功能描述:打印机实现类
 */
public class PrintImpl implements IPrint {
    private IConnectCallback mCallback;
    private PrinterInstance mPrinter;

    @Override
    public PrinterInstance connectPrinter(@NonNull IConnectCallback callback) {
        mCallback = callback;
        mPrinter = PrinterInstance.getPrinterInstance(new File("/dev/ttyMT0"), 115200, 0, printerHandler);
        mPrinter.openConnection();
        return mPrinter;
    }

    @Override
    public PrinterInstance connectPrinter(File device, int baudrate, int flags, @NonNull IConnectCallback callback) {
        mCallback = callback;
        mPrinter = PrinterInstance.getPrinterInstance(device, baudrate, flags, printerHandler);
        mPrinter.openConnection();
        return mPrinter;
    }

    @Override
    public int setPaperType(int paperType) {
        List<byte[]> paperList = new ArrayList<>();
        paperList.add(new byte[]{0x1F, 0x11, 0x1F, 0x44, (byte) 0, 0x1F, 0x1F});
        paperList.add(new byte[]{0x1F, 0x11, 0x1F, 0x44, (byte) 1, 0x1F, (byte) 192, (byte) 120, 0x1F, 0x46, (byte) 23, 0x1F, 0x1F});
        paperList.add(new byte[]{0x1F, 0x11, 0x1F, 0x44, (byte) 2, 0x1F, 0x1F});
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.sendBytesData(paperList.get(paperType));
    }

    @Override
    public void printText(String text) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.printText(text);
    }

    @Override
    public int printBarCode(Barcode barcode) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.printBarCode(barcode);
    }

    @Override
    public void printImage(Bitmap bitmap, PrinterConstants.PAlign alignType, int left, boolean isCompressed) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.printImage(bitmap, alignType, left, isCompressed);
    }


    /**
     * 获取打印机操作类
     *
     * @return 打印机操作类
     */
    public PrinterInstance getPrinter() {
        return mPrinter;
    }

    @SuppressLint("HandlerLeak")
    private final Handler printerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PrinterConstants.Connect.SUCCESS) {
                mCallback.onPrinterConnectSuccess();
            } else {
                mCallback.onPrinterConnectFailed(msg.what);
            }
        }
    };
}
