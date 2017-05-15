package com.weichen2046.filesender2.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.weichen2046.filesender2.R;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.UserConfirmationHandleService;

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
    public static final String TAG = "NotificationHelper";

    public static final int NOTIFICATION_DEVICE_AUTH_REQ = 1;
    public static final int NOTIFICATION_RECV_FILE_REQ = 2;

    public static final int NOTIFICATION_DEVICE_AUTH_ACCEPT = 1;
    public static final int NOTIFICATION_DEVICE_AUTH_DENIAL = 2;
    public static final int NOTIFICATION_DEVICE_RECV_FILE_ACCEPT = 3;
    public static final int NOTIFICATION_DEVICE_RECV_FILE_DENIAL = 4;
    public static final int NOTIFICATION_DEVICE_RECV_FILE_DETAILS = 5;

    public static void notifyAuthRequest(Context context, Desktop device) {
        if (context == null) {
            Log.w(TAG, "can not make auth request notification, context is null");
            return;
        }

        boolean isAppShowing = BaseActivity.isAppShowingUnlocked();
        Resources rs = context.getResources();
        if (isAppShowing) {
            Intent transparentActivity = new Intent(context, NotificationDialogHelperActivity.class);
            transparentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            transparentActivity.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_AUTH);
            transparentActivity.putExtra(EXTRA_AUTH_DEVICE, device);
            context.startActivity(transparentActivity);
        }
        // show notification via system notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_statusbar_notify_auth_req);
        builder.setLargeIcon(BitmapFactory.decodeResource(rs, R.mipmap.ic_launcher));
        builder.setContentTitle(rs.getString(R.string.auth_request_title));
        builder.setContentText(String.format(rs.getString(R.string.fmt_auth_request_text), device.address));
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        Intent service = new Intent(context, UserConfirmationHandleService.class);
        service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_AUTH);
        service.putExtra(EXTRA_ACCEPT_STATE, true);
        service.putExtra(EXTRA_AUTH_DEVICE, device);
        PendingIntent pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DEVICE_AUTH_ACCEPT, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_check_black_32dp, rs.getString(R.string.request_accept), pendingIntent);

        service = new Intent(context, UserConfirmationHandleService.class);
        service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_AUTH);
        service.putExtra(EXTRA_ACCEPT_STATE, false);
        service.putExtra(EXTRA_AUTH_DEVICE, device);
        pendingIntent = PendingIntent.getService(context,
                NOTIFICATION_DEVICE_AUTH_DENIAL, service, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_close_black_32dp, rs.getString(R.string.request_denial), pendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_DEVICE_AUTH_REQ, builder.build());
    }

    public static void notifySendFileRequest(Context context, Desktop device, String[] fileIDs,
                                             String[] fileNames) {
        if (context == null) {
            Log.w(TAG, "can not make notification, context is null");
            return;
        }

        boolean isAppShowing = BaseActivity.isAppShowingUnlocked();
        if (isAppShowing) {
            Intent notificationDialog = new Intent(context, NotificationDialogHelperActivity.class);
            notificationDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationDialog.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_RECV_FILE);
            notificationDialog.putExtra(EXTRA_AUTH_DEVICE, device);
            notificationDialog.putExtra(EXTRA_FILE_IDS, fileIDs);
            notificationDialog.putExtra(EXTRA_FILE_NAMES, fileNames);
            context.startActivity(notificationDialog);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Resources rs = context.getResources();
        builder.setSmallIcon(R.drawable.ic_statusbar_notify_auth_req);
        builder.setLargeIcon(BitmapFactory.decodeResource(rs, R.mipmap.ic_launcher));
        builder.setContentTitle(rs.getString(R.string.file_sending_request));
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        String contentText =
                fileNames.length == 1
                        ? fileNames[0]
                        : String.format(rs.getString(R.string.fmt_file_to_recv), fileNames.length);
        builder.setContentText(contentText);

        if (fileNames.length == 1) {
            Intent service = new Intent(context, UserConfirmationHandleService.class);
            service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_RECV_FILE);
            service.putExtra(EXTRA_ACCEPT_STATE, true);
            service.putExtra(EXTRA_AUTH_DEVICE, device);
            service.putExtra(EXTRA_FILE_IDS, fileIDs);
            service.putExtra(EXTRA_FILE_NAMES, fileNames);
            PendingIntent pendingIntent = PendingIntent.getService(context,
                    NOTIFICATION_DEVICE_RECV_FILE_ACCEPT, service, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_check_black_32dp, rs.getString(R.string.request_accept), pendingIntent);

            service = new Intent(context, UserConfirmationHandleService.class);
            service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_RECV_FILE);
            service.putExtra(EXTRA_ACCEPT_STATE, false);
            service.putExtra(EXTRA_AUTH_DEVICE, device);
            service.putExtra(EXTRA_FILE_IDS, fileIDs);
            service.putExtra(EXTRA_FILE_NAMES, fileNames);
            pendingIntent = PendingIntent.getService(context,
                    NOTIFICATION_DEVICE_RECV_FILE_DENIAL, service, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_close_black_32dp, rs.getString(R.string.request_denial), pendingIntent);

        } else {
            Intent detailsActivity = new Intent(context, PendingRecvFilesActivity.class);
            detailsActivity.putExtra(EXTRA_AUTH_DEVICE, device);
            detailsActivity.putExtra(EXTRA_FILE_IDS, fileIDs);
            detailsActivity.putExtra(EXTRA_FILE_NAMES, fileNames);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    NOTIFICATION_DEVICE_RECV_FILE_DETAILS, detailsActivity,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_viewlist_black_32dp,
                    rs.getString(R.string.request_details), pendingIntent);
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_RECV_FILE_REQ, builder.build());
    }
}
