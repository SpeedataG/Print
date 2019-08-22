package com.spd.print.jx.main.presenter;

import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.application.BaseApp;
import com.spd.print.jx.main.MainActivity;
import com.spd.print.jx.main.contract.MainContract;
import com.spd.print.jx.main.model.MainModel;

/**
 * @author :Reginer in  2019/8/21 11:11.
 * 联系方式:QQ:282921012
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

}
