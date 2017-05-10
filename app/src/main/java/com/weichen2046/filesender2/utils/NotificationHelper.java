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
import com.weichen2046.filesender2.service.UserConfirmationHandleService;
import com.weichen2046.filesender2.service.Desktop;

import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_ACCEPT_STATE;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_AUTH_DEVICE;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_FILE_IDS;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_FILE_NAMES;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_MSG_TYPE;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.MSG_TYPE_AUTH;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.MSG_TYPE_RECV_FILE;

/**
 * Created by chenwei on 2017/3/18.
 */

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static final int NOTIFICATION_DEVICE_AUTH_REQ = 1;
    public static final int NOTIFICATION_RECV_FILE_REQ = 2;

    public static final int NOTIFICATION_DEVICE_AUTH_ACCEPT = 1;
    public static final int NOTIFICATION_DEVICE_AUTH_DENIAL = 2;
    public static final int NOTIFICATION_DEVICE_RECV_FILE_ACCEPT = 3;
    public static final int NOTIFICATION_DEVICE_REC_FILE_DENIAL = 4;

    public static void notifyAuthRequest(Context context, Desktop desktop) {
        if (context == null) {
            Log.w(TAG, "can not make auth request notification, context is null");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Resources rs = context.getResources();
        builder.setSmallIcon(R.drawable.ic_statusbar_notify_auth_req);
        builder.setLargeIcon(BitmapFactory.decodeResource(rs, R.mipmap.ic_launcher));
        builder.setContentTitle(rs.getString(R.string.auth_request_title));
        builder.setContentText(String.format(rs.getString(R.string.fmt_auth_request_text), desktop.address));

        Intent service = new Intent(context, UserConfirmationHandleService.class);
        service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_AUTH);
        service.putExtra(EXTRA_ACCEPT_STATE, true);
        service.putExtra(EXTRA_AUTH_DEVICE, desktop);
        PendingIntent pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DEVICE_AUTH_ACCEPT, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_check_black_32dp, rs.getString(R.string.request_accept), pendingIntent);

        service = new Intent(context, UserConfirmationHandleService.class);
        service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_AUTH);
        service.putExtra(EXTRA_ACCEPT_STATE, false);
        service.putExtra(EXTRA_AUTH_DEVICE, desktop);
        pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DEVICE_AUTH_DENIAL, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_close_black_32dp, rs.getString(R.string.request_denial), pendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_DEVICE_AUTH_REQ, builder.build());
    }

    public static void notifySendFileRequest(Context context, Desktop remoteDevice,
                                             String[] fileIDs, String[] files) {
        if (context == null) {
            Log.w(TAG, "can not make notification, context is null");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Resources rs = context.getResources();
        builder.setSmallIcon(R.drawable.ic_statusbar_notify_auth_req);
        builder.setLargeIcon(BitmapFactory.decodeResource(rs, R.mipmap.ic_launcher));
        builder.setContentTitle(rs.getString(R.string.file_sending_request));
        String contentText =
                files.length == 1
                        ? files[0]
                        : String.format(rs.getString(R.string.fmt_file_to_recv), files.length);
        builder.setContentText(contentText);

        Intent service = new Intent(context, UserConfirmationHandleService.class);
        service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_RECV_FILE);
        service.putExtra(EXTRA_ACCEPT_STATE, true);
        service.putExtra(EXTRA_AUTH_DEVICE, remoteDevice);
        service.putExtra(EXTRA_FILE_IDS, fileIDs);
        service.putExtra(EXTRA_FILE_NAMES, files);
        PendingIntent pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DEVICE_RECV_FILE_ACCEPT, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_check_black_32dp, rs.getString(R.string.request_accept), pendingIntent);

        service = new Intent(context, UserConfirmationHandleService.class);
        service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_RECV_FILE);
        service.putExtra(EXTRA_ACCEPT_STATE, false);
        service.putExtra(EXTRA_AUTH_DEVICE, remoteDevice);
        service.putExtra(EXTRA_FILE_IDS, fileIDs);
        service.putExtra(EXTRA_FILE_NAMES, files);
        pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DEVICE_REC_FILE_DENIAL, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_close_black_32dp, rs.getString(R.string.request_denial), pendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_RECV_FILE_REQ, builder.build());
    }
}
