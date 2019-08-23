package com.spd.print.jx.textprint.presenter;

import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.textprint.PrintTextActivity;
import com.spd.print.jx.textprint.contract.PrintTextContract;
import com.spd.print.jx.textprint.model.PrintTextModel;

/**
 * @author zzc
 */
public class PrintTextPresenter extends BasePresenter<PrintTextActivity, PrintTextModel> implements PrintTextContract.Presenter {
    @Override
    protected PrintTextModel createModel() {
        return new PrintTextModel();
    }
}
