package com.spd.print.jx.setting.presenter;

import com.spd.lib.mvp.BasePresenter;
import com.spd.print.jx.setting.PrintSettingActivity;
import com.spd.print.jx.setting.contract.PrintSettingContract;
import com.spd.print.jx.setting.model.PrintSettingModel;

/**
 * @author :Reginer in  2019/8/21 12:10.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class PrintSettingPresenter extends BasePresenter<PrintSettingActivity, PrintSettingModel> implements PrintSettingContract.Presenter {
    @Override
    protected PrintSettingModel createModel() {
        return new PrintSettingModel();
    }

    @Override
    public void systemUpdate() {
        //开始升级
        //升级之后调用结果回调getView().onUpdateSuccess();  or getView().onUpdateError(null);
    }
}
