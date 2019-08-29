package com.spd.print.jx.pictureprint.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.printer.sdk.PrinterConstants;
import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.R;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.pictureprint.PrintPictureActivity;
import com.spd.print.jx.pictureprint.contract.PrintPictureContract;
import com.spd.print.jx.pictureprint.model.PrintPictureModel;
import com.spd.print.jx.utils.CanvasUtils;
import com.spd.print.jx.utils.PictureUtils;

/**
 * @author zzc
 */
public class PrintPicturePresenter extends BasePresenter<PrintPictureActivity, PrintPictureModel> implements PrintPictureContract.Presenter {
    @Override
    protected PrintPictureModel createModel() {
        return new PrintPictureModel();
    }

    public void printPresetBitmap() {
        BaseApp.getPrinterImpl().initPrinter();
        BaseApp.getPrinterImpl().printText("\n");
        BaseApp.getPrinterImpl().printText(getView().getString(R.string.print_bitmap_test));
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getView().getResources(), R.drawable.my_monochrome_image, bfoOptions);
        Matrix matrix = new Matrix();
        //matrix.setScale(X轴缩放,Y轴缩放，，);后面两个参数是相对于缩放的位置放置，尝试设置，建议数值>100以上进行设置
        matrix.setScale(1f, 1f);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        PictureUtils.printBitmapImage(BaseApp.getPrinterImpl().getPrinter(), resizeBmp, PrinterConstants.PAlign.NONE, 0, false);
        BaseApp.getPrinterImpl().initPrinter();
        BaseApp.getPrinterImpl().printText("\n");
        BaseApp.getPrinterImpl().printText(getView().getString(R.string.print_bitmap_end_test));
        BaseApp.getPrinterImpl().printText("\n");
    }

    public void printCanvas() {
        new CanvasUtils().printCustomImage(getView().getResources(), BaseApp.getPrinterImpl().getPrinter());
    }

    public void printLocalPhoto() {

    }
}
