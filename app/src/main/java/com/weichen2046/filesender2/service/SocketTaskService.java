package com.weichen2046.filesender2.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.weichen2046.filesender2.ui.ResultBroadcastReceiver;
import com.weichen2046.filesender2.utils.ConfirmDesktopAuthRequestDataSource;
import com.weichen2046.filesender2.utils.ConfirmExchangeTcpPortDataSource;
import com.weichen2046.filesender2.utils.tcpdatasource.RequestSendFileDataSource;
import com.weichen2046.filesender2.utils.tcpdatasource.SendFileDataSource;
import com.weichen2046.filesender2.utils.TcpDataSender;
import com.weichen2046.filesender2.utils.UdpDataSender;
import com.weichen2046.filesender2.utils.tcpdatasource.ConfirmSendingFileRequestDataSource;

public class SocketTaskService extends IntentService {
    private static final String TAG = "SocketTaskService";

    private static final String ACTION_SEND_FILE
            = "action.filesender2.SEND_FILE";
    private static final String ACTION_REQEST_SEND_FILE
            = "action.filesender2.REQUEST_SEND_FILE";
    private static final String ACTION_CONFIRM_DESKTOP_AUTH
            = "action.filesender2.ACTION_CONFIRM_DESKTOP_AUTH";
    private static final String ACTION_CONFIRM_EXCHANGE_TCP_PORT
            = "action.filesender2.ACTION_CONFIRM_EXCHANGE_TCP_PORT";
    private static final String ACTION_CONFIRM_SENDING_FILE_REQ
            = "action.filesender2.ACTION_CONFIRM_SENDING_FILE_REQ";

    private static final String EXTRA_FILE_URI      = "extra_file_uri";
    private static final String EXTRA_DEST_HOST     = "extra_dest_host";
    private static final String EXTRA_DEST_PORT     = "extra_dest_port";
    private static final String EXTRA_DESKTOP       = "extra_desktop";
    private static final String EXTRA_DEVICE_CONFIRM_STATE = "extra_desktop_confirm_state";
    private static final String EXTRA_FILE_IDS      = "extra_file_ids";

    public SocketTaskService() {
        super("SocketTaskService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            Log.w(TAG, "intent is null");
            return;
        }

        final String action = intent.getAction();
        if (ACTION_SEND_FILE.equals(action)) {
            final Uri uri = intent.getParcelableExtra(EXTRA_FILE_URI);
            final Desktop desktop = intent.getParcelableExtra(EXTRA_DESKTOP);
            handleActionSendFile(uri, desktop);
        } else if (ACTION_REQEST_SEND_FILE.equals(action)) {
            final Uri uri = intent.getParcelableExtra(EXTRA_FILE_URI);
            final Desktop desktop = intent.getParcelableExtra(EXTRA_DESKTOP);
            handleActionRequestSendFile(uri, desktop);
        } else if (ACTION_CONFIRM_DESKTOP_AUTH.equals(action)) {
            final Desktop desktop = intent.getParcelableExtra(EXTRA_DESKTOP);
            final boolean accept = intent.getBooleanExtra(EXTRA_DEVICE_CONFIRM_STATE, false);
            handleConfirmDesktopAuthRequest(desktop, accept);
        } else if (ACTION_CONFIRM_EXCHANGE_TCP_PORT.equals(action)) {
            final Desktop desktop = intent.getParcelableExtra(EXTRA_DESKTOP);
            handleConfirmExchangeTcpPort(desktop);
        } else if (ACTION_CONFIRM_SENDING_FILE_REQ.equals(action)) {
            final Desktop device = intent.getParcelableExtra(EXTRA_DESKTOP);
            final String[] fileIds = intent.getStringArrayExtra(EXTRA_FILE_IDS);
            final boolean accept = intent.getBooleanExtra(EXTRA_DEVICE_CONFIRM_STATE, false);
            handleConfirmSendingFileRequest(device, fileIds, accept);
        } else {
            Log.w(TAG, "unknown action: " + action);
        }
    }

    public static void startActionRequestSendFile(Context context, Uri fileUri, Desktop desktop) {
        Intent intent = new Intent(context, SocketTaskService.class);
        intent.setAction(ACTION_REQEST_SEND_FILE);
        intent.putExtra(EXTRA_FILE_URI, fileUri);
        intent.putExtra(EXTRA_DESKTOP, desktop);
        context.startService(intent);
    }

    public static void startActionSendFile(Context context, Uri fileUri, Desktop desktop) {
        Intent intent = new Intent(context, SocketTaskService.class);
        intent.setAction(ACTION_SEND_FILE);
        intent.putExtra(EXTRA_FILE_URI, fileUri);
        intent.putExtra(EXTRA_DESKTOP, desktop);
        context.startService(intent);
    }

    public static void confirmDesktopAuthRequest(Context context, Desktop desktop, boolean accept) {
        Intent intent = new Intent(context, SocketTaskService.class);
        intent.setAction(ACTION_CONFIRM_DESKTOP_AUTH);
        intent.putExtra(EXTRA_DESKTOP, desktop);
        intent.putExtra(EXTRA_DEVICE_CONFIRM_STATE, accept);
        context.startService(intent);
    }

    public static void confirmFileSendingRequest(Context context, Desktop device, String[] fileIDs,
                                                 boolean accept) {
        Intent intent = new Intent(context, SocketTaskService.class);
        intent.setAction(ACTION_CONFIRM_SENDING_FILE_REQ);
        intent.putExtra(EXTRA_DESKTOP, device);
        intent.putExtra(EXTRA_FILE_IDS, fileIDs);
        intent.putExtra(EXTRA_DEVICE_CONFIRM_STATE, accept);
        context.startService(intent);
    }

    public static void confirmExchangeTcpPort(Context context, Desktop desktop) {
        Intent intent = getServiceIntent(context, ACTION_CONFIRM_EXCHANGE_TCP_PORT);
        intent.putExtra(EXTRA_DESKTOP, desktop);
        context.startService(intent);
    }

    private static Intent getServiceIntent(Context context, String action) {
        Intent serviceIntent = new Intent(context, SocketTaskService.class);
        serviceIntent.setAction(action);
        return serviceIntent;
    }

    private void handleActionRequestSendFile(Uri fileUri, Desktop desktop) {
        RequestSendFileDataSource dataSource =
                new RequestSendFileDataSource(this, fileUri, desktop);
        TcpDataSender.sendDataSync(desktop.address, desktop.tcpPort, dataSource);
    }

    private void handleActionSendFile(Uri fileUri, Desktop desktop) {
        SendFileDataSource dataSource = new SendFileDataSource(this, fileUri, desktop);
        TcpDataSender.sendDataSync(desktop.address, desktop.tcpPort, dataSource);
        ResultBroadcastReceiver.nofitySendFileComplete(this);
    }

    private void handleConfirmDesktopAuthRequest(Desktop desktop, boolean accept) {
        ConfirmDesktopAuthRequestDataSource dataSource =
                new ConfirmDesktopAuthRequestDataSource(desktop, accept);
        UdpDataSender.sendData(desktop.address, desktop.udpPort, dataSource);
    }

    private void handleConfirmExchangeTcpPort(Desktop desktop) {
        ConfirmExchangeTcpPortDataSource dataSource = new ConfirmExchangeTcpPortDataSource(desktop);
        UdpDataSender.sendData(desktop.address, desktop.udpPort, dataSource);
    }

    private void handleConfirmSendingFileRequest(Desktop device, String[] fileIDs, boolean accept) {
        ConfirmSendingFileRequestDataSource dataSource =
                new ConfirmSendingFileRequestDataSource(device, fileIDs, accept);
        TcpDataSender.sendDataSync(device.address, device.tcpPort, dataSource);
    }
}
