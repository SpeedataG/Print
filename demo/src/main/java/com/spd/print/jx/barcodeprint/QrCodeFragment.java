package com.spd.print.jx.barcodeprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;
import com.shizhefei.fragment.LazyFragment;
import com.spd.print.jx.R;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.popupwindow.PopupWindowActivity;
import com.speedata.libutils.SharedXmlUtil;

import static com.printer.sdk.PrinterConstants.BarcodeType.*;


/**
 * @author zzc
 */
public class QrCodeFragment extends LazyFragment implements View.OnClickListener {
    private EditText etBarcodeContent;
    private int typeInt = 0;
    private TextView tvQrCodeType;

    public QrCodeFragment() {
    }

    public QrCodeFragment setArguments(String type) {
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
        tvQrCodeType.setText(getResources().getStringArray(R.array.barcode2)[0]);
        SharedXmlUtil.getInstance(getActivity(), "setting").write("qr_code", 0);
    }

    private void initView() {
        tvQrCodeType = (TextView) findViewById(R.id.tv_barcode_type);
        tvQrCodeType.setOnClickListener(this);
        findViewById(R.id.btn_send_print).setOnClickListener(this);
        etBarcodeContent = (EditText) findViewById(R.id.et_barcode_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_barcode_type:
                Intent intent = new Intent(getActivity(), PopupWindowActivity.class);
                intent.putExtra("setting", "qr_code");
                startActivityForResult(intent, 3);
                break;
            case R.id.btn_send_print:
                sendPrint();
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
            String type = bundle.getString("listResult");
            typeInt = bundle.getInt("position");
            tvQrCodeType.setText(type);
            SharedXmlUtil.getInstance(getActivity(), "setting").write("qr_code", typeInt);
        }
    }

    private void sendPrint() {
        String content = etBarcodeContent.getText().toString();
        if (!content.isEmpty()) {
            Barcode barcode;
            int[] width = new int[]{0, 1, 8};
            int[] height = new int[]{76, 0, 0};
            byte[] bytes = new byte[]{QRCODE, PDF417, DATAMATRIX};
            barcode = new Barcode(bytes[typeInt], width[typeInt], height[typeInt], 6, content);
            BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
            BaseApp.getPrinterImpl().printText("打印 " + tvQrCodeType.getText().toString() + " 码效果展示：");
            BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
            BaseApp.getPrinterImpl().printBarCode(barcode);
            BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
            BaseApp.getPrinterImpl().setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        }
    }
}
