package com.spd.print.jx.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author :lzfengluo in  2019/8/16 15:10.
 * 功能描述:打印浓度
 */
public class DensityConstant {

    public static final int DENSITY_LOWEST = 0;
    public static final int DENSITY_LOW = 1;
    public static final int DENSITY_MEDIUM = 2;
    public static final int DENSITY_HIGH = 3;
    public static final int DENSITY_HIGHEST = 4;

    @IntDef({DENSITY_LOWEST, DENSITY_LOW, DENSITY_MEDIUM, DENSITY_HIGH, DENSITY_HIGHEST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PrintDensity {
    }
}
