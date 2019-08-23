package com.spd.print.jx.setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.setting.contract.PrintSettingContract;
import com.spd.print.jx.setting.presenter.PrintSettingPresenter;
import com.spd.print.jx.utils.ToastUtil;

/**
 * @author :Reginer in  2019/8/21 12:11.
 * 联系方式:QQ:282921012
 * 功能描述:打印设置
 */
public class PrintSettingActivity extends BaseMvpActivity<PrintSettingPresenter> implements View.OnClickListener, PrintSettingContract.View {

    private AlertDialog dialog;
    private EditText etSensitivity;
    private TextView tvReadShow;
    private Spinner spDensity, spPaperType;

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
        findViewById(R.id.paperFeed).setOnClickListener(this);
        findViewById(R.id.paperBack).setOnClickListener(this);
        findViewById(R.id.print_normal_paper).setOnClickListener(this);
        findViewById(R.id.print_label_paper).setOnClickListener(this);
        findViewById(R.id.print_self_page).setOnClickListener(this);
        findViewById(R.id.btn_set_sensitivity).setOnClickListener(this);
        findViewById(R.id.btn_read).setOnClickListener(this);
        etSensitivity = findViewById(R.id.et_sensitivity);
        tvReadShow = findViewById(R.id.tv_read_show);
        findViewById(R.id.btn_set_density).setOnClickListener(this);
        findViewById(R.id.btn_set_paper_type).setOnClickListener(this);
        spDensity = findViewById(R.id.sp_density);
        spPaperType = findViewById(R.id.sp_paper_type);
    }

    @Override
    protected PrintSettingPresenter createPresenter() {
        return new PrintSettingPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.systemUpdate:
                progressDialogShow();
                mPresenter.systemUpdate();
                break;
            case R.id.paperFeed:
                mPresenter.setPaperFeed();
                break;
            case R.id.paperBack:
                mPresenter.setPaperBack();
                break;
            case R.id.print_normal_paper:
                mPresenter.printNormalTest(getString(R.string.example_text));
                break;
            case R.id.print_label_paper:
                mPresenter.printLabelTest(getString(R.string.example_text));
                break;
            case R.id.print_self_page:
                mPresenter.printSelfCheck();
                break;
            case R.id.btn_set_sensitivity:
                String strSensitivity = etSensitivity.getText().toString();
                mPresenter.setSensitivity(strSensitivity);
                break;
            case R.id.btn_read:
                tvReadShow.setText(mPresenter.readStatus());
                break;
            case R.id.btn_set_density:
                mPresenter.setDensity(spDensity.getSelectedItemPosition());
                break;
            case R.id.btn_set_paper_type:
                mPresenter.setPaperType(spPaperType.getSelectedItemPosition());
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSuccess() {
        // TODO: 2019/8/21 升级成功
        progressDialogDismiss();
        BaseApp.isConnection = false;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.customToastView(mContext, getString(R.string.toast_operation_success), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
            }
        });
    }

    @Override
    public void onUpdateError(Exception e) {
        // TODO: 2019/8/21 升级失败
        progressDialogDismiss();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.customToastView(mContext, getString(R.string.toast_operation_fail), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
            }
        });
    }

    private void progressDialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_progress);
        WindowManager.LayoutParams wm = window.getAttributes();
        // 设置对话框的宽
        wm.width = 700;
        // 设置对话框的高
        wm.height = 500;
        // 对话框背景透明度
        wm.alpha = 0.4f;
        // 遮罩层亮度
        wm.dimAmount = 0.2f;
        window.setAttributes(wm);
    }

    private void progressDialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
