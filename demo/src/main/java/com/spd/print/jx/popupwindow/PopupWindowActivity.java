package com.spd.print.jx.popupwindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.spd.print.jx.R;
import com.spd.print.jx.adapter.DownListAdapter;
import com.spd.print.jx.application.BaseApp;
import com.speedata.libutils.SharedXmlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zzc
 */
public class PopupWindowActivity extends Activity {

    private final static String SET_TYPE = "type";
    private final static String SET_DENSITY = "density";
    private ListView listView;
    private List<String> list = new ArrayList<>();
    private DownListAdapter downListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popupwindow);
        // 让此界面的宽度撑满整个屏幕
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
        initData();
    }

    private void initData() {
        String setting = getIntent().getStringExtra("setting");
        if (SET_TYPE.equals(setting)) {
            list.add(getResources().getString(R.string.normal_paper));
            list.add(getResources().getString(R.string.label_paper));
            downListAdapter.setChecked(SharedXmlUtil.getInstance(this,"setting").read("paper_type", 1));
        } else if (SET_DENSITY.equals(setting)) {
            list.addAll(Arrays.asList("1", "2", "3", "4", "5"));
            downListAdapter.setChecked(SharedXmlUtil.getInstance(this,"setting").read("density", 1));
        }
    }

    private void initView() {
        listView = findViewById(R.id.list_down_pop);
        downListAdapter = new DownListAdapter(this, R.layout.item_list_pop, list);
        listView.setAdapter(downListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                downListAdapter.setChecked(position);
                downListAdapter.notifyDataSetInvalidated();
                String listResult = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("listResult", listResult);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
            }
        });
    }

    /**
     * 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
     *
     * @param event 触摸事件
     * @return 返回值
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }
}
