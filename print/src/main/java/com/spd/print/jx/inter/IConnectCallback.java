package com.spd.print.jx.inter;

import com.spd.print.jx.constant.PrintConstant.PrintConnectError;

/**
 * @author :Reginer in  2019/8/16 10:31.
 * 联系方式:QQ:282921012
 * 功能描述:连接打印机回调
 */
public interface IConnectCallback {
    /**
     * 连接成功
     */
    void onPrinterConnectSuccess();

    /**
     * 连接失败
     *
     * @param errorCode 错误码 {@link PrintConnectError}
     *                  <p>
     *                  由于不确定是否只有这些错误码，参数上不做限制了，如果确定错误码只有这些，
     *                  可以参数上加上 @PrintConnectError限定
     *                  </p>
     */
    void onPrinterConnectFailed(int errorCode);


}
