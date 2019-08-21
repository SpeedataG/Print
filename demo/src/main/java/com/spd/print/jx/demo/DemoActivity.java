package com.spd.print.jx.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.spd.print.jx.constant.PaperConstant;
import com.spd.print.jx.constant.PrintConstant;
import com.spd.print.jx.impl.PrintImpl;
import com.spd.print.jx.inter.IConnectCallback;

/**
 * @author :Reginer in  2019/8/16 11:44.
 * 联系方式:QQ:282921012
 * 功能描述:调用示例，也可以增加单例模式，将mPrintImpl通过单例设置和获取，一次设置全局使用
 */
@SuppressLint("Registered")
public class DemoActivity extends AppCompatActivity implements IConnectCallback {
    private PrintImpl mPrintImpl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrintImpl = new PrintImpl();
        mPrintImpl.connectPrinter(this);
    }

    @Override
    public void onPrinterConnectSuccess() {
        //设置纸类型
//        mPrintImpl.setPaperType(PaperConstant.TAG_PAPER);
        //打印文本1
//        mPrintImpl.printText("你好");
//        //打印文本2
//        mPrintImpl.getPrinter().printText("你好");
    }

    @Override
    public void onPrinterConnectFailed(int errorCode) {
        if (errorCode == PrintConstant.CONNECT_CLOSED) {
            // TODO: 2019/8/16 连接关闭
        }
    }


    private void singleImpl() {
        PrintInstant.getInstance().setPrintImpl(new PrintImpl());
        PrintInstant.getInstance().getPrintImpl().setPaperType(PaperConstant.TAG_PAPER);
    }
}
