package com.spd.print.jx.setting.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.printer.sdk.PrinterConstants;
import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.setting.PrintSettingActivity;
import com.spd.print.jx.setting.contract.PrintSettingContract;
import com.spd.print.jx.setting.model.PrintSettingModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author :Reginer in  2019/8/21 12:10.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class PrintSettingPresenter extends BasePresenter<PrintSettingActivity, PrintSettingModel> implements PrintSettingContract.Presenter {
    @Override
    protected PrintSettingModel createModel() {
        return new PrintSettingModel();
    }

    @Override
    public void systemUpdate() {
        //开始升级
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                InputStream in = null;
                //创建文件夹
                File f = new File("/sdcard/Android/data/updata");
                if (!f.exists()) {
                    f.mkdir();
                }
                //复制升级文件到指定目录
                copyFilesFromassets(getView(), "T581U0.73-V0.16-sbtV05.bin", "/sdcard/Android/data/updata/T581U0.73-V0.16-sbtV05.bin");
                //获取升级文件
                File fileParent = new File("/sdcard/Android/data/updata/T581U0.73-V0.16-sbtV05.bin");
                try {
                    in = new FileInputStream(fileParent);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    getView().onUpdateError(e);
                }
                int a = 0;
                a = BaseApp.getPrinterImpl().update(in);
                if (a == -2) {
                    getView().onUpdateSuccess();
                } else {
                    getView().onUpdateError(null);
                }
                Looper.loop();
            }
        }).start();
    }

    public void setPaperFeed() {
        BaseApp.getPrinterImpl().setPaperFeed(2);
    }

    public void setPaperBack() {
        BaseApp.getPrinterImpl().setPaperBack(2);
    }

    /**
     * 打印普通热敏纸示例
     *
     * @param text
     */
    public void printNormalTest(String text) {
        BaseApp.getPrinterImpl().initPrinter();
        BaseApp.getPrinterImpl().printText(text);
        BaseApp.getPrinterImpl().setFont(0, 0, 0, 0, 0);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, 0);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
    }

    /**
     * 打印标签纸示例
     *
     * @param text
     */
    public void printLabelTest(String text) {
        BaseApp.getPrinterImpl().initPrinter();
        byte[] backBytes = new byte[]{0x1B, 0x4B, (byte) (10 * 8), 0x1B, 0x4A, (byte) 8};
        BaseApp.getPrinterImpl().sendBytesData(backBytes);
        BaseApp.getPrinterImpl().initPrinter();
        BaseApp.getPrinterImpl().printText(text);
        BaseApp.getPrinterImpl().setFont(0, 0, 0, 0, 0);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, 0);
        BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        byte[] bytes = new byte[]{0x0C};
        BaseApp.getPrinterImpl().sendBytesData(bytes);
    }

    /**
     * 打印自检页
     */
    public void printSelfCheck() {
        byte[] selfCheck = new byte[]{0x1d, 0x28, 0x41, 0x02, 0x00, 0x00, 0x02};
        BaseApp.getPrinterImpl().sendBytesData(selfCheck);
    }

    private void copyFilesFromassets(Context context, String oldPath, String newPath) {
        try {
            // 获取assets目录下的所有文件及目录名
            String fileNames[] = context.getAssets().list(oldPath);
            if (Objects.requireNonNull(fileNames).length > 0) {
                // 如果是目录
                File file = new File(newPath);
                file.mkdirs();
                // 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFromassets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                // 循环从输入流读取
                while ((byteCount = is.read(buffer)) != -1) {
                    // buffer字节
                    // 将读取的输入流写入到输出流
                    fos.write(buffer, 0, byteCount);
                }
                // 刷新缓冲区
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}