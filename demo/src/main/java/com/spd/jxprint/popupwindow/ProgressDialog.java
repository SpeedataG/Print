package com.spd.jxprint.popupwindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.spd.jxprint.R;

/**
 * 正在打印等待框
 *
 * @author zzc
 */
public class ProgressDialog {
    private static Context mContext;
    private static Dialog dialog;
    private static ProgressDialog mProgressDialog;

    public static ProgressDialog getInstance(Context context) {
        if (dialog == null) {
            mProgressDialog = new ProgressDialog(context);
        }
        return mProgressDialog;
    }

    public static void releaseInstance() {
        mProgressDialog = null;
    }

    private ProgressDialog(Context context) {
        mContext = context;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_progress_printing);
        WindowManager.LayoutParams wm = window.getAttributes();
        // 设置对话框的宽
        wm.width = 700;
        // 设置对话框的高
        wm.height = 500;
        // 对话框背景透明度
        wm.alpha = 0.5f;
        // 遮罩层亮度
        wm.dimAmount = 0.2f;
        window.setAttributes(wm);
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
