package com.spd.print.jx.barcodeprint;

import android.os.Bundle;

import com.shizhefei.fragment.LazyFragment;

public class QrCodeFragment extends LazyFragment {
    public QrCodeFragment() {
    }

    public QrCodeFragment setArguments(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("_type", type);
        setArguments(bundle);
        return this;
    }
}
