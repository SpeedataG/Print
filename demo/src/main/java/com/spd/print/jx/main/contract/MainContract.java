package com.spd.print.jx.main.contract;

/**
 * @author :Reginer in  2019/8/21 11:11.
 * 联系方式:QQ:282921012
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
