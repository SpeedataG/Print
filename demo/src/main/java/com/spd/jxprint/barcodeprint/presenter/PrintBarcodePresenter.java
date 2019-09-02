package com.spd.jxprint.barcodeprint.presenter;

import android.widget.Toast;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;
import com.spd.lib.mvp.BasePresenter;
import com.spd.jxprint.application.BaseApp;
import com.spd.jxprint.barcodeprint.PrintBarcodeActivity;
import com.spd.jxprint.barcodeprint.contract.PrintBarcodeContract;
import com.spd.jxprint.barcodeprint.model.PrintBarcodeModel;
import com.spd.jxprint.utils.IsChineseOrNot;

import java.io.UnsupportedEncodingException;

/**
 * @author zzc
 */
public class PrintBarcodePresenter extends BasePresenter<PrintBarcodeActivity, PrintBarcodeModel> implements PrintBarcodeContract.Presenter {
    @Override
    protected PrintBarcodeModel createModel() {
        return new PrintBarcodeModel();
    }

    public void printBarcodeEx() {
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        Barcode barcode1 = new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 150, 2, "(10)CEDIS-1");
        BaseApp.getPrinterImpl().printBarCode(barcode1);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
    }

    public void printQrCodeEx() {
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        Barcode barcode2 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6, "123456");
        BaseApp.getPrinterImpl().printBarCode(barcode2);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
    }

    public void printScanTest(int barcodeType, String barcodeContent) {
        boolean isCn = false;
        String utfStr = "";
        String gbStr = "";
        try {
            utfStr = new String(barcodeContent.getBytes("ISO-8859-1"), "UTF-8");
            isCn = IsChineseOrNot.isChineseCharacter(utfStr);
            //防止有人特意使用乱码来生成二维码来判断的情况
            boolean b = IsChineseOrNot.isSpecialCharacter(barcodeContent);
            if (b) {
                isCn = true;
            }
            System.out.println("是为:" + isCn);
            if (!isCn) {
                gbStr = new String(barcodeContent.getBytes("ISO-8859-1"), "GB2312");
                System.out.println("这是转了GB2312的" + gbStr);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (isCn) {
            barcodeContent = utfStr;
        } else {
            barcodeContent = gbStr;
        }
        final String content = barcodeContent;
        if (barcodeType == 1) {
            Toast.makeText(getView(), "一维码：" + barcodeContent, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getView(), "二维码：" + barcodeContent, Toast.LENGTH_SHORT).show();
        }
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        // 判端条码类型
        switch (barcodeType) {
            case 1:
                Barcode barcode1 = new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 150, 2, content);
                BaseApp.getPrinterImpl().printBarCode(barcode1);
                break;
            case 2:
                Barcode barcode2 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6, content);
                BaseApp.getPrinterImpl().printBarCode(barcode2);
                break;
            case 3:
                Barcode barcode3 = new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 150, 2, content);
                BaseApp.getPrinterImpl().printBarCode(barcode3);
                break;
            default:
                break;
        }
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
    }
}
