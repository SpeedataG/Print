package com.spd.print.jx.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.barcodeprint.PrintBarcodeActivity;
import com.spd.print.jx.constant.PrintConstant;
import com.spd.print.jx.inter.IConnectCallback;
import com.spd.print.jx.main.presenter.MainPresenter;
import com.spd.print.jx.pictureprint.PrintPictureActivity;
import com.spd.print.jx.setting.PrintSettingActivity;
import com.spd.print.jx.textprint.PrintTextActivity;
import com.spd.print.jx.utils.ToastUtil;

/**
 * @author :Reginer in  2019/8/21 11:00.
 * 联系方式:QQ:282921012
 * 功能描述:首页
 */
public class MainActivity extends BaseMvpActivity<MainPresenter> implements View.OnClickListener, IConnectCallback {

    private Button btnConnect;
    private TextView statusName, statusAddress;
    private ImageView mIvConnect;

    @Override
    protected int getActLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        btnConnect = findViewById(R.id.connectPrinter);
        btnConnect.setOnClickListener(this);
        findViewById(R.id.ll_text_print).setOnClickListener(this);
        findViewById(R.id.ll_barcode_print).setOnClickListener(this);
        findViewById(R.id.ll_picture_print).setOnClickListener(this);
        findViewById(R.id.ll_setting).setOnClickListener(this);
        statusName = findViewById(R.id.tv_status_name);
        statusAddress = findViewById(R.id.tv_status_address);
        mIvConnect = findViewById(R.id.img_icon_connect);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectPrinter:
                if (!BaseApp.isConnection) {
                    mPresenter.connectPrinter();
                } else {
                    mPresenter.disconnectPrinter();
                }
                break;
            case R.id.ll_setting:
                startActivity(new Intent(this, PrintSettingActivity.class));
                break;
            case R.id.ll_text_print:
                startActivity(new Intent(this, PrintTextActivity.class));
                break;
            case R.id.ll_barcode_print:
                startActivity(new Intent(this, PrintBarcodeActivity.class));
                break;
            case R.id.ll_picture_print:
                startActivity(new Intent(this, PrintPictureActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPrinterConnectSuccess() {
        BaseApp.isConnection = true;
        BaseApp.deviceName = "Serial device";
        BaseApp.deviceAddress = "/dev/ttyMT0";
        statusName.setText(BaseApp.deviceName);
        statusAddress.setText(BaseApp.deviceAddress);
        btnConnect.setText(getResources().getString(R.string.disconnect_printer));
        mIvConnect.setImageResource(R.mipmap.home_connect);
        ToastUtil.customToastView(mContext, getString(R.string.toast_success), Toast.LENGTH_SHORT
                , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
    }

    @Override
    public void onPrinterConnectFailed(int errorCode) {
        BaseApp.isConnection = false;
        BaseApp.deviceName = getResources().getString(R.string.status_disconnect);
        BaseApp.deviceAddress = getResources().getString(R.string.status_disconnect);
        statusName.setText(BaseApp.deviceName);
        statusAddress.setText(BaseApp.deviceAddress);
        btnConnect.setText(getResources().getString(R.string.connect_printer));
        mIvConnect.setImageResource(R.mipmap.home_disconnect);
        switch (errorCode) {
            case PrintConstant.CONNECT_CLOSED:
                ToastUtil.customToastView(mContext, getString(R.string.toast_close), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
                break;
            default:
                ToastUtil.customToastView(mContext, getString(R.string.toast_fail), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.disconnectPrinter();
        super.onDestroy();
    }
}
