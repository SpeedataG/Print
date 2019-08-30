package com.spd.print.jx.barcodeprint;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.spd.lib.mvp.BaseModel;
import com.spd.lib.mvp.BaseMvpActivity;
import com.spd.print.jx.R;
import com.spd.print.jx.adapter.BarcodeAdapter;
import com.spd.print.jx.barcodeprint.contract.PrintBarcodeContract;
import com.spd.print.jx.barcodeprint.presenter.PrintBarcodePresenter;

/**
 * 条码打印页
 *
 * @author zzc
 */
public class PrintBarcodeActivity extends BaseMvpActivity<PrintBarcodePresenter> implements View.OnClickListener, PrintBarcodeContract.View {
    private ViewPager mViewPager;
    private ScrollIndicatorView mIndicator;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_barcode_ex:
                break;
            case R.id.tv_qrcode_ex:
                break;
            case R.id.tv_scan_print:
                break;
            default:
                break;
        }
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolBar.setNavigationIcon(R.drawable.ic_back);
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
        findViewById(R.id.tv_barcode_ex).setOnClickListener(this);
        findViewById(R.id.tv_qrcode_ex).setOnClickListener(this);
        findViewById(R.id.tv_scan_print).setOnClickListener(this);
        mViewPager = findViewById(R.id.barcode_body_view);
        mIndicator = findViewById(R.id.indicator);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        float unSelectSize = 14;
        float selectSize = 14;
        mIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(getColor(R.color.blue_text)
                , getColor(R.color.black_text)).setSize(selectSize, unSelectSize));
        ColorBar scrollBar = new ColorBar(this, getColor(R.color.blue_text), 6);
        scrollBar.setWidth(400);
        mIndicator.setScrollBar(scrollBar);
        mViewPager.setOffscreenPageLimit(2);
        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(mIndicator, mViewPager);
        BarcodeAdapter barcodeAdapter = new BarcodeAdapter(getSupportFragmentManager(), this);
        indicatorViewPager.setAdapter(barcodeAdapter);
    }

}
