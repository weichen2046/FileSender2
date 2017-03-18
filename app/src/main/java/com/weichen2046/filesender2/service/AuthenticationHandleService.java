package com.weichen2046.filesender2.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.weichen2046.filesender2.utils.NotificationHelper;

public class AuthenticationHandleService extends Service {
    private static final String TAG = "AuthenticationHandleService";

    public AuthenticationHandleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NotificationHelper.NOTIFICATION_DESKTOP_AUTH_REQ);
        boolean accept = intent.getBooleanExtra(NotificationHelper.EXTRA_ACCEPT_STATE, false);
        Log.d(TAG, (accept ? "accept" : "denial") + " authentication request");
        return super.onStartCommand(intent, flags, startId);
    }
}
