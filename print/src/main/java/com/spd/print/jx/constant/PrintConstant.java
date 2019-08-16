package com.spd.print.jx.constant;

import android.support.annotation.IntDef;

import com.printer.sdk.PrinterConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author :Reginer in  2019/8/16 10:11.
 * 联系方式:QQ:282921012
 * 功能描述:打印机常量类
 */
public class PrintConstant {
    /**
     * 连接成功
     */
    public static final int CONNECT_SUCCESS = PrinterConstants.Connect.SUCCESS;
    /**
     * 连接失败
     */
    public static final int CONNECT_FAILED = PrinterConstants.Connect.FAILED;
    /**
     * 连接关闭
     */
    public static final int CONNECT_CLOSED = PrinterConstants.Connect.CLOSED;
    /**
     * 无设备
     */
    public static final int CONNECT_NO_DEVICE = PrinterConstants.Connect.NODEVICE;
    /**
     * 打印机通信正常
     */
    public static final int PRINTER_WORKED = 0;
    /**
     * 打印机通信异常
     */
    public static final int PRINTER_NO_WORKED = -1;
    /**
     * 打印机缺纸
     */
    public static final int PRINTER_NEED_PAPER = -2;
    /**
     * 打印机开盖
     */
    public static final int PRINTER_UNCAPPED = -3;


    @IntDef({CONNECT_FAILED, CONNECT_CLOSED, CONNECT_NO_DEVICE, PRINTER_WORKED,
            PRINTER_NO_WORKED, PRINTER_NEED_PAPER, PRINTER_UNCAPPED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PrintConnectError {
    }
}
