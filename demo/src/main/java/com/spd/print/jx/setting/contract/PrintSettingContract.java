package com.spd.print.jx.setting.contract;

/**
 * @author :Reginer in  2019/8/21 12:10.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public interface PrintSettingContract {
    interface Model {
    }

    interface View {
        /**
         * 升级成功
         */
        void onUpdateSuccess();

        /**
         * 升级失败
         *
         * @param e 错误信息
         */
        void onUpdateError(Exception e);
    }

    interface Presenter {
        /**
         * 系统升级
         */
        void systemUpdate();
    }
}
