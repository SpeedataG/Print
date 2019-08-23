package com.spd.print.jx.barcodeprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.spd.lib.mvp.BaseModel;
import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.barcodeprint.contract.PrintBarcodeContract;
import com.spd.print.jx.barcodeprint.presenter.PrintBarcodePresenter;

/**
 * 条码打印页
 *
 * @author zzc
 */
public class PrintBarcodeActivity extends BaseMvpActivity<PrintBarcodePresenter> implements View.OnClickListener, PrintBarcodeContract.View {
    @Override
    public void onClick(View v) {

    }

    @Override
    protected PrintBarcodePresenter createPresenter() {
        return new PrintBarcodePresenter();
    }

    @Override
    protected int getActLayoutId() {
        return R.layout.activity_print_barcode;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

    }
}
