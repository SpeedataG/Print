package com.spd.print.jx.inter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.Table;
import com.spd.print.jx.constant.DensityConstant;
import com.spd.print.jx.constant.PaperConstant;

import java.io.File;

/**
 * @author :Reginer in  2019/8/16 10:23.
 * 联系方式:QQ:282921012
 * 功能描述:打印机接口
 */
public interface IPrint {
    /**
     * 连接打印机
     *
     * @param callback 连接回调{@link IConnectCallback}
     * @return 打印机操作类, 需要可获取，不需要也可以如下使用:
     * <p>
     * PrintImpl impl = new PrintImpl();
     * impl.connectPrinter(callback);
     * impl.getPrinter().***
     */
    PrinterInstance connectPrinter(@NonNull IConnectCallback callback);

    /**
     * 连接打印机，动态指定各个参数，如参数确定，用上一个连接方法
     *
     * @param device   路径
     * @param baudrate 波特率
     * @param flags    flag
     * @param callback 连接回调{@link IConnectCallback}
     * @return 打印机操作类
     */
    PrinterInstance connectPrinter(File device, int baudrate, int flags, @NonNull IConnectCallback callback);

    /**
     * 设置纸类型
     *
     * @return 返回值应加以说明，我还没看到这返回值是什么意思，你了解的话需要加上
     * <p>
     * >0 成功发送到打印机的字节数
     * -1 未初始化打印
     * -2 srcData 为空或者srcData 里没有数据。
     * </p>
     */
    int setPaperType(@PaperConstant.PrintPaperType int paperType);

    /**
     * 设置浓度
     *
     * @param density 浓度值
     * @return <p>  >0 成功发送到打印机的字节数
     * -1 未初始化打印
     * -2 srcData 为空或者srcData 里没有数据。</p>
     */
    int setDensity(@DensityConstant.PrintDensity int density);

    /**
     * 设置走纸
     *
     * @param line 走纸行数 mm
     */
    void setPaperFeed(int line);

    /**
     * 设置退纸
     *
     * @param line 退纸行数 mm
     */
    void setPaperBack(int line);

    /**
     * 打印自检页
     */
    void printSelfCheck();


    //设置中的一些其他的设置参照setPaperType加上去

    /**
     * 打印文本
     *
     * @param text 待打印内容
     */
    void printText(String text);

    /**
     * 打印条码
     *
     * @param barcode 条码内容
     */
    int printBarCode(Barcode barcode);

    /**
     * 打印图片
     *
     * @param bitmap       单色位图
     * @param alignType    -
     * @param left         -
     * @param isCompressed 是否压缩
     */
    void printImage(Bitmap bitmap, PrinterConstants.PAlign alignType, int left, boolean isCompressed);

    /**
     * 数据分包打印 适合大图片打印
     *
     * @param bitmap       单色位图
     * @param alignType    对齐方式
     * @param left         偏移 alignType为NONE时有效
     * @param isCompressed 是否压缩
     */
    void printBigImage(Bitmap bitmap, PrinterConstants.PAlign alignType, int left, boolean isCompressed);

    /**
     * 打印表格
     *
     * @param table 表格
     */
    void printTable(Table table);

}
