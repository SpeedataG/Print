package com.spd.jxprint.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spd.jxprint.R;

import java.util.List;


/**
 * 下拉列表适配器
 *
 * @author zzc
 */
public class DownListAdapter extends ArrayAdapter {

    private int resourceId;
    private int checkedId;

    public DownListAdapter(Context context, int resource, List<String> list) {
        super(context, resource, list);
        resourceId = resource;
    }

    public void setChecked(int checked) {
        checkedId = checked;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String str = (String) getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView itemText = view.findViewById(R.id.item_text);
        ImageView itemImg = view.findViewById(R.id.item_img);
        itemImg.setVisibility(View.INVISIBLE);
        itemText.setText(str);
        if (checkedId == position) {
            itemImg.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
