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
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author :zzc in  2020/8/27 14:53 update.
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
     * 关闭连接
     */
    void closeConnect();

    /**
     * 向打印机发送数据
     *
     * @param srcData 要发送的byte数组
     * @return <p>
     * >0 成功发送到打印机的字节数
     * -1 未初始化打印
     * -2 srcData 为空或者srcData 里没有数据。
     * </p>
     */
    int sendBytesData(byte[] srcData);

    /**
     * 读取打印机返回的数据
     *
     * @param buffer 用于接收读到字节的数组
     * @return <p>
     * >0 成功读到的字节数
     * -1 未初始化打印
     * -2 srcData 为空或者 srcData 里没有数据。
     * </p>
     */
    int read(byte[] buffer);

    /**
     * <p>
     * 初始化打印机
     * 可以清除缓存
     * </p>
     */
    void initPrinter();

    /**
     * 设置打印机字体
     *
     * @param mCharacterType 0 表示 12*24 字体大小，1 表示 9*16 字体大小，此设置临时有效
     * @param mWidth         倍宽，范围 0~7
     * @param mHeight        倍高，范围 0~7
     * @param mBold          0 不加粗，1 加粗
     * @param mUnderline     0 无 下划线，1 下划线
     */
    void setFont(int mCharacterType, int mWidth, int mHeight, int mBold, int mUnderline);

    /**
     * 设置打印机打印
     *
     * @param command <p>
     *                PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LNCH 打印并走纸 value 点行
     *                PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE 打印并走纸 value 字符行
     *                PrinterConstants.Command.ALIGN 设置打印内容位置，Value 值可指定设置的具体位置
     *                </p>
     * @param value   <p>
     *                Value 值可指定设置的具体位置
     *                PrinterConstants.Command.ALIGN_LEFT;
     *                PrinterConstants.Command.ALIGN_CENTER;
     *                PrinterConstants.Command.ALIGN_RIGHT
     *                </p>
     */
    void setPrinter(int command, int value);

    /**
     * 设置纸类型
     *
     * @param paperType 纸类型
     * @return <p>
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
     * 设置打印速度
     *
     * @param speed 默认4
     *              4---47mm/s
     *              3---45.2mm/s
     *              2---40.8mm/s
     *              1---36.4mm/s
     *              0---33mm/s
     */
    void setSpeed(int speed);

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
     * 打印条码
     *
     * @param barcodeType 条码类型
     * @param param1      一维条码：横向宽度2<=n<=6
     * @param param2      一维条码：条码高度1<=n<=255
     * @param param3      一维条码：条码注释位置，0 不打印，1 上方，2 下方，3 上下方均有
     * @param content     条码数据
     * @return 二维条码具体参考api文档
     */
    int printBarCode(byte barcodeType, int param1, int param2, int param3, String content);

    /**
     * 打印图片
     *
     * @param bitmap       单色位图
     * @param alignType    -
     * @param left         -
     * @param isCompressed 是否压缩
     */
    void printImage(Bitmap bitmap, int alignType, int left, boolean isCompressed);

    /**
     * 数据分包打印 适合大图片打印
     *
     * @param bitmap       单色位图
     * @param alignType    对齐方式 0-start,1-center,2-end,3-none
     * @param left         偏移 alignType为NONE时有效
     * @param isCompressed 是否压缩
     */
    void printBigImage(Bitmap bitmap, int alignType, int left, boolean isCompressed);

    /**
     * 打印表格
     *
     * @param table 表格
     */
    void printTable(Table table);

    /**
     * 打印表格
     *
     * @param column            表头，列名，以regularExpression字符分隔
     * @param regularExpression 表内分隔符
     * @param columnWidth       每列列宽，字符个数
     * @param rows              list集合每一条数据代表一行（格式与表头一致）
     */
    void printTable(String column, String regularExpression, int[] columnWidth, ArrayList<String> rows);

    /**
     * 升级sdk
     *
     * @param inputStream   升级文件
     * @param hexFileLength 文件大小
     * @return -2 成功
     */
    int update(InputStream inputStream, String hexFileLength);

    /**
     * 设置灵敏度
     *
     * @param sensitivity 电压值
     */
    void setSensitivity(int sensitivity);

    /**
     * 设置出纸距离
     *
     * @param len 距离 (mm)
     */
    void setOutPaperLen(int len);

    /**
     * 定位
     */
    void searchGap();

    /**
     * 设置打印机参数
     *
     * @param params {@link com.spd.print.jx.constant.ParamsConstant}
     *               可以多个设置指令一起设置
     */
    void setAllParams(byte[]... params);

    /**
     * 获取打印机状态
     *
     * @return 0-正常、1-缺纸、3-通讯异常
     */
    int getPrinterStatus();

    /**
     * 获取打印浓度
     *
     * @return 0-4
     */
    int getPrintDensity();

    /**
     * 获取纸类型
     *
     * @return 0-普通纸 1-标签纸 2-黑标纸
     */
    int getPaperType();

    /**
     * 获取灵敏度
     *
     * @return 1-33
     */
    int getPaperSensitivity();

}
