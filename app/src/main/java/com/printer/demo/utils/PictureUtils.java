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
        if (baos.toByteArray().length / 1024 > 1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
            baos.reset();//����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
        float hh = 800f;//�������ø߶�Ϊ800f
        float ww = 480f;//�������ÿ��Ϊ480f
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
        int be = 1;//be=1��ʾ������
//	    if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
//	    }
        be = (int) (newOpts.outWidth / ww);
        Log.i("fdh", "w:" + w + "h:" + h + "   newOpts.outWidth:" + newOpts.outWidth + "ww:" + ww + "be:" + be);

//	    if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
//	        be = (int) (newOpts.outWidth / ww);
//	    } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����
//	        be = (int) (newOpts.outHeight / hh);
//	    }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = 8;//�������ű���
//	    ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        Log.i("fdh", "inSampleSize:" + newOpts.inSampleSize);
        Log.i("fdh", "heightUtils:" + bitmap.getHeight() + "----" + "widthUtils:" + bitmap.getWidth());

        return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
            baos.reset();//����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
            options -= 10;//ÿ�ζ�����10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
        return bitmap;
    }

    public static int printBitmapImage(PrinterInstance mPrinter, Bitmap bitmap, PrinterConstants.PAlign alignType, int left, boolean isCompressed) {
        byte[] bmp;
        InputStream statusFile;
        if (isCompressed) {
            bmp = Utils.compressBitmap2PrinterBytes(bitmap, alignType, left);
        } else {
            bmp = Utils.bitmap2PrinterBytes(bitmap, alignType, left);
        }
        int n = bmp.length / 1024;
        Log.d("zzc", "====bmp.length====" + n + "====" + bmp.length);
        for (int i = 0; i < n; i++) {
            try {
                String statusFileStr;
                String[] str;
                do {
                    statusFile = new FileInputStream("/sys/bus/platform/drivers/mediatek-pinctrl/10005000.pinctrl/mt_gpio");
                    statusFileStr = convertStreamToString(statusFile);
                    str = statusFileStr.split("46: ");
                    str[1] = str[1].substring(3, 4);
                    Log.d("zzc", "====statusFileStr:====" + i + "====" + str[1]);
//                    SystemClock.sleep(500);
                } while ("1".equals(str[1]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] subBmp = new byte[1024];
            System.arraycopy(bmp, i * 1024, subBmp, 0, 1024);
            mPrinter.sendBytesData(subBmp);
        }
        byte[] endBmp = new byte[bmp.length - n * 1024];
        System.arraycopy(bmp, n * 1024, endBmp, 0, bmp.length - n * 1024);
        mPrinter.sendBytesData(endBmp);
        return 0;
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
