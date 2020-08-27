package com.spd.jxprint.main.contract;

/**
 * @author :zzc in  2020/8/27 14:48 update.
 * 功能描述:
 */
public interface MainContract {
    interface Model {
    }

    interface View {
    }

    interface Presenter {
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
