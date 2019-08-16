package com.spd.print.jx.ui;

import android.os.Bundle;
import android.serialport.DeviceControl;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.spd.print.jx.R;
import com.spd.print.jx.constant.PaperConstant;
import com.spd.print.jx.constant.PrintConstant;
import com.spd.print.jx.impl.PrintImpl;
import com.spd.print.jx.inter.IConnectCallback;

import java.io.IOException;

/**
 * @author zzc
 */
public class MainActivity extends AppCompatActivity implements IConnectCallback {

    private PrintImpl mPrintImpl;
    private LinearLayout layoutPrintTest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutPrintTest = findViewById(R.id.ll_setting);
        layoutPrintTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrintImpl = new PrintImpl();
                mPrintImpl.connectPrinter(MainActivity.this);
                //设置纸类型
//                mPrintImpl.setPaperType(PaperConstant.NORMAL_PAPER);
            }
        });
        try {
            DeviceControl deviceControl = new DeviceControl(DeviceControl.PowerType.NEW_MAIN, 8);
            deviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrinterConnectSuccess() {
        //打印文本1
        mPrintImpl.printText("你好\n");
        //打印文本2
        mPrintImpl.getPrinter().printText("你好\n");
        Log.d("zzc", "success");
    }

    @Override
    public void onPrinterConnectFailed(int errorCode) {
        Log.d("zzc", "failed:" + errorCode);
        if (errorCode == PrintConstant.CONNECT_CLOSED) {
            // TODO: 2019/8/16 连接关闭
        }
    }
}
