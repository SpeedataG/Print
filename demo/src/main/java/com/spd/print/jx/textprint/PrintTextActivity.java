package com.spd.print.jx.textprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.textprint.contract.PrintTextContract;
import com.spd.print.jx.textprint.presenter.PrintTextPresenter;

/**
 * 文本打印
 *
 * @author zzc
 * @date 2019/8/23
 */
public class PrintTextActivity extends BaseMvpActivity<PrintTextPresenter> implements View.OnClickListener, PrintTextContract.View {

    @Override
    protected PrintTextPresenter createPresenter() {
        return new PrintTextPresenter();
    }

    @Override
    protected int getActLayoutId() {
        return R.layout.activity_print_text;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolBar.setNavigationIcon(R.drawable.ic_back);
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {

    }
}
