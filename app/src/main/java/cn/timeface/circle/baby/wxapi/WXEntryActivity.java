/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.timeface.circle.baby.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.responses.WxLoginInfoResponse;
import cn.timeface.circle.baby.api.services.WechatApiService;
import cn.timeface.circle.baby.constants.WXConstants;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends WechatHandlerActivity implements
		IWXAPIEventHandler {
	private static WxLoginCallBack callBack;
	private IWXAPI api;
	private String code;
	private WechatApiService apiWechatService = ApiFactory.getApi().getApiWechatService();

	public static void setWxLoginCallBack(WxLoginCallBack call) {
		callBack = call;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		api = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	/**
	 * 处理微信发出的向第三方应用请求app message
	 * <p>
	 * 在微信客户端中的聊天页面有“添加工具”，可以将本应用的图标添加到其中 此后点击图标，下面的代码会被执行。Demo仅仅只是打开自己而已，但你可
	 * 做点其他的事情，包括根本不打开任何页面
	 */
	public void onGetMessageFromWXReq(WXMediaMessage msg) {
		Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(
				getPackageName());
		startActivity(iLaunchMyself);
	}

	/**
	 * 处理微信向第三方应用发起的消息
	 * <p>
	 * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
	 * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作 回调。
	 * <p>
	 * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
	 */
	public void onShowMessageFromWXReq(WXMediaMessage msg) {
		if (msg != null && msg.mediaObject != null
				&& (msg.mediaObject instanceof WXAppExtendObject)) {
			WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
			Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
	}
	//
	@Override
	public void onResp(BaseResp resp) {

		// 微信授权登录 （1，先获取code 2，用code再换取accessToken 3，使用accessToken调接口）
//        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//            code = ((SendAuth.Resp) resp).code;
//            getAccessToken();
//        }

//        int result = 0;
//        switch (resp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                result = R.string.errcode_success;
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = R.string.errcode_cancel;
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = R.string.errcode_deny;
//                break;
//            default:
//                result = R.string.errcode_unknown;
//                break;
//        }

//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

//    /**
//     * 第二步用code换取accessToken
//     */
//    public void getAccessToken() {
//        if (callBack == null) return;
//        if (TextUtils.isEmpty(code)) {
//            callBack.loginErr("登录失败");
//            return;
//        }
//
//        apiWechatService.getAccessToken(
//                WXConstants.APP_ID,
//                WXConstants.APP_SECRET_KEY,
//                code,
//                "authorization_code")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(response -> {
//
//                    if (response.getErrcode() == 0) {
//                        doGetLoginInfo(response.getAccess_token(), response.getOpenid(), response.getExpires_in());
//                    } else {
//                        callBack.loginErr(response.getErrmsg());
//                    }
//                }, throwable -> {
//                });
//    }
//
//    /**
//     * 第三步获取用户信息
//     *
//     * @param token
//     * @param openid
//     * @param timeout
//     */
//    private void doGetLoginInfo(final String token, String openid, final long timeout) {
//        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(openid)) {
//            callBack.loginErr("登录失败");
//            return;
//        }
//
//        apiWechatService.getPersonInfo(token, openid)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(response -> {
//                    if (response.getErrcode() == 0) {
//                        response.setAccessToken(token);
//                        response.setExpiry_in(String.valueOf(timeout));
//                        callBack.loginSuc(response);
//                    } else {
//                        callBack.loginErr(response.getErrmsg());
//                    }
//                }, throwable -> {
//
//                });
//
//    }

	public interface WxLoginCallBack {
		void loginErr(String msg);

		void loginSuc(WxLoginInfoResponse wxLoginInfo);
	}
}

