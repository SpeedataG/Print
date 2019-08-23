package com.spd.print.jx.barcodeprint.presenter;

import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.barcodeprint.PrintBarcodeActivity;
import com.spd.print.jx.barcodeprint.contract.PrintBarcodeContract;
import com.spd.print.jx.barcodeprint.model.PrintBarcodeModel;

/**
 * @author zzc
 */
public class PrintBarcodePresenter extends BasePresenter<PrintBarcodeActivity, PrintBarcodeModel> implements PrintBarcodeContract.Presenter {
    @Override
    protected PrintBarcodeModel createModel() {
        return new PrintBarcodeModel();
    }
}
