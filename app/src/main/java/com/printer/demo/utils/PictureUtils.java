package com.printer.demo.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.util.Log;

import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.utils.Utils;

public class PictureUtils {

    private DeviceControl deviceControl;

    public static Bitmap compress(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = 800f;
        float ww = 480f;
        int be = 1;

        be = (int) (newOpts.outWidth / ww);
        Log.i("fdh", "w:" + w + "h:" + h + "   newOpts.outWidth:" + newOpts.outWidth + "ww:" + ww + "be:" + be);

        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = 8;
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        Log.i("fdh", "inSampleSize:" + newOpts.inSampleSize);
        Log.i("fdh", "heightUtils:" + bitmap.getHeight() + "----" + "widthUtils:" + bitmap.getWidth());

        return compressImage(bitmap);
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public static void printBitmapImage(PrinterInstance mPrinter, Bitmap bitmap, PrinterConstants.PAlign alignType, int left, boolean isCompressed) {
        byte[] bmp;
        InputStream statusFile;
        if (isCompressed) {
            bmp = Utils.compressBitmap2PrinterBytes(bitmap, alignType, left);
        } else {
            bmp = Utils.bitmap2PrinterBytes(bitmap, alignType, left);
        }
        int n = bmp.length / 1024;
        Log.d("zzc", "====bmp.length====" + n + "====" + bmp.length);
        int i;
        for (i = 0; i < n; i++) {
            try {
                String statusFileStr;
                String[] str;
                do {
                    statusFile = new FileInputStream("/sys/bus/platform/drivers/mediatek-pinctrl/10005000.pinctrl/mt_gpio");
                    statusFileStr = convertStreamToString(statusFile);
                    str = statusFileStr.split("46: ");
                    str[1] = str[1].substring(3, 4);
                    Log.d("zzc", "====statusFileStr:====" + i + "====" + str[1]);
                } while ("1".equals(str[1]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] subBmp = new byte[1024];
            System.arraycopy(bmp, i * 1024, subBmp, 0, 1024);
            mPrinter.sendBytesData(subBmp);
        }
        try {
            String statusFileStr;
            String[] str;
            do {
                statusFile = new FileInputStream("/sys/bus/platform/drivers/mediatek-pinctrl/10005000.pinctrl/mt_gpio");
                statusFileStr = convertStreamToString(statusFile);
                str = statusFileStr.split("46: ");
                str[1] = str[1].substring(3, 4);
                Log.d("zzc", "====statusFileStr:====" + i + "====" + str[1]);
            } while ("1".equals(str[1]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] endBmp = new byte[1024];
        System.arraycopy(bmp, n * 1024, endBmp, 0, bmp.length - n * 1024);
        mPrinter.sendBytesData(endBmp);
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
