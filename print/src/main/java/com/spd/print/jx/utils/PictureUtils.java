package com.spd.print.jx.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.utils.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 图片工具类
 *
 * @author zzc
 */
public class PictureUtils {

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

    public static Bitmap compress(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = 8;
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
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

    /**
     * 生成QR_CODE类型二维码图片
     *
     * @param str    内容
     * @param param1 宽度
     * @param param2 高度
     * @return
     */
    public static Bitmap createBitmapQrCode(String str, int param1, int param2) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, param1, param2);
            int width = matrix.width;
            int height = matrix.height;
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    if (matrix.get(x, y)) {
                        // black pixel
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        // white pixel
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, width, 0, 0, width, height);
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
