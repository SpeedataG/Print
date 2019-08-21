package com.spd.print.jx.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.setting.contract.PrintSettingContract;
import com.spd.print.jx.setting.presenter.PrintSettingPresenter;

/**
 * @author :Reginer in  2019/8/21 12:11.
 * 联系方式:QQ:282921012
 * 功能描述:打印设置
 */
public class PrintSettingActivity extends BaseMvpActivity<PrintSettingPresenter> implements View.OnClickListener, PrintSettingContract.View {


    @Override
    protected int getActLayoutId() {
        return R.layout.activity_print_setting;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolBar.setNavigationIcon(R.drawable.ic_back);
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        findViewById(R.id.systemUpdate).setOnClickListener(this);
    }

    @Override
    protected PrintSettingPresenter createPresenter() {
        return new PrintSettingPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.systemUpdate:
                mPresenter.systemUpdate();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSuccess() {
        // TODO: 2019/8/21 升级成功
    }

    @Override
    public void onUpdateError(Exception e) {
        // TODO: 2019/8/21 升级失败
    }

}
