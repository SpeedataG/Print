package com.spd.jxprint.barcodeprint;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.sdk.Barcode;
import com.shizhefei.fragment.LazyFragment;
import com.spd.jxprint.R;
import com.spd.jxprint.application.BaseApp;
import com.spd.jxprint.popupwindow.PopupWindowActivity;
import com.spd.jxprint.utils.CheckUtils;
import com.spd.print.jx.utils.ToastUtil;
import com.speedata.libutils.SharedXmlUtil;

import java.util.Objects;

import static com.printer.sdk.PrinterConstants.BarcodeType.CODABAR;
import static com.printer.sdk.PrinterConstants.BarcodeType.CODE128;
import static com.printer.sdk.PrinterConstants.BarcodeType.CODE39;
import static com.printer.sdk.PrinterConstants.BarcodeType.CODE93;
import static com.printer.sdk.PrinterConstants.BarcodeType.ITF;
import static com.printer.sdk.PrinterConstants.BarcodeType.JAN13;
import static com.printer.sdk.PrinterConstants.BarcodeType.JAN8;
import static com.printer.sdk.PrinterConstants.BarcodeType.UPC_A;
import static com.printer.sdk.PrinterConstants.BarcodeType.UPC_E;
import static com.printer.sdk.PrinterConstants.Command;

/**
 * @author zzc
 */
public class BarcodeFragment extends LazyFragment implements View.OnClickListener {

    private EditText etBarcodeContent;
    private int typeInt = 0;
    private TextView tvBarcodeType;
    private TextView tvBarcodeLen;

    public BarcodeFragment() {
    }

    public BarcodeFragment setArguments(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("_type", type);
        setArguments(bundle);
        return this;
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_barcode);
        initView();
        tvBarcodeLen.setVisibility(View.VISIBLE);
        tvBarcodeType.setText(getResources().getStringArray(R.array.barcode1)[0]);
        SharedXmlUtil.getInstance(getActivity(), "setting").write("barcode", 0);
        checkLength(etBarcodeContent.getText().toString());
    }

    private void initView() {
        tvBarcodeType = (TextView) findViewById(R.id.tv_barcode_type);
        tvBarcodeType.setOnClickListener(this);
        findViewById(R.id.btn_send_print).setOnClickListener(this);
        tvBarcodeLen = (TextView) findViewById(R.id.tv_code_len);
        etBarcodeContent = (EditText) findViewById(R.id.et_barcode_content);
        etBarcodeContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_barcode_type:
                Intent intent = new Intent(getActivity(), PopupWindowActivity.class);
                intent.putExtra("setting", "barcode");
                startActivityForResult(intent, 3);
                break;
            case R.id.btn_send_print:
                try {
                    sendPrint();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    ToastUtil.customToastView(getContext(), getString(R.string.toast_error_tips), Toast.LENGTH_SHORT
                            , (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_toast, null));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Bundle bundle = data.getExtras();
            String type = Objects.requireNonNull(bundle).getString("listResult");
            typeInt = bundle.getInt("position");
            tvBarcodeType.setText(type);
            SharedXmlUtil.getInstance(getActivity(), "setting").write("barcode", typeInt);
            checkText(etBarcodeContent.getText().toString());
            checkLength(etBarcodeContent.getText().toString());
        }
    }

    private void sendPrint() {
        String content = etBarcodeContent.getText().toString();
        if (!content.isEmpty()) {
            if (!checkLength(content)) {
                ToastUtil.customToastView(getContext(), getString(R.string.toast_content_length), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_toast, null));
                return;
            }
            if (!checkText(content)) {
                return;
            }
            int width = 2;
            int height = 162;
            Barcode barcode;
            byte[] bytes = new byte[]{CODE128, CODE39, CODABAR, ITF, CODE93, UPC_A, UPC_E, JAN13, JAN8};
            if (typeInt < 9) {
                barcode = new Barcode(bytes[typeInt], width, height, 2, content);
                BaseApp.getPrinterImpl().setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
                BaseApp.getPrinterImpl().printText("打印 " + tvBarcodeType.getText().toString() + " 码效果展示：");
                BaseApp.getPrinterImpl().setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
                BaseApp.getPrinterImpl().printBarCode(barcode);
                BaseApp.getPrinterImpl().setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
                BaseApp.getPrinterImpl().setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
            } else {
                for (int i = 0; i < typeInt; i++) {
                    barcode = new Barcode(bytes[i], width, height, 2, content);
                    BaseApp.getPrinterImpl().setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
                    BaseApp.getPrinterImpl().printText("打印 " + getResources().getStringArray(R.array.barcode1)[i] + " 码效果演示：");
                    BaseApp.getPrinterImpl().setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
                    BaseApp.getPrinterImpl().printBarCode(barcode);
                    BaseApp.getPrinterImpl().setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
                }
            }
        }
    }

    private boolean checkText(String text) {
        switch (typeInt) {
            case 1:
                if (CheckUtils.code39Check(text)) {
                    etBarcodeContent.setTextColor(getResources().getColor(R.color.black_text));
                    return true;
                } else {
                    etBarcodeContent.setTextColor(getResources().getColor(R.color.red_text));
                    ToastUtil.customToastView(getContext(), getString(R.string.toast_content_illegal) + getString(R.string.toast_code39_legal)
                            , Toast.LENGTH_SHORT, (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_toast, null));
                    return false;
                }
            case 2:
                if (CheckUtils.codeBarCheck(text)) {
                    etBarcodeContent.setTextColor(getResources().getColor(R.color.black_text));
                    return true;
                } else {
                    etBarcodeContent.setTextColor(getResources().getColor(R.color.red_text));
                    ToastUtil.customToastView(getContext(), getString(R.string.toast_content_illegal) + getString(R.string.toast_code_bar_legal)
                            , Toast.LENGTH_SHORT, (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_toast, null));
                    return false;
                }
            case 0:
            case 4:
                etBarcodeContent.setTextColor(getResources().getColor(R.color.black_text));
                return true;
            default:
                if (CheckUtils.numberCheck(text)) {
                    etBarcodeContent.setTextColor(getResources().getColor(R.color.black_text));
                    return true;
                } else {
                    etBarcodeContent.setTextColor(getResources().getColor(R.color.red_text));
                    ToastUtil.customToastView(getContext(), getString(R.string.toast_content_illegal) + getString(R.string.toast_number_legal)
                            , Toast.LENGTH_SHORT, (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_toast, null));
                    return false;
                }
        }
    }

    private boolean checkLength(String text) {
        switch (typeInt) {
            case 0:
                tvBarcodeLen.setText(getString(R.string.toast_len_code128));
                return text.length() <= 255 && text.length() >= 2;
            case 5:
            case 6:
                tvBarcodeLen.setText(getString(R.string.toast_len_upc));
                return text.length() <= 12 && text.length() >= 11;
            case 7:
                tvBarcodeLen.setText(getString(R.string.toast_len_ean13));
                return text.length() <= 13 && text.length() >= 12;
            case 8:
                tvBarcodeLen.setText(getString(R.string.toast_len_ean8));
                return text.length() <= 8 && text.length() >= 7;
            default:
                tvBarcodeLen.setText(getString(R.string.toast_len_def));
                return text.length() <= 255 && text.length() >= 1;
        }
    }
}
