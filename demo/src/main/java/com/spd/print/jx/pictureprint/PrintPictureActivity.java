package com.spd.print.jx.pictureprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.pictureprint.contract.PrintPictureContract;
import com.spd.print.jx.pictureprint.presenter.PrintPicturePresenter;

/**
 * 图片打印
 *
 * @author zzc
 */
public class PrintPictureActivity extends BaseMvpActivity<PrintPicturePresenter> implements View.OnClickListener, PrintPictureContract.View {
    @Override
    public void onClick(View v) {

    }

    @Override
    protected PrintPicturePresenter createPresenter() {
        return new PrintPicturePresenter();
    }

    @Override
    protected int getActLayoutId() {
        return R.layout.activity_print_picture;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

    }
}
