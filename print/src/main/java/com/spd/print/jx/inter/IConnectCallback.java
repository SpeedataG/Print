package com.spd.print.jx.inter;

import com.spd.print.jx.constant.PrintConstant.PrintConnectError;

/**
 * @author :zzc in  2020/8/27 14:53 update.
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
