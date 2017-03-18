package com.weichen2046.filesender2.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.weichen2046.filesender2.R;
import com.weichen2046.filesender2.service.AuthenticationHandleService;
import com.weichen2046.filesender2.service.Desktop;

/**
 * Created by chenwei on 2017/3/18.
 */

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static final int NOTIFICATION_DESKTOP_AUTH_REQ = 1;

    public static final String EXTRA_ACCEPT_STATE = "extra_access_state";
    public static final String EXTRA_AUTH_DESKTOP = "extra_auth_desktop";

    public static void makeAuthRequestNotification(Context context, Desktop desktop) {
        if (context == null) {
            Log.w(TAG, "can not make auth request notification, context is null");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Resources rs = context.getResources();
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(rs, R.mipmap.ic_launcher));
        builder.setContentTitle(rs.getString(R.string.auth_request_title));
        builder.setContentText(String.format(rs.getString(R.string.auth_request_text), desktop.address));

        Intent service = new Intent(context, AuthenticationHandleService.class);
        service.putExtra(EXTRA_ACCEPT_STATE, true);
        service.putExtra(EXTRA_AUTH_DESKTOP, desktop);
        PendingIntent pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DESKTOP_AUTH_REQ, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_check_black_32dp, rs.getString(R.string.auth_request_accept), pendingIntent);

        service = new Intent(context, AuthenticationHandleService.class);
        service.putExtra(EXTRA_ACCEPT_STATE, false);
        service.putExtra(EXTRA_AUTH_DESKTOP, desktop);
        pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DESKTOP_AUTH_REQ, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_close_black_32dp, rs.getString(R.string.auth_request_denial), pendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_DESKTOP_AUTH_REQ, builder.build());
    }
}
