package com.spd.print.jx.ui.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.spd.print.jx.R;
import com.spd.print.jx.BR;
import com.spd.print.jx.databinding.ActivityMainBinding;
import com.spd.print.jx.ui.viewmodel.MainViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * @author zzc
 */
public class MainActivity extends BaseActivity<ActivityMainBinding,MainViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public MainViewModel initViewModel() {
        return ViewModelProviders.of(this).get(MainViewModel.class);
    }
}
