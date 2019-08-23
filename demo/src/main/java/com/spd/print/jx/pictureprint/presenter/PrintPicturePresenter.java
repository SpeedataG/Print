package com.spd.print.jx.pictureprint.presenter;

import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.pictureprint.PrintPictureActivity;
import com.spd.print.jx.pictureprint.contract.PrintPictureContract;
import com.spd.print.jx.pictureprint.model.PrintPictureModel;

/**
 * @author zzc
 */
public class PrintPicturePresenter extends BasePresenter<PrintPictureActivity, PrintPictureModel> implements PrintPictureContract.Presenter {
    @Override
    protected PrintPictureModel createModel() {
        return new PrintPictureModel();
    }
}
