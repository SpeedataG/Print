package com.spd.print.jx.pictureprint.contract;

public interface PrintPictureContract {
    interface Model {
    }

    interface View {
        /**
         * 显示等待框
         */
        void progressDialogShow();

        /**
         * 关闭等待框
         */
        void progressDialogDismiss();
    }

    interface Presenter {
    }
}
