package com.printer.demo;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.sdk.PrinterInstance;

public class BaseActivity extends Activity {
    private static boolean isFirst = true;
    protected static String titleConnectState = "";
    private static PrinterInstance mPrinter;
    private static int status;
    /**
     * ��������textview
     */
    protected static TextView headerConnecedState;

    public static String getTitleState() {
        return titleConnectState;
    }

    public static void setTitleState(String titleState) {
        BaseActivity.titleConnectState = titleState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFirst) {
            setTitleState(getResources().getString(R.string.off_line));
            isFirst = false;
        }
    }

    /**
     * ����2������������headerview��
     */
    public void setHeaderLeftImage(LinearLayout header,
                                   View.OnClickListener listener) {
        ImageView ivback = (ImageView) header
                .findViewById(R.id.iv_hearderview_left_image);
        ivback.setVisibility(View.VISIBLE);
        if (listener != null) {
            ivback.setOnClickListener(listener);
        }
        ivback.setImageResource(R.drawable.icon_back);
    }

    public void setHeaderLeftText(LinearLayout header, String title,
                                  View.OnClickListener listener) {
        TextView tvText = (TextView) header
                .findViewById(R.id.tv_headerview_left_text);
        headerConnecedState = (TextView) header
                .findViewById(R.id.tv_headerview_connected);
        if (TextUtils.isEmpty(title)) {
            tvText.setText("");
        } else {
            tvText.setText(title);
        }
        if (listener != null) {
            tvText.setOnClickListener(listener);
        }

    }

    public void setHeaderCenterText(LinearLayout header, String title) {
        TextView tvText = (TextView) header
                .findViewById(R.id.tv_headerview_center_text);
        headerConnecedState = (TextView) header
                .findViewById(R.id.tv_headerview_connected);
        if (TextUtils.isEmpty(title)) {
            tvText.setText("");
        } else {
            tvText.setText(title);
        }

    }

    // 判断当前是否使用的是 WIFI网络
    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private static Handler handler = new Handler();
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mPrinter != null) {
                status = mPrinter.getPrinterStatus();
                switch (status) {
                    case 0:
                        Log.d("zzc:", "====正常====");
                        break;
                    case 1:
                        Log.d("zzc:", "====缺纸====");
                        break;
                    default:
                        Log.d("zzc:", "====status====" + status);
                        break;
                }
            } else {
                mPrinter = PrinterInstance.mPrinter;
            }
            handler.postDelayed(this, 1000);
        }
    };

    public static void startCheckStatus(PrinterInstance myPrinter) {
        mPrinter = myPrinter;
        if (handler != null) {
            handler.postDelayed(runnable, 500);
        }
    }

    public static void stopCheckStatus() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
