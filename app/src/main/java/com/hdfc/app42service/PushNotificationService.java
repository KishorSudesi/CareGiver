package com.hdfc.app42service;

import android.content.Context;

import com.hdfc.libs.AsyncApp42ServiceApi;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

public class PushNotificationService {

    private AsyncApp42ServiceApi asyncService;

    public PushNotificationService(Context context) {
        asyncService = AsyncApp42ServiceApi.instance(context);
    }

    public void sendPushToUser(String strUserName, String strMessage, App42CallBack app42CallBack) {
        asyncService.sendPushToUser(strUserName, strMessage, app42CallBack);
    }

    public void removeUserDevice(String strUserName, String strToken, App42CallBack app42CallBack) {
        asyncService.removeDevice(strUserName, strToken, app42CallBack);
    }

}
