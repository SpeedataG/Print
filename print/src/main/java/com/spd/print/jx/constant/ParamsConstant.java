package com.spd.print.jx.constant;

/**
 * @author zzc
 * @date 2019/12/17
 */
public class ParamsConstant {

    /**
     * 纸类型
     *
     * @param type 0 1 2
     * @return byte[]
     */
    public static byte[] paperType(int type) {
        return new byte[]{0x1F, 0x44, (byte) type};
    }

    /**
     * 浓度
     *
     * @param density 0-4
     * @return byte[]
     */
    public static byte[] density(int density) {
        return new byte[]{0x1F, 0x16, (byte) density};
    }

    /**
     * 速度
     *
     * @param speed 0-4
     * @return byte[]
     */
    public static byte[] speed(int speed) {
        return new byte[]{0x1F, 0x15, (byte) speed};
    }

    /**
     * 灵敏度
     *
     * @param sensitivity 电压值，默认20
     * @return byte[]
     */
    public static byte[] sensitivity(int sensitivity) {
        return new byte[]{0x1F, 0x46, (byte) sensitivity};
    }

    /**
     * 检测到纸缝后的出纸距离
     *
     * @param len 默认144
     * @return byte[]
     */
    public static byte[] outPaperLen(int len) {
        return new byte[]{0x1F, (byte) 0xC0, (byte) len};
    }

}
