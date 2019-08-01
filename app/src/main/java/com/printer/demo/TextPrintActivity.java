package com.printer.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.printer.demo.global.GlobalContants;
import com.printer.demo.utils.CodePageUtils;
import com.printer.demo.utils.PrefUtils;
import com.printer.demo.utils.PrintLabel100;
import com.printer.demo.utils.XTUtils;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;

public class TextPrintActivity extends BaseActivity implements OnClickListener,
        OnCheckedChangeListener {

    private static final String TAG = "TextPrintActivity";
    private LinearLayout header;
    private Button btn_send, btn_print_note, btn_print_table,
            btn_print_codepaper;
    private ToggleButton tb_isHexData;
    private EditText et_input;
    private boolean isHexData = false;
    private static PrinterInstance mPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, " TextPrintActivity onCreate");
        setContentView(R.layout.activity_print_text);
        init();
        mPrinter = PrinterInstance.mPrinter;
//		PrinterConstants.paperWidth = PrefUtils.getInt(TextPrintActivity.this,
//				GlobalContants.PAPERWIDTH, 384);
//		Log.i(TAG, "paperWidth:" + PrinterConstants.paperWidth);

    }

    @Override
    protected void onResume() {
        super.onResume();
//		if (GlobalContants.ISCONNECTED) {
//			if ("".equals(GlobalContants.DEVICENAME)
//					|| GlobalContants.DEVICENAME == null) {
//				headerConnecedState.setText(R.string.unknown_device);
//
//			} else {
//
//				headerConnecedState.setText(GlobalContants.DEVICENAME);
//			}
//
//		}

    }

    private void init() {

        et_input = (EditText) findViewById(R.id.et_input);
        et_input.setText(R.string.textprintactivty_input_content);
        header = (LinearLayout) findViewById(R.id.ll_headerview_textPrint);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        btn_print_note = (Button) findViewById(R.id.btn_print_note);
        btn_print_note.setOnClickListener(this);
        btn_print_table = (Button) findViewById(R.id.btn_print_table);
        btn_print_table.setOnClickListener(this);
        btn_print_codepaper = (Button) findViewById(R.id.btn_print_codepaper);
        btn_print_codepaper.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        tb_isHexData = (ToggleButton) findViewById(R.id.tb_hex_on);
        tb_isHexData.setOnCheckedChangeListener(this);
        isHexData = tb_isHexData.isChecked();
        initHeader();
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
        headerConnecedState.setText(getTitleState());
        setHeaderCenterText(header, getString(R.string.headview_TextPrint));
        setHeaderLeftImage(header, new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (PrinterInstance.mPrinter != null && SettingActivity.isConnected) {
            if (view == btn_send) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String content = et_input.getText().toString();
                        Log.i(TAG, content);
                        if (content != null || content.length() != 0) {

                            if (isHexData && SettingActivity.isConnected) {
                                byte[] srcData = XTUtils.string2bytes(content);
                                mPrinter.sendBytesData(srcData);
                            } else if (!isHexData && SettingActivity.isConnected) {
                                mPrinter.printText(content + "\r\n");
                            }
                        }
                    }
                }).start();

            } else if (view == btn_print_table) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XTUtils.printTable1(TextPrintActivity.this.getResources(), mPrinter, true);
                    }
                }).start();

            } else if (view == btn_print_note) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XTUtils.printNote(TextPrintActivity.this.getResources(),
                                mPrinter);
                    }
                }).start();

            } else if (view == btn_print_codepaper) {
                new CodePageUtils().selectCodePage(TextPrintActivity.this,
                        PrinterInstance.mPrinter);
            }
        } else {
            Toast.makeText(TextPrintActivity.this, getString(R.string.no_connected), 0).show();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {// 16进制开
            isHexData = true;
            et_input.setText("0x54 0x68 0x69 0x73 0x20 0x69 0x73 0x20 0x6a 0x75 0x73 0x74 0x20 0x61 0x20 0x74 0x65 0x73 0x74 0x21 0x0a 0x0d");
        } else {
            isHexData = false;
            et_input.setText(R.string.textprintactivty_input_content);
        }

    }

}
