package com.spd.jxprint.pictureprint.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.printer.sdk.PrinterConstants;
import com.printer.sdk.monochrome.BitmapConvertor;
import com.spd.jxprint.R;
import com.spd.jxprint.application.BaseApp;
import com.spd.jxprint.pictureprint.PrintPictureActivity;
import com.spd.jxprint.pictureprint.contract.PrintPictureContract;
import com.spd.jxprint.pictureprint.model.PrintPictureModel;
import com.spd.jxprint.utils.CanvasUtils;
import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.utils.PictureUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * @author zzc
 */
public class PrintPicturePresenter extends BasePresenter<PrintPictureActivity, PrintPictureModel> implements PrintPictureContract.Presenter {

    private BitmapConvertor convertor = new BitmapConvertor(getView());

    @Override
    protected PrintPictureModel createModel() {
        return new PrintPictureModel();
    }

    public void printPresetBitmap() {
        getView().progressDialogShow();
        PrintBitmapThread thread = new PrintBitmapThread();
        thread.start();
    }

    public void printCanvas() {
        getView().progressDialogShow();
        PrintCanvasThread thread = new PrintCanvasThread();
        thread.start();
    }

    public void printLocalPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getView().startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            // 这个方法是根据Uri获取Bitmap图片的静态方法
            Uri mImageCaptureUri = Objects.requireNonNull(data).getData();
            try {
                // 选择相册
                Bitmap photoBitmap2 = MediaStore.Images.Media.getBitmap(getView().getContentResolver(), mImageCaptureUri);
                new ConvertInBackground().execute(photoBitmap2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ConvertInBackground extends AsyncTask<Bitmap, String, Void> {
        Bitmap monoChromeBitmap = null;

        @Override
        protected Void doInBackground(Bitmap... params) {
            Bitmap compress = PictureUtils.compress(params[0]);
            monoChromeBitmap = convertor.convertBitmap(compress);
            if (monoChromeBitmap == null) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            PictureUtils.printBitmapImage(BaseApp.getPrinterImpl().getPrinter(), monoChromeBitmap, PrinterConstants.PAlign.NONE, 0, false);
            getView().progressDialogDismiss();
        }

        @Override
        protected void onPreExecute() {
            getView().progressDialogShow();
        }
    }

    private class PrintBitmapThread extends Thread {
        @Override
        public void run() {
            super.run();
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
            getView().progressDialogDismiss();
        }
    }

    private class PrintCanvasThread extends Thread {
        @Override
        public void run() {
            super.run();
            new CanvasUtils().printCustomImage(getView().getResources(), BaseApp.getPrinterImpl().getPrinter());
            getView().progressDialogDismiss();
        }
    }

}
