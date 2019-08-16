package com.spd.print.jx.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author :Reginer in  2019/8/16 11:10.
 * 联系方式:QQ:282921012
 * 功能描述:纸类型
 */
public class PaperConstant {
    /**
     * 普通纸
     */
    public static final int NORMAL_PAPER = 0;
    /**
     * 标签纸
     */
    public static final int TAG_PAPER = 1;
    /**
     * 黑标纸
     */
    public static final int BLACK_MARKING_PAPER = 2;

    @IntDef({NORMAL_PAPER, TAG_PAPER, BLACK_MARKING_PAPER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PrintPaperType {
    }
}
