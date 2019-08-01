package com.printer.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.printer.demo.utils.PrintLabel100;
import com.printer.demo.utils.PrintLabel58;
import com.printer.demo.utils.PrintLabel80;
import com.printer.sdk.PrinterInstance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LablePrintActivity extends BaseActivity implements OnClickListener {
    private Button btnPrintLable100mm;
    private Button btnPrintLable80mm;
    private Button btnPrintLable50mm;
    private Button btnPrintLable;
    private LinearLayout header;
    public static final int PRINT_START = 0x1557; // 15:57
    public static final int PRINT_DONE = 0x1558;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.CHINESE);

    private Context mContext;

    protected Handler printerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {

            return false;
        }
    });

    PrinterInstance mPritner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lable_print);
        header = (LinearLayout) findViewById(R.id.ll_headerview_LablePrintactivity);
        btnPrintLable100mm = (Button) findViewById(R.id.btn_100mm);
        btnPrintLable80mm = (Button) findViewById(R.id.btn_80mm);
        btnPrintLable50mm = (Button) findViewById(R.id.btn_58mm);
        btnPrintLable = (Button) findViewById(R.id.btn_lable);
        btnPrintLable100mm.setOnClickListener(this);
        btnPrintLable80mm.setOnClickListener(this);
        btnPrintLable50mm.setOnClickListener(this);
        btnPrintLable.setOnClickListener(this);
        mPritner = PrinterInstance.mPrinter;
        initHeader();
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化标题上的信息
     */
    private void initHeader() {
        setHeaderLeftText(header, getString(R.string.back),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();

                    }
                });
        setHeaderLeftImage(header, new OnClickListener() {// 初始化了
            // headerConnecedState
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        headerConnecedState.setText(getTitleState());
        setHeaderCenterText(header, getString(R.string.headview_LablePrint));
    }

    @Override
    public void onClick(View v) {
        if (mPritner == null && !SettingActivity.isConnected) {
            Toast.makeText(LablePrintActivity.this, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btn_100mm:

                break;
            case R.id.btn_80mm:

                break;
            case R.id.btn_58mm:

                break;
            case R.id.btn_lable:

                break;
            default:
                break;
        }
    }

    private void print() {
        if (PrinterInstance.mPrinter == null) {
            Toast.makeText(LablePrintActivity.this, "请连接打印机",
                    Toast.LENGTH_SHORT).show();
        } else {
            new PrintThread().start();
        }
    }

    private class PrintThread extends Thread {
        String codeStr, destinationStr, countStr, weightStr, volumeStr,
                dispatchModeStr, businessModeStr, packModeStr, receiverAddress;

        public PrintThread() {
            codeStr = "DF1234567890";
            destinationStr = "西安长线";
            countStr = "1";
            weightStr = "2";
            volumeStr = "1";
            dispatchModeStr = "派送";
            businessModeStr = "定时达";
            packModeStr = "袋装";
            receiverAddress = "陕西省西安市临潼区秦始皇陵兵马俑一号坑五排三列俑";
        }

        @Override
        public void run() {
            Looper.prepare();
            try {
                printerHandler.obtainMessage(PRINT_START).sendToTarget();
                printing(codeStr, destinationStr, countStr, weightStr,
                        volumeStr, dispatchModeStr, businessModeStr,
                        packModeStr, receiverAddress);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                printerHandler.obtainMessage(PRINT_DONE).sendToTarget();
            }
            Looper.loop();
        }
    }

    public void printing(String codeStr, String destinationStr,
                         String countStr, String weightStr, String volumeStr,
                         String dispatchModeStr, String businessModeStr, String packModeStr,
                         String receiverAddress) throws Exception {
        String centerName = "西安分拔中心"; // 目的地分拨
        String centerCode = "0292001"; // 目的地分拨编号

        String userSite = "测试二级网点" + "(" + dateFormat.format(new Date()) + ")"; // 出发网点

        int count = Integer.parseInt(countStr);
        for (int c = 1; c <= count; c++) {
            // 子单号
            String serialNum = String.format("%03d", c);
            String subCodeStr = codeStr + serialNum + centerCode;
            String serialStr = "第" + c + "件";
            //
            // LablePrintUtils.doPrint(this, mPritner, codeStr,
            // businessModeStr, centerName, destinationStr, userSite,
            // receiverAddress, countStr, serialStr, dispatchModeStr,
            // packModeStr, subCodeStr);
        }
        // onPrintSucceed();
    }
}
