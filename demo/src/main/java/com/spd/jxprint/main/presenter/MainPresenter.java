package com.spd.jxprint.main.presenter;

import com.spd.lib.mvp.BasePresenter;
import com.spd.jxprint.application.BaseApp;
import com.spd.jxprint.main.MainActivity;
import com.spd.jxprint.main.contract.MainContract;
import com.spd.jxprint.main.model.MainModel;
import com.spd.print.jx.constant.ParamsConstant;

/**
 * @author :zzc in  2020/8/27 14:48 update.
 * 功能描述:
 */
public class MainPresenter extends BasePresenter<MainActivity, MainModel> implements MainContract.Presenter {
    @Override
    protected MainModel createModel() {
        return new MainModel();
    }

    @Override
    public void connectPrinter() {
        BaseApp.getPrinterImpl().connectPrinter(getView());
    }

    @Override
    public void disconnectPrinter() {
        BaseApp.getPrinterImpl().closeConnect();
    }

    public void initPrint(int type, int density) {
        BaseApp.getPrinterImpl().setAllParams(ParamsConstant.paperType(type), ParamsConstant.density(density));
    }

}
