package com.spd.print.jx.textprint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.popupwindow.PopupWindowActivity;
import com.spd.print.jx.textprint.contract.PrintTextContract;
import com.spd.print.jx.textprint.presenter.PrintTextPresenter;
import com.spd.print.jx.utils.ToastUtil;

import java.util.Objects;

/**
 * 文本打印
 *
 * @author zzc
 * @date 2019/8/23
 */
public class PrintTextActivity extends BaseMvpActivity<PrintTextPresenter> implements View.OnClickListener, PrintTextContract.View {

    private EditText etTextContent;
    private CheckBox checkBoxHex;

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
        findViewById(R.id.tv_text_note).setOnClickListener(this);
        findViewById(R.id.tv_text_code).setOnClickListener(this);
        findViewById(R.id.tv_text_table).setOnClickListener(this);
        findViewById(R.id.btn_send_print).setOnClickListener(this);
        etTextContent = findViewById(R.id.et_text_content);
        checkBoxHex = findViewById(R.id.cb_text_hex);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_text_note:
                    mPresenter.printNote();
                    break;
                case R.id.tv_text_code:
                    Intent intent = new Intent(this, PopupWindowActivity.class);
                    intent.putExtra("setting", "code_pages");
                    startActivityForResult(intent, 4);
                    break;
                case R.id.tv_text_table:
                    mPresenter.printTable();
                    break;
                case R.id.btn_send_print:
                    mPresenter.sendPrintData(etTextContent.getText().toString(), checkBoxHex.isChecked());
                    break;
                default:
                    break;
            }
        } catch (RuntimeException e) {
            ToastUtil.customToastView(mContext, getString(R.string.toast_error_tips), Toast.LENGTH_SHORT
                    , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkBoxHex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etTextContent.setText(getResources().getString(R.string.text_print_test_hex));
                } else {
                    etTextContent.setText(getResources().getString(R.string.text_print_test));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("zzc", "requestCode:" + requestCode + " resultCode:" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 4) {
                Bundle bundle = Objects.requireNonNull(data).getExtras();
                String codeType = Objects.requireNonNull(bundle).getString("listResult");
                int codeTypeId = bundle.getInt("position");
                try {
                    mPresenter.printCodePages(codeTypeId, codeType);
                } catch (RuntimeException e) {
                    ToastUtil.customToastView(mContext, getString(R.string.toast_error_tips), Toast.LENGTH_SHORT
                            , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
                }
            }
        }
    }
}
