package com.spd.jxprint.pictureprint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spd.jxprint.R;
import com.spd.jxprint.application.BaseApp;
import com.spd.jxprint.pictureprint.contract.PrintPictureContract;
import com.spd.jxprint.pictureprint.presenter.PrintPicturePresenter;
import com.spd.jxprint.popupwindow.ProgressDialog;
import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.utils.ToastUtil;

/**
 * 图片打印
 *
 * @author zzc
 */
public class PrintPictureActivity extends BaseMvpActivity<PrintPicturePresenter> implements View.OnClickListener, PrintPictureContract.View {

    private ImageView mIvPreview;

    @Override
    public void onClick(View v) {
        if (BaseApp.isConnection) {
            switch (v.getId()) {
                case R.id.btn_print_bitmap:
                    mPresenter.printPresetBitmap();
                    break;
                case R.id.btn_print_canvas:
                    mPresenter.printCanvas();
                    break;
                case R.id.btn_picture_photo:
                    mPresenter.printLocalPhoto();
                    break;
                default:
                    break;
            }
        } else {
            ToastUtil.customToastView(mContext, getString(R.string.toast_error_tips), Toast.LENGTH_SHORT
                    , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
        }
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolBar.setNavigationIcon(R.drawable.ic_back);
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
        findViewById(R.id.btn_print_bitmap).setOnClickListener(this);
        findViewById(R.id.btn_print_canvas).setOnClickListener(this);
        findViewById(R.id.btn_picture_photo).setOnClickListener(this);
        mIvPreview = findViewById(R.id.iv_picture_preview);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void progressDialogShow() {
        ProgressDialog.getInstance(this).show();
    }

    @Override
    public void progressDialogDismiss() {
        ProgressDialog.getInstance(this).dismiss();
    }
}
