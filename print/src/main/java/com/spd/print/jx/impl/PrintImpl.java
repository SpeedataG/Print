package com.spd.print.jx.impl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControlSpd;
import android.support.annotation.NonNull;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.Table;
import com.spd.print.jx.inter.IConnectCallback;
import com.spd.print.jx.inter.IPrint;
import com.spd.print.jx.utils.PictureUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :zzc in  2020/8/27 14:53 update.
 * 功能描述:打印机实现类
 */
public class PrintImpl implements IPrint {
    private IConnectCallback mCallback;
    private PrinterInstance mPrinter;

    @Override
    public PrinterInstance connectPrinter(@NonNull IConnectCallback callback) {
        mCallback = callback;
        try {
            DeviceControlSpd deviceControl = new DeviceControlSpd(DeviceControlSpd.PowerType.NEW_MAIN, 8);
            deviceControl.PowerOnDevice();
            deviceControl.newSetDir(46, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void closeConnect() {
        if (mPrinter == null) {
            return;
        }
        mPrinter.closeConnection();
        mPrinter = null;
        try {
            DeviceControlSpd deviceControl = new DeviceControlSpd(DeviceControlSpd.PowerType.NEW_MAIN, 8);
            deviceControl.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int sendBytesData(byte[] srcData) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.sendBytesData(srcData);
    }

    @Override
    public int read(byte[] buffer) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.read(buffer);
    }

    @Override
    public void initPrinter() {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.initPrinter();
    }

    @Override
    public void setFont(int mCharacterType, int mWidth, int mHeight, int mBold, int mUnderline) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.setFont(mCharacterType, mWidth, mHeight, mBold, mUnderline);
    }

    @Override
    public void setPrinter(int command, int value) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        List commandList = new ArrayList();
        commandList.add(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LNCH);
        commandList.add(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE);
        commandList.add(PrinterConstants.Command.ALIGN);
        mPrinter.setPrinter((int) commandList.get(command), value);
    }

    @Override
    public int setPaperType(int paperType) {
        List<byte[]> paperList = new ArrayList<>();
        paperList.add(new byte[]{0x1F, 0x11, 0x1F, 0x44, (byte) 0, 0x1F, 0x1F});
        paperList.add(new byte[]{0x1F, 0x11, 0x1F, 0x44, (byte) 1, 0x1F, (byte) 192, (byte) 128, 0x1F, 0x46, (byte) 23, 0x1F, 0x1F});
        paperList.add(new byte[]{0x1F, 0x11, 0x1F, 0x44, (byte) 2, 0x1F, 0x1F});
        return sendBytesData(paperList.get(paperType));
    }

    @Override
    public int setDensity(int density) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.sendBytesData(new byte[]{0x1F, 0x11, 0x1F, 0x16, (byte) density, 0x1F, 0x1F});
    }

    @Override
    public void setSpeed(int speed) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.sendBytesData(new byte[]{0x1F, 0x11, 0x1F, 0x15, (byte) speed, 0x1F, 0x1F});
    }

    @Override
    public void setPaperFeed(int line) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        line = line * 8;
        byte[] bytes = new byte[]{0x1B, 0x4A, (byte) line};
        mPrinter.sendBytesData(bytes);
    }

    @Override
    public void setPaperBack(int line) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        line = line * 8;
        byte[] bytes = new byte[]{0x1B, 0x4B, (byte) line};
        mPrinter.sendBytesData(bytes);
    }

    @Override
    public void printSelfCheck() {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        byte[] bytes = new byte[]{0x1d, 0x28, 0x41, 0x02, 0x00, 0x00, 0x02};
        mPrinter.sendBytesData(bytes);
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
    public int printBarCode(byte barcodeType, int param1, int param2, int param3, String content) {
        return printBarCode(new Barcode(barcodeType, param1, param2, param3, content));
    }

    @Override
    public void printImage(Bitmap bitmap, int alignType, int left, boolean isCompressed) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        List<PrinterConstants.PAlign> alignList = new ArrayList<>();
        alignList.add(PrinterConstants.PAlign.START);
        alignList.add(PrinterConstants.PAlign.CENTER);
        alignList.add(PrinterConstants.PAlign.END);
        alignList.add(PrinterConstants.PAlign.NONE);
        mPrinter.printImage(bitmap, alignList.get(alignType), left, isCompressed);
    }

    @Override
    public void printBigImage(Bitmap bitmap, int alignType, int left, boolean isCompressed) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        List<PrinterConstants.PAlign> alignList = new ArrayList<>();
        alignList.add(PrinterConstants.PAlign.START);
        alignList.add(PrinterConstants.PAlign.CENTER);
        alignList.add(PrinterConstants.PAlign.END);
        alignList.add(PrinterConstants.PAlign.NONE);
        PictureUtils.printBitmapImage(mPrinter, bitmap, alignList.get(alignType), left, isCompressed);
    }

    @Override
    public void printTable(Table table) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.printTable(table);
    }

    @Override
    public void printTable(String column, String regularExpression, int[] columnWidth, ArrayList<String> rows) {
        Table table = new Table(column, regularExpression, columnWidth);
        if (rows != null && rows.size() > 0) {
            for (String row : rows) {
                table.addRow(row);
            }
        }
        printTable(table);
    }

    @Override
    public int update(InputStream inputStream, String hexFileLength) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return UpdatePrinter.update(inputStream, hexFileLength, mPrinter);
    }

    @Override
    public void setSensitivity(int sensitivity) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.sendBytesData(new byte[]{0x1F, 0x11, 0x1F, 0x46, (byte) sensitivity, 0x1F, 0x1F});
    }

    @Override
    public void setOutPaperLen(int len) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        len = len * 8;
        mPrinter.sendBytesData(new byte[]{0x1F, 0x11, 0x1F, (byte) 0xC0, (byte) len, 0x1F, 0x1F});
    }

    @Override
    public void searchGap() {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        mPrinter.sendBytesData(new byte[]{0x0C});
    }

    @Override
    public void setAllParams(byte[]... params) {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        int len = params.length * 3 + 4;
        byte[] send = new byte[len];
        for (int i = 0; i < params.length; i++) {
            System.arraycopy(params[i], 0, send, i * 3 + 2, 3);
        }
        send[0] = 0x1F;
        send[1] = 0x11;
        send[len - 2] = 0x1F;
        send[len - 1] = 0x1F;
        mPrinter.sendBytesData(send);
    }

    @Override
    public int getPrinterStatus() {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.getPrinterStatus();
    }

    @Override
    public int getPrintDensity() {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        int d = mPrinter.getPrintDensity();
        return d - 48;
    }

    @Override
    public int getPaperType() {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.getPaperType() - 48;
    }

    @Override
    public int getPaperSensitivity() {
        if (mPrinter == null) {
            throw new RuntimeException("先调用connectPrinter方法初始化打印机操作类");
        }
        return mPrinter.getPaperSensitivity();
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
