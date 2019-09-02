package com.spd.jxprint.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.printer.sdk.Barcode;
import com.printer.sdk.CanvasPrint;
import com.printer.sdk.FontProperty;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterConstants.Command;
import com.printer.sdk.PrinterConstants.PAlign;
import com.printer.sdk.PrinterConstants.PrinterType;
import com.printer.sdk.PrinterInstance;
import com.spd.jxprint.R;
import com.spd.print.jx.utils.PictureUtils;

/**
 * @author zzc
 */
public class CanvasUtils {

    /**
     * 画布模版   图片跟文字并行
     *
     * @param resources -
     * @param mPrinter  -
     */
    public void printCustomImage(Resources resources, PrinterInstance mPrinter) {

        Bitmap phone = BitmapFactory.decodeResource(resources, R.drawable.phone);
        Bitmap mark = BitmapFactory.decodeResource(resources, R.drawable.mark);
        //创建画布
        CanvasPrint cp = new CanvasPrint();
        //初始化画布
        cp.init(PrinterType.M21);
        //创建字体
        FontProperty fp = new FontProperty();
        //字体属性赋值 此处参数个数根据SDK版本不同，有略微差别，酌情增减。
        fp.setFont(true, false, false, false, 24, null);
        //设置字体
        cp.setFontProperty(fp);
        cp.drawImage(0, 0, phone);
        //将文字画到画布上指定坐标处
        cp.drawText(100, 50, "0760-122312455");
        cp.drawImage(0, 100, mark);
        cp.drawText(100, 150, "中山市沙朗肉联厂A15卡");
        cp.drawText(0, 200, "\n");
        CanvasPrint cp1 = new CanvasPrint();
        cp1.init(PrinterType.M21);
        cp1.setFontProperty(fp);
        cp1.drawImage(0, 0, phone);
        cp1.drawText(100, 50, "小红");
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
        mPrinter.setFont(0, 1, 1, 0, 0);
        mPrinter.printText("中山市肉联厂秋季批发行" + "\n");
        mPrinter.setFont(0, 0, 0, 0, 0);
        //将画布保存成图片并进行打印
        PictureUtils.printBitmapImage(mPrinter, cp.getCanvasImage(), PAlign.NONE, 0, false);
        mPrinter.printText("————————————————" + "\n");
        PictureUtils.printBitmapImage(mPrinter, cp1.getCanvasImage(), PAlign.NONE, 0, false);
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
        mPrinter.printText("\n");
        mPrinter.printText("订单号      384279857092u938u2" + "\n");
        mPrinter.printText("时间          2019-07-21 17:20" + "\n");
        mPrinter.printText("————————————————" + "\n");
        mPrinter.printText("商品  |  重量  |  价格  |  金额" + "\n");
        mPrinter.printText("————————————————" + "\n");
        mPrinter.setFont(0, 0, 0, 1, 0);
        mPrinter.printText("猪类1   20公斤    50     10000" + "\n");
        mPrinter.printText("猪类1   20公斤    50     10000" + "\n");
        mPrinter.printText("猪类1   20公斤    50     10000" + "\n");
        mPrinter.setFont(0, 0, 0, 0, 0);
        mPrinter.printText("————————————————" + "\n");
        mPrinter.printText("总金额                   30000" + "\n");
        mPrinter.printText("————————————————" + "\n\n");
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6,
                "123456");
        mPrinter.printBarCode(barcode);
        mPrinter.printText("该技术由xxx公司提供" + "\n\n\n\n");
    }

}
