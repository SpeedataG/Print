package com.spd.jxprint.adapter;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.view.indicator.IndicatorViewPager;
import com.spd.jxprint.R;
import com.spd.jxprint.barcodeprint.BarcodeFragment;
import com.spd.jxprint.barcodeprint.PrintBarcodeActivity;
import com.spd.jxprint.barcodeprint.QrCodeFragment;

/**
 * @author :zzc in  2020/8/27 14:53 update.
 */
public class BarcodeAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
    private int[] versions = {R.string.barcode_fragment, R.string.qr_code_fragment};
    private PrintBarcodeActivity mContext;

    public BarcodeAdapter(FragmentManager fragmentManager, PrintBarcodeActivity mContext) {
        super(fragmentManager);
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return versions.length;
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = mContext.getLayoutInflater().inflate(R.layout.tab_barcode_top, container, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(mContext.getString(versions[position]));

        int width = getTextWidth(textView);
        int padding = 4;
        //因为wrap的布局 字体大小变化会导致textView大小变化产生抖动，这里通过设置textView宽度就避免抖动现象
        //1.1f是根据上面字体大小变化的倍数1.1f设置
        textView.setWidth((int) (width * 1.1f) + padding);

        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        if (position == 0) {
            return new BarcodeFragment().setArguments(position + "");
        } else {
            return new QrCodeFragment().setArguments(position + "");
        }
    }


    @Override
    public int getItemPosition(Object object) {
        //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
        // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
        return PagerAdapter.POSITION_UNCHANGED;
    }

    private int getTextWidth(TextView textView) {
        if (textView == null) {
            return 0;
        }
        Rect bounds = new Rect();
        String text = textView.getText().toString();
        Paint paint = textView.getPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width();
    }

}

