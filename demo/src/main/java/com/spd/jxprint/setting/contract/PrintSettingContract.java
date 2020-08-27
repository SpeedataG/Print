package com.spd.jxprint.setting.contract;

/**
 * @author :zzc in  2020/8/27 14:50 update.
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
        /**
         * 连接打印机
         */
        void connectPrinter();

        /**
         * 断开连接
         */
        void disconnectPrinter();
    }
}
