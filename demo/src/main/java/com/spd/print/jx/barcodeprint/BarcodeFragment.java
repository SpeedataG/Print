package com.spd.print.jx.barcodeprint;

import android.os.Bundle;

import com.shizhefei.fragment.LazyFragment;
import com.spd.print.jx.R;

public class BarcodeFragment extends LazyFragment {
    public BarcodeFragment() {
    }

    public BarcodeFragment setArguments(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("_type", type);
        setArguments(bundle);
        return this;
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_barcode);
    }
}
