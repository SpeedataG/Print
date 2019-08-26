package com.spd.print.jx.setting.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import android.util.Log;

import com.printer.sdk.PrinterConstants;
import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.setting.PrintSettingActivity;
import com.spd.print.jx.setting.contract.PrintSettingContract;
import com.spd.print.jx.setting.model.PrintSettingModel;
import com.spd.print.jx.utils.XTUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author :Reginer in  2019/8/21 12:10.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class PrintSettingPresenter extends BasePresenter<PrintSettingActivity, PrintSettingModel> implements PrintSettingContract.Presenter {
    private Timer timer;

    @Override
    protected PrintSettingModel createModel() {
        return new PrintSettingModel();
    }

    @Override
    public void systemUpdate() {
        //开始升级
        UpdateThread update = new UpdateThread();
        update.start();
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

    /**
     * 设置灵敏度
     *
     * @param sensitivity 灵敏度值
     */
    public void setSensitivity(String sensitivity) {
        if (sensitivity.isEmpty()) {
            int sen = Integer.parseInt(sensitivity);
            BaseApp.getPrinterImpl().sendBytesData(new byte[]{0x1F, 0x11, 0x1F, 0x46, (byte) sen, 0x1F, 0x1F});
        }
    }

    /**
     * 读返回值
     *
     * @return 返回值
     */
    public String readStatus() {
        byte[] result = new byte[6];
        BaseApp.getPrinterImpl().read(result);
        Log.d("zzc", "read====" + Arrays.toString(result));
        return Arrays.toString(result);
    }

    /**
     * 设置浓度
     *
     * @param density 0——5 低到高
     */
    public void setDensity(int density) {
        BaseApp.getPrinterImpl().setDensity(density);
    }

    /**
     * 设置纸类型
     *
     * @param type 类型
     */
    public void setPaperType(int type) {
        BaseApp.getPrinterImpl().setPaperType(type);
    }

    /**
     * 疲劳测试
     */
    public void fatigueTest(final Resources resources) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                XTUtils.printNote(resources, BaseApp.getPrinterImpl());
            }
        }, 0, 30000);
    }

    public void stopFatigueTest() {
        timer.cancel();
        timer = null;
    }

    private class UpdateThread extends Thread {
        @Override
        public void run() {
            super.run();
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
            a = BaseApp.getPrinterImpl().update(in, "35 31 34 30 34");
            if (a == -2) {
                getView().onUpdateSuccess();
            } else {
                getView().onUpdateError(null);
            }
            Looper.loop();
        }
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
