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
    private TextView tvShowPrinterType;
    private TextView tvShowPrinterPortType;
    private Spinner spinner_printer_type;
    private Spinner spinner_interface_type, spinnerSetConcentration, spinnerPaperType;
    private List<String> data_list;
    private ArrayAdapter<CharSequence> arr_adapter;
    private ArrayAdapter<CharSequence> printType_adapter;
    private ArrayAdapter<CharSequence> printConcentrationAdapter, paperTypeAdapter;
    private final static int SCANNIN_GREQUEST_CODE = 2;
    public static final int CONNECT_DEVICE = 1;
    protected static final String TAG = "SettingActivity";
    private static Button btn_search_devices, btn_scan_and_connect, btn_selfprint_test,
            btn_update, btn_getstate, btnForPrint, btnForPrintNote, btnSetVol, btnSetConcentration,
            btnSetType, btnPaperOut, btnPaperBack;
    private EditText etV;
    // 蓝牙连接状态
    public static boolean isConnected = false;
    public static String devicesName = "未知设备";
    private static String devicesAddress;
    private ProgressDialog dialog;
    public static PrinterInstance myPrinter;
    private static BluetoothDevice mDevice;
    private static UsbDevice mUSBDevice;
    private Context mContext;
    private int printerId = 0;
    private int interfaceType = 0;
    private List<UsbDevice> deviceList;
    private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
    private RadioGroup rg__select_paper_size;
    private static boolean isFirst = true;
    boolean isError;
    private BluetoothAdapter mBtAdapter;
    private TextView tv_device_name, tv_printer_address;
    private IntentFilter bluDisconnectFilter;
    private static boolean hasRegDisconnectReceiver = false;
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
        isFirst = false;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Toast.makeText(SettingActivity.this, "SettingActivity start.", Toast.LENGTH_SHORT).show();
    }

    // 用于接受连接状态消息的 Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Connect.SUCCESS:
                    isConnected = true;
                    GlobalContants.ISCONNECTED = isConnected;
                    GlobalContants.DEVICENAME = devicesName;
                    if (interfaceType == 0) {
                        PrefUtils.setString(mContext, GlobalContants.DEVICEADDRESS,
                                devicesAddress);
                        bluDisconnectFilter = new IntentFilter();
                        bluDisconnectFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                        mContext.registerReceiver(myReceiver, bluDisconnectFilter);
                        hasRegDisconnectReceiver = true;
                    }

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

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
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
        // 初始化下拉列表框
        spinner_interface_type = (Spinner) findViewById(R.id.spinner_interface_type);
        // 初始化按钮的点击事件
        btn_search_devices = (Button) findViewById(R.id.btn_search_devices);
        btn_scan_and_connect = (Button) findViewById(R.id.btn_scan_and_connect);
        btn_selfprint_test = (Button) findViewById(R.id.btn_selfprint_test);
        btn_update = (Button) findViewById(R.id.btn_Update);
        btn_getstate = (Button) findViewById(R.id.btn_getstate);
        // 设置按钮的监听事件
        btn_search_devices.setOnClickListener(this);
        btn_scan_and_connect.setOnClickListener(this);
        btn_selfprint_test.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_getstate.setOnClickListener(this);

        //循环打印
        btnForPrint = findViewById(R.id.btn_for_print);
        btnForPrint.setOnClickListener(this);
        //循环打印小票示例
        btnForPrintNote = findViewById(R.id.btn_for_print_note);
        btnForPrintNote.setOnClickListener(this);

        //灵敏度值（电压）
        etV = findViewById(R.id.et_v);
        //设置灵敏度
        btnSetVol = findViewById(R.id.btn_set_vol);
        btnSetVol.setOnClickListener(this);

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
        // 适配器
        printType_adapter = ArrayAdapter.createFromResource(this,
                R.array.interface_type, android.R.layout.simple_spinner_item);

        // 设置样式
        printType_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 加载适配器
        spinner_interface_type.setAdapter(printType_adapter);
        // 下拉列表框的监听事件
        spinner_interface_type.setOnItemSelectedListener(this);

        rg__select_paper_size = (RadioGroup) findViewById(R.id.rg__select_paper_size);
        rg__select_paper_size.setOnCheckedChangeListener(this);

        // 初始化对话框
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getString(R.string.connecting));
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        getSaveState();
        updateButtonState(isConnected);

        PrinterConstants.paperWidth = 384;
        PrefUtils.setInt(mContext, GlobalContants.PAPERWIDTH, 384);

    }

    private void getSaveState() {
        isConnected = PrefUtils.getBoolean(SettingActivity.this,
                GlobalContants.CONNECTSTATE, false);
        printerId = PrefUtils.getInt(mContext, GlobalContants.PRINTERID, 0);
        interfaceType = PrefUtils.getInt(mContext,
                GlobalContants.INTERFACETYPE, 0);
//		spinner_printer_type.setSelection(printerId);
        spinner_interface_type.setSelection(interfaceType);
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
        super.onDestroy();
        if (interfaceType == 0 && hasRegDisconnectReceiver) {
            mContext.unregisterReceiver(myReceiver);
            hasRegDisconnectReceiver = false;
//			 Log.i(TAG, "关闭了广播！");
        }
        if (timer != null) {
            timer.cancel();
        }

    }


    private boolean isStart = false;
    private Timer timer = null;

    private void start(int way) {
        if (timer == null) {
            timer = new Timer();
            if (way == 1) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        PrinterInstance mPrinter = PrinterInstance.mPrinter;
                        XTUtils.printForTest(getResources(), mPrinter);
                    }
                }, 0, 300);
            } else if (way == 2) {
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
                switch (interfaceType) {
                    case 0:// bluetooth
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.str_message)
                                .setMessage(R.string.str_connlast)
                                .setPositiveButton(R.string.yesconn,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface arg0, int arg1) {
                                                // 重新连接
                                                if (!(mBtAdapter == null)) {
                                                    // 判断设备蓝牙功能是否打开
                                                    if (!mBtAdapter.isEnabled()) {
                                                        // 打开蓝牙功能
                                                        Intent enableIntent = new Intent(
                                                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                                        startActivity(enableIntent);
                                                    } else {
                                                        // mDevice
                                                        devicesAddress = PrefUtils
                                                                .getString(
                                                                        mContext,
                                                                        GlobalContants.DEVICEADDRESS,
                                                                        "");

                                                        if (devicesAddress == null
                                                                || devicesAddress
                                                                .length() <= 0) {
                                                            Toast.makeText(
                                                                    SettingActivity.this,
                                                                    "您是第一次启动程序，请选择重新搜索连接！",
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        } else {
                                                            connect2BlueToothdevice();
                                                        }
                                                    }
                                                }

                                            }
                                        })
                                .setNegativeButton(R.string.str_resel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                if (!(mBtAdapter == null)) {
                                                    // 判断设备蓝牙功能是否打开
                                                    if (!mBtAdapter.isEnabled()) {
                                                        // 打开蓝牙功能
                                                        Intent enableIntent = new Intent(
                                                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                                        startActivity(enableIntent);
                                                        Intent intent = new Intent(
                                                                mContext,
                                                                BluetoothDeviceList.class);
                                                        startActivityForResult(
                                                                intent,
                                                                CONNECT_DEVICE);
                                                    } else {
                                                        Intent intent = new Intent(
                                                                mContext,
                                                                BluetoothDeviceList.class);
                                                        startActivityForResult(
                                                                intent,
                                                                CONNECT_DEVICE);
                                                    }
                                                }
                                            }
                                        }).show();
                        break;
                    case 1:// USB

                        new AlertDialog.Builder(this)
                                .setTitle(R.string.str_message)
                                .setMessage(R.string.str_connlast)
                                .setPositiveButton(R.string.yesconn,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface arg0, int arg1) {

                                                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

                                                usbAutoConn(manager);
                                            }
                                        })
                                .setNegativeButton(R.string.str_resel,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {

                                                Intent intent = new Intent(
                                                        mContext,
                                                        UsbDeviceList.class);
                                                startActivityForResult(intent,
                                                        CONNECT_DEVICE);
                                            }

                                        }).show();
                        break;
                    case 2:// wifi
                        Intent intent = new Intent(mContext, IpEditActivity.class);
                        intent.putExtra(GlobalContants.WIFINAME, getWiFiName());
                        startActivityForResult(intent, CONNECT_DEVICE);
                        break;
                    case 3:// serial port
                        Intent intentSerial = new Intent(mContext,
                                SerialsDeviceList.class);
                        startActivityForResult(intentSerial, CONNECT_DEVICE);
                        break;
                    default:
                        break;
                }
            } else {
                if (myPrinter != null) {
                    myPrinter.closeConnection();
                    myPrinter = null;
                    Log.i(TAG, "已经断开");
                    if (interfaceType == 0 && hasRegDisconnectReceiver) {
                        mContext.unregisterReceiver(myReceiver);
                        hasRegDisconnectReceiver = false;
//						 Log.i(TAG, "关闭了广播！");
                    }
                }
                //下电
                if (deviceControl != null) {
                    try {
                        deviceControl.PowerOffDevice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                BaseActivity.stopCheckStatus();
            }

        }

        if (v == btn_scan_and_connect) {
            if (!isConnected) {
                Intent intent = new Intent();
                intent.setClass(mContext, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            } else {
                Toast.makeText(mContext, "当前已经连接到" + devicesName, Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnForPrint) {

            if (isConnected) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XTUtils.printForTest(getResources(), myPrinter);
                    }
                }).start();

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
        if (v == btnForPrintNote) {
            if (isConnected) {
                if (isStart && timer != null) {
                    timer.cancel();
                    timer = null;
                    isStart = false;
                    btnForPrintNote.setText(getResources().getString(R.string.repeat_print_note));
                    btnForPrint.setEnabled(true);
                    btn_selfprint_test.setEnabled(true);
                } else {
                    start(2);
                    isStart = true;
                    btnForPrintNote.setText(getResources().getString(R.string.repeat_print_stop));
                    btnForPrint.setEnabled(false);
                    btn_selfprint_test.setEnabled(false);
                }
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnSetVol) {
            if (isConnected) {
                String n = etV.getText().toString();
                assert myPrinter != null;
                String result = XTUtils.setVoltage(myPrinter, n);
                Toast.makeText(mContext, "" + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btnSetType) {
            if (isConnected) {
                int position = spinnerPaperType.getSelectedItemPosition();
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
                showConnectingDialog();
                new updateThread().start();
            } else {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == btn_getstate) {
            if (isConnected) {
                myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_RIGHT);
                Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6, "http://weixin.qq.com/r/mDu7o9DEGo1lrZTA926K");
                myPrinter.printBarCode(barcode);
                int state;
                state = myPrinter.getCurrentStatus();
                //没有相应传感器 只能检测缺纸状态和正常状态
                switch (state) {
                    case 0:
                        Toast.makeText(SettingActivity.this, "正常", Toast.LENGTH_SHORT).show();
                        break;
                    case -1:
                        Toast.makeText(SettingActivity.this, "通讯异常", Toast.LENGTH_SHORT).show();
                        break;
                    case -2:
                        Toast.makeText(SettingActivity.this, "缺纸", Toast.LENGTH_SHORT).show();
                        break;
                    case -3:
                        Toast.makeText(SettingActivity.this, "纸将尽", Toast.LENGTH_SHORT).show();
                        break;
                    case -4:
                        Toast.makeText(SettingActivity.this, "打印机开盖", Toast.LENGTH_SHORT).show();
                        break;
                }
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
//			Timer timer = new Timer();
//			timer.schedule(timerTask,150000);
            if (a == -2) {
                dismissConnectingDialog();
//				timer.cancel();
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
            //XTUtils.sendSedDate(in,myPrinter);
            Looper.loop();
        }
    }


    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(SettingActivity.this)
                            .setTitle("提示")
                            .setMessage("超时，升级失败。")
                            .setPositiveButton("确定", null).show();
                }
            });
        }
    };

    public static String jsonToStringFromAssetFolder(String fileName,
                                                     Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(fileName);

        byte[] data = new byte[file.available()];
        file.read(data);
        file.close();
        return new String(data, "gbk");
    }

    // 安卓3.1以后才有权限操作USB
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == CONNECT_DEVICE) {
            // 连接设备

            if (interfaceType == 0) {

                devicesAddress = data.getExtras().getString(
                        BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
                devicesName = data.getExtras().getString(
                        BluetoothDeviceList.EXTRA_DEVICE_NAME);
                Log.i("fdh", "设备名：" + devicesName + "设备地址:" + devicesAddress);
                connect2BlueToothdevice();
            } else if (interfaceType == 1)// usb
            {
                mUSBDevice = data.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                myPrinter = PrinterInstance.getPrinterInstance(mContext,
                        mUSBDevice, mHandler);
                devicesName = "USB device";
                UsbManager mUsbManager = (UsbManager) mContext
                        .getSystemService(Context.USB_SERVICE);

                if (mUsbManager.hasPermission(mUSBDevice)) {
                    // TODO
                    myPrinter.openConnection();
                    // myPrinter.printText("测试USB连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭/n");
                    // myPrinter.closeConnection();
                } else {
                    // 没有权限询问用户是否授予权限
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(
                            ACTION_USB_PERMISSION);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                    mContext.registerReceiver(mUsbReceiver, filter);
                    // 该代码执行后，系统弹出一个对话框
                    mUsbManager.requestPermission(mUSBDevice, pendingIntent);
                }

            } else if (interfaceType == 2) {
                // wifi
                devicesName = "Net device";
                devicesAddress = data.getStringExtra("ip_address");
                myPrinter = PrinterInstance.getPrinterInstance(devicesAddress,
                        9100, mHandler);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO
                        // myPrinter.printText("测试WiFi连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭/n");
                        myPrinter.openConnection();
                    }
                }).start();
            } else if (interfaceType == 3) {
                // 串口
                int baudrate = 9600;
                String path = data.getStringExtra("path");
                devicesName = "Serial device";
                devicesAddress = path;
                String com_baudrate = data.getExtras().getString("baudrate");
                if (com_baudrate == null || com_baudrate.length() == 0) {
                    baudrate = 9600;
                }
                baudrate = Integer.parseInt(com_baudrate);
                myPrinter = PrinterInstance.getPrinterInstance(new File(path),
                        baudrate, 0, mHandler);
                myPrinter.openConnection();
                Log.i(TAG, "波特率:" + baudrate + "路径:" + path);
                BaseActivity.startCheckStatus(mContext, myPrinter);
            }

        }
        if (requestCode == SCANNIN_GREQUEST_CODE) {

            // 校验扫描到的mac是否合法
            devicesAddress = data.getExtras().getString(
                    BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
            // if(devicesAddress.length() == 17 && devicesAddress.charAt(2) ==
            // ':'
            // && devicesAddress.charAt(5) == ':' && devicesAddress.charAt(8) ==
            // ':'
            // && devicesAddress.charAt(11) == ':' && devicesAddress.charAt(14)
            // == ':')
            Log.i(TAG, "devicesAddress:" + devicesAddress);
            if (BluetoothAdapter.checkBluetoothAddress(devicesAddress)) {
                connect2BlueToothdevice();

            } else {
                Toast.makeText(mContext, "蓝牙mac:" + devicesAddress + "不合法", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void connect2BlueToothdevice() {
        dialog.show();
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
                devicesAddress);
        devicesName = mDevice.getName();
        myPrinter = PrinterInstance.getPrinterInstance(mDevice, mHandler);
        if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {// 未绑定
            // Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            // mContext.startActivity(intent);
            IntentFilter boundFilter = new IntentFilter();
            boundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(boundDeviceReceiver, boundFilter);
            PairOrConnect(true);
        } else {
            PairOrConnect(false);
        }
    }

    private void PairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter(
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(boundDeviceReceiver, boundFilter);
            boolean success = false;
            try {
                Method createBondMethod = BluetoothDevice.class
                        .getMethod("createBond");
                success = (Boolean) createBondMethod.invoke(mDevice);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "createBond is success? : " + success);

        } else {
            new connectThread().start();

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

    private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDevice.equals(device)) {
                    return;
                }
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.i(TAG, "bounding......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.i(TAG, "bound success");
                        // if bound success, auto init BluetoothPrinter. open
                        // connect.
                        mContext.unregisterReceiver(boundDeviceReceiver);
                        dialog.show();
                        // 配对完成开始连接
                        if (myPrinter != null) {
                            new connectThread().start();
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        mContext.unregisterReceiver(boundDeviceReceiver);
                        Log.i(TAG, "bound cancel");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        @SuppressLint("NewApi")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.w(TAG, "receiver action: " + action);

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    mContext.unregisterReceiver(mUsbReceiver);
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            && mUSBDevice.equals(device)) {
                        myPrinter.openConnection();
                    } else {
                        mHandler.obtainMessage(Connect.FAILED).sendToTarget();
                        Log.e(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private class connectThread extends Thread {
        @Override
        public void run() {

            if (myPrinter != null) {
                // TODO
                isConnected = myPrinter.openConnection();
                // myPrinter.printText("测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭/n");
                // myPrinter.closeConnection();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        if (parent == spinner_interface_type) {
            PrefUtils.setInt(mContext, GlobalContants.INTERFACETYPE, position);
            interfaceType = position;
            Log.i(TAG, "position:" + position);
        }
        if (parent == spinnerSetConcentration) {
            if (!isConnected) {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
        if (parent == spinnerPaperType) {
            if (!isConnected) {
                Toast.makeText(mContext, getString(R.string.no_connected), Toast.LENGTH_SHORT).show();
            }
        }
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

    public void usbAutoConn(UsbManager manager) {
        doDiscovery(manager);
        if (!deviceList.isEmpty()) {
            mUSBDevice = deviceList.get(0);
        }
        if (mUSBDevice != null) {
            PrinterInstance.getPrinterInstance(mContext, mUSBDevice, mHandler)
                    .openConnection();
        } else {
            mHandler.obtainMessage(Connect.FAILED).sendToTarget();
            myPrinter.closeConnection();
        }
    }

    @SuppressLint("NewApi")
    private void doDiscovery(UsbManager manager) {
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        deviceList = new ArrayList<UsbDevice>();
        for (UsbDevice device : devices.values()) {
            if (USBPort.isUsbPrinter(device)) {
                deviceList.add(device);
            }
        }
    }

    private String getWiFiName() {
        String wifiName = null;
        WifiManager mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!mWifi.isWifiEnabled()) {
            mWifi.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        wifiName = wifiInfo.getSSID();
        wifiName = wifiName.replaceAll("\"", "");
        return wifiName;
    }

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                if (device != null && myPrinter != null
                        && isConnected && device.equals(mDevice)) {
                    myPrinter.closeConnection();
                    mHandler.obtainMessage(Connect.CLOSED).sendToTarget();
                }
            }
        }
    };

    private void copyFilesFromassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFromassets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
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
