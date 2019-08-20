package com.spd.print.jx.ui.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.spd.print.jx.impl.PrintImpl;
import com.spd.print.jx.inter.IConnectCallback;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * @author zzc
 */
public class MainViewModel extends BaseViewModel implements IConnectCallback {
    private PrintImpl mPrintImpl;

    @Override
    public void onCreate() {
        super.onCreate();
        mPrintImpl = new PrintImpl();
        mPrintImpl.connectPrinter(MainViewModel.this);
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onPrinterConnectSuccess() {

    }

    @Override
    public void onPrinterConnectFailed(int errorCode) {

    }

    @Override
    public void accept(Object o) throws Exception {

    }

    public BindingCommand llPrint = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            mPrintImpl.printText("打印打印\n");
        }
    });

}
