package com.spd.print.jx.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.inter.IConnectCallback;
import com.spd.print.jx.main.presenter.MainPresenter;
import com.spd.print.jx.setting.PrintSettingActivity;

/**
 * @author :Reginer in  2019/8/21 11:00.
 * 联系方式:QQ:282921012
 * 功能描述:首页
 */
public class MainActivity extends BaseMvpActivity<MainPresenter> implements View.OnClickListener, IConnectCallback {


    @Override
    protected int getActLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        findViewById(R.id.connectPrinter).setOnClickListener(this);
        findViewById(R.id.printSetting).setOnClickListener(this);
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
                mPresenter.connectPrinter();
                break;
            case R.id.printSetting:
                startActivity(new Intent(this, PrintSettingActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    public void onPrinterConnectSuccess() {

    }

    @Override
    public void onPrinterConnectFailed(int errorCode) {

    }
}
