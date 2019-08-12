package com.printer.demo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.serialport.DeviceControl;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.demo.global.GlobalContants;
import com.printer.demo.utils.PrefUtils;
import com.printer.demo.utils.XTUtils;
import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterConstants.Connect;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.usb.USBPort;
import com.speedata.libutils.SharedXmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SettingActivity extends BaseActivity implements OnClickListener,
        OnItemSelectedListener, OnCheckedChangeListener {
    private LinearLayout header;
    private Spinner spinnerSetConcentration, spinnerPaperType;
    private ArrayAdapter<CharSequence> printConcentrationAdapter, paperTypeAdapter;
    protected static final String TAG = "SettingActivity";
    private static Button btn_search_devices, btn_selfprint_test,
            btn_update, btnLabelPrint, btnSetConcentration,
            btnSetType, btnPaperOut, btnPaperBack, btnNormalPrint;
    // 连接状态
    public static boolean isConnected = false;
    public static String devicesName = "未知设备";
    private static String devicesAddress;
    public static PrinterInstance myPrinter;
    private Context mContext;
    private RadioGroup rg__select_paper_size;
    private TextView tv_device_name, tv_printer_address;
    private DeviceControl deviceControl;

    /**
     * 显示扫描结果
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
        try {
            deviceControl = new DeviceControl(DeviceControl.PowerType.NEW_MAIN, 8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "" + SettingActivity.this);
    }

    // 用于接受连接状态消息的 Handler
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Connect.SUCCESS:
                    isConnected = true;
                    GlobalContants.ISCONNECTED = isConnected;
                    GlobalContants.DEVICENAME = devicesName;
                    break;
                case Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(mContext, R.string.conn_failed,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "连接失败!");
                    break;
                case Connect.CLOSED:
                    isConnected = false;
                    GlobalContants.ISCONNECTED = isConnected;
                    GlobalContants.DEVICENAME = devicesName;
                    Toast.makeText(mContext, R.string.conn_closed,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "连接关闭!");
                    break;
                case Connect.NODEVICE:
                    isConnected = false;
                    Toast.makeText(mContext, R.string.conn_no, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 0:
                    Toast.makeText(mContext, "打印机通信正常!", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mContext, "打印机通信异常常，请检查蓝牙连接!", Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                case -2:
                    Toast.makeText(mContext, "打印机缺纸!", Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                case -3:
                    Toast.makeText(mContext, "打印机开盖!", Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                default:
                    break;
            }
            updateButtonState(isConnected);
        }

    };
    int count = 0;

    public void vibrator() {
        count++;
        PrefUtils.setInt(mContext, "count3", count);
        Log.e(TAG, "" + count);
        MediaPlayer player = new MediaPlayer().create(mContext, R.raw.test);
        MediaPlayer player2 = new MediaPlayer().create(mContext, R.raw.beep);

        player.start();
        player2.start();
    }

    /**
     * 初始化界面
     */
    private void init() {
        mContext = SettingActivity.this;
        header = (LinearLayout) findViewById(R.id.ll_headerview_settingactivity);
        // 初始化标题
        initHeader();

        // 初始化按钮的点击事件
        btn_search_devices = (Button) findViewById(R.id.btn_search_devices);
        btn_selfprint_test = (Button) findViewById(R.id.btn_selfprint_test);
        btn_update = (Button) findViewById(R.id.btn_Update);
        // 设置按钮的监听事件
        btn_search_devices.setOnClickListener(this);
        btn_selfprint_test.setOnClickListener(this);
        btn_update.setOnClickListener(this);

        //标签纸打印测试
        btnLabelPrint = findViewById(R.id.btn_label_print);
        btnLabelPrint.setOnClickListener(this);
        //普通纸测试
        btnNormalPrint = findViewById(R.id.btn_normal_print);
        btnNormalPrint.setOnClickListener(this);

        //走纸 退纸
        btnPaperOut = findViewById(R.id.btn_paper_out);
        btnPaperBack = findViewById(R.id.btn_paper_back);
        btnPaperOut.setOnClickListener(this);
        btnPaperBack.setOnClickListener(this);

        //设置纸类型
        spinnerPaperType = findViewById(R.id.spinner_paper_type);
        paperTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.paper_type, android.R.layout.simple_spinner_item);
        paperTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaperType.setAdapter(paperTypeAdapter);
        spinnerPaperType.setOnItemSelectedListener(this);
        btnSetType = findViewById(R.id.btn_set_type);
        btnSetType.setOnClickListener(this);
        //设置浓度
        spinnerSetConcentration = findViewById(R.id.spinner_set_concentration);
        printConcentrationAdapter = ArrayAdapter.createFromResource(this,
                R.array.set_concentration, android.R.layout.simple_spinner_item);
        printConcentrationAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSetConcentration.setAdapter(printConcentrationAdapter);
        spinnerSetConcentration.setOnItemSelectedListener(this);
        btnSetConcentration = findViewById(R.id.btn_set_concentration);
        btnSetConcentration.setOnClickListener(this);

        //展示设备名和设备地址
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        tv_printer_address = (TextView) findViewById(R.id.tv_printer_address);

        rg__select_paper_size = (RadioGroup) findViewById(R.id.rg__select_paper_size);
        rg__select_paper_size.setOnCheckedChangeListener(this);

        getSaveState();
        updateButtonState(isConnected);

        PrinterConstants.paperWidth = 384;
        PrefUtils.setInt(mContext, GlobalContants.PAPERWIDTH, 384);
        int pos = SharedXmlUtil.getInstance(this, "print_type").read("type", 0);
        spinnerPaperType.setSelection(pos);
        switch (pos) {
            case 0:
                btnLabelPrint.setEnabled(false);
                btnNormalPrint.setEnabled(true);
                break;
            case 1:
                btnLabelPrint.setEnabled(true);
                btnNormalPrint.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void getSaveState() {
        PrefUtils.setInt(mContext, GlobalContants.INTERFACETYPE, 3);
        isConnected = PrefUtils.getBoolean(SettingActivity.this,
                GlobalContants.CONNECTSTATE, false);
        Log.i(TAG, "isConnected:" + isConnected);
    }

    /**
     * 初始化标题上的信息
     */
    private void initHeader() {
        // 初始化了
        setHeaderLeftImage(header, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderCenterText(header, getString(R.string.headview_setting));
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestory()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }


    private boolean isStart = false;
    private Timer timer = null;

    private void start(int way) {
        if (timer == null) {
            timer = new Timer();
            if (way == 2) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        PrinterInstance mPrinter = PrinterInstance.mPrinter;
                        XTUtils.printNote(getResources(), mPrinter);
                    }
                }, 0, 5000);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btn_search_devices) {
            Log.i(TAG, "isConnected:" + isConnected);
            if (!isConnected) {
                //上电
                try {
                    if (deviceControl != null) {
                        deviceControl.PowerOnDevice();
                        deviceControl.newSetDir(46, 0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 串口
                int baudrate = 115200;
                String path = "/dev/ttyMT0";
                devicesName = "Serial device";
                devicesAddress = path;
                myPrinter = PrinterInstance.getPrinterInstance(new File(path),
                        baudrate, 0, mHandler);
                myPrinter.openConnection();
                Log.i(TAG, "波特率:" + baudrate + "路径:" + path);
                startCheckStatus(mContext, myPrinter);

            } else {
                if (myPrinter != null) {
                    myPrinter.closeConnection();
                    myPrinter = null;
                    isConnected = false;
                    updateButtonState(isConnected);
                    Log.i(TAG, "已经断开");
                }
                //下电
                if (deviceControl != null) {
                    try {
                        deviceControl.PowerOffDevice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                stopCheckStatus();
            }
        }

        if (v == btnLabelPrint) {
            if (isConnected) {
                XTUtils.printLabelTest(getResources(), myPrinter);
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnNormalPrint) {
            if (isConnected) {
                XTUtils.printNormalTest(getResources(), myPrinter);
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btn_selfprint_test) {
            if (isConnected) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XTUtils.printSelfCheck(myPrinter);
                    }
                }).start();
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnSetType) {
            if (isConnected) {
                int position = spinnerPaperType.getSelectedItemPosition();
                SharedXmlUtil.getInstance(this, "print_type").write("type", position);
                switch (position) {
                    case 0:
                        btnLabelPrint.setEnabled(false);
                        btnNormalPrint.setEnabled(true);
                        startCheckStatus(mContext, myPrinter);
                        break;
                    case 1:
                        btnLabelPrint.setEnabled(true);
                        btnNormalPrint.setEnabled(false);
                        stopCheckStatus();
                        break;
                    default:
                        break;
                }
                byte n = (byte) position;
                XTUtils.setPaperType(myPrinter, n);
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnSetConcentration) {
            if (isConnected) {
                int position = spinnerSetConcentration.getSelectedItemPosition();
                byte n = (byte) position;
                XTUtils.setConcentration(myPrinter, n);
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }

        if (v == btnPaperOut) {
            if (isConnected) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XTUtils.setOutPaper(myPrinter, 1);
                    }
                }).start();
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnPaperBack) {
            if (isConnected) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XTUtils.setBackPaper(myPrinter, 1);
                    }
                }).start();
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btn_update) {
            if (isConnected) {
                stopCheckStatus();
                showConnectingDialog();
                new updateThread().start();
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ProgressDialog connectingDialog;

    private void showConnectingDialog() {
        try {
            connectingDialog = new ProgressDialog(this);
            connectingDialog.setTitle("正在升级打印机...");
            connectingDialog.setMessage("正在升级...");
            connectingDialog.setCancelable(false);
            connectingDialog.setCanceledOnTouchOutside(false);
            connectingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissConnectingDialog() {
        try {
            if (connectingDialog != null && connectingDialog.isShowing()
                    && !isFinishing()) {
                connectingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class updateThread extends Thread {
        public updateThread() {
        }

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
            copyFilesFromassets(SettingActivity.this, "T581U0.73-V0.16-sbtV05.bin", "/sdcard/Android/data/updata/T581U0.73-V0.16-sbtV05.bin");
            //获取升级文件
            File fileParent = new File("/sdcard/Android/data/updata/T581U0.73-V0.16-sbtV05.bin");
            try {
                in = new FileInputStream(fileParent);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int a = 0;
            a = XTUtils.update(getResources(), myPrinter, in);
            if (a == -2) {
                dismissConnectingDialog();
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("提示")
                        .setMessage("升级完成，请重启打印机！")
                        .setPositiveButton("确定", null).show();
            } else {
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("提示")
                        .setMessage("升级失败，请重启打印机。")
                        .setPositiveButton("确定", null).show();
            }
            Looper.loop();
        }
    }

    private void updateButtonState(boolean isConnected) {
        if (isConnected) {
            headerConnecedState.setText(R.string.on_line);
            btn_search_devices.setText(R.string.disconnect);
            setTitleState(mContext.getResources().getString(R.string.on_line));
            Log.i("fdh", getString(R.string.printerName).split(":")[0]);
            Log.i("fdh", getString(R.string.printerAddress).split(":")[0]);
            tv_device_name.setText(getString(R.string.printerName).split(":")[0] + ": " + devicesName);
            tv_printer_address.setText(getString(R.string.printerAddress).split(":")[0] + ": " + devicesAddress);
        } else {
            btn_search_devices.setText(R.string.connect);
            header.getShowDividers();
            headerConnecedState.setText(R.string.off_line);
            setTitleState(mContext.getResources().getString(R.string.off_line));
            tv_device_name.setText(getString(R.string.printerName));
            tv_printer_address.setText(getString(R.string.printerAddress));
        }
        //  存储连接状态
        PrefUtils.setBoolean(SettingActivity.this, GlobalContants.CONNECTSTATE,
                isConnected);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        Log.i(TAG, "position:" + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_58mm:
                PrinterConstants.paperWidth = 384;
                break;
            default:
                PrinterConstants.paperWidth = 384;
                break;
        }
        PrefUtils.setInt(mContext, GlobalContants.PAPERWIDTH, PrinterConstants.paperWidth);
    }

    private void copyFilesFromassets(Context context, String oldPath, String newPath) {
        try {
            // 获取assets目录下的所有文件及目录名
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
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
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // 如果捕捉到错误则通知UI线程
            // MainActivity.handler.sendEmptyMessage(COPY_FALSE);
        }
    }
}
