/*
 * Copyright (C) 2010 The MobileSecurePay Project All right reserved. author:
 * shiqun.shi@alipay.com
 *
 * 提示：如何获取安全校验码和合作身份者id 1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 * 2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 * 3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package cn.timeface.circle.baby.payment.timeface;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl
// RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

    // 合作身份者id，以2088开头的16位纯数字
    public static final String DEFAULT_PARTNER = "2088002597202423";

    // 收款支付宝账号
    public static final String DEFAULT_SELLER = "ahea@163.com";

    // 商户私钥，自助生成
    public static final String PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJwXH9rWUvLtXvbl"
            + "pHWv6301QZCM+uZD4TeORoLSvm8VSjaM5ZNan/TQ8leKqk1DJBzF7DhbuAqM4NuM"
            + "r5SgftOlAEWJI0Ylx3q90Uvllpq5sdcpsW3jhVJg8EcE27l0LH6yTuSYBLkSI4uB"
            + "xR9vJ9qneCkuEKG+3KHKscGvInx1AgMBAAECgYBdonmXe05TMBXxohygBKINgC8O"
            + "maPBEiM+gnjF7coTNQBJ7Qei95BQ+i8GWMaEhqxZHlnwDQVAPvZ8fc6uKAEr4Ujt"
            + "OAgtDPuGgwX8vf86gPQ6wSYOIKMuzDgPyYnbVja7hTuOfDb85+QNif6xkh3JVxOR"
            + "vQ8pXUTX9SnPMnl6GQJBAMsYMvg5dFQ3vY/pnxslJIm1FTeUK633p3Lg9IrOcXb6"
            + "5NtCEto9dWHnjc93Nsy0wR3Y/gS5Ca6XQmjv4lPzZ+8CQQDEwFkYI4TefWsdmLPw"
            + "0r4qk+8+ubMn/NAJj0AhJM8HF077Z31dUV6nb/U3ziBehiS4EfN7iikxZqpRdB7o"
            + "hZ3bAkBMDc3Ygrt7ZjxIjjYU1j3ui69cVtJcnWdJb9Bjwpde9OmK6h1hOK6icTH7"
            + "xSryUaYX5VCKuDhV9zLZVSuuQHJlAkBKQ5QdgWKonExvKnFZCCLRbW9TjMJr6IgZ"
            + "46FAIWWndovQZxqxu4Hvz1mOy9X598YqWFRAIEE2LVtCTYNRHwYbAkEAlrqqGi4N"
            + "bnA+rUQk1psJNlGeK0L2RRm+YPjKDC79uMDZJF4cAkt/VUDxUzcc975o/oL7dnoF" + "L1khmYHYPTmOeg==";

    public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdy"
            + "PuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBV"
            + "OrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
}
