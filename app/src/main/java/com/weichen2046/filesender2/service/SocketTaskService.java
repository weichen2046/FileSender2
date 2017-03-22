package com.weichen2046.filesender2.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.utils.ConfirmDesktopAuthRequestDataSource;
import com.weichen2046.filesender2.utils.ConfirmExchangeTcpPortDataSource;
import com.weichen2046.filesender2.utils.RequestSendFileDataSource;
import com.weichen2046.filesender2.utils.SendFileDataSource;
import com.weichen2046.filesender2.utils.TcpDataSender;
import com.weichen2046.filesender2.utils.UdpDataSender;

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

    private static final String EXTRA_FILE_URI      = "extra_file_uri";
    private static final String EXTRA_DEST_HOST     = "extra_dest_host";
    private static final String EXTRA_DEST_PORT     = "extra_dest_port";
    private static final String EXTRA_DESKTOP       = "extra_desktop";
    private static final String EXTRA_DESKTOP_CONFIRM_STATE = "extra_desktop_confirm_state";

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
            final String host = intent.getStringExtra(EXTRA_DEST_HOST);
            final int port = intent.getIntExtra(EXTRA_DEST_PORT, INetworkDefs.DEFAULT_DESKTOP_TCP_PORT);
            handleActionSendFile(uri, host, port);
        } else if (ACTION_REQEST_SEND_FILE.equals(action)) {
            final Uri uri = intent.getParcelableExtra(EXTRA_FILE_URI);
            final String host = intent.getStringExtra(EXTRA_DEST_HOST);
            final int port = intent.getIntExtra(EXTRA_DEST_PORT, INetworkDefs.DEFAULT_DESKTOP_TCP_PORT);
            handleActionRequestSendFile(uri, host, port);
        } else if (ACTION_CONFIRM_DESKTOP_AUTH.equals(action)) {
            final Desktop desktop = intent.getParcelableExtra(EXTRA_DESKTOP);
            final boolean accept = intent.getBooleanExtra(EXTRA_DESKTOP_CONFIRM_STATE, false);
            handleConfirmDesktopAuthRequest(desktop, accept);
        } else if (ACTION_CONFIRM_EXCHANGE_TCP_PORT.equals(action)) {
            final Desktop desktop = intent.getParcelableExtra(EXTRA_DESKTOP);
            handleConfirmExchangeTcpPort(desktop);
        } else {
            Log.w(TAG, "unknown action: " + action);
        }
    }

    public static void startActionRequestSendFile(Context context, Uri fileUri, String host, int port) {
        Intent intent = new Intent(context, SocketTaskService.class);
        intent.setAction(ACTION_REQEST_SEND_FILE);
        intent.putExtra(EXTRA_FILE_URI, fileUri);
        intent.putExtra(EXTRA_DEST_HOST, host);
        intent.putExtra(EXTRA_DEST_PORT, port);
        context.startService(intent);
    }

    public static void startActionSendFile(Context context, Uri fileUri, String host, int port) {
        Intent intent = new Intent(context, SocketTaskService.class);
        intent.setAction(ACTION_SEND_FILE);
        intent.putExtra(EXTRA_FILE_URI, fileUri);
        intent.putExtra(EXTRA_DEST_HOST, host);
        intent.putExtra(EXTRA_DEST_PORT, port);
        context.startService(intent);
    }

    public static void confirmDesktopAuthRequest(Context context, Desktop desktop, boolean accept) {
        Intent intent = new Intent(context, SocketTaskService.class);
        intent.setAction(ACTION_CONFIRM_DESKTOP_AUTH);
        intent.putExtra(EXTRA_DESKTOP, desktop);
        intent.putExtra(EXTRA_DESKTOP_CONFIRM_STATE, accept);
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

    private void handleActionRequestSendFile(Uri fileUri, String destHost, int destPort) {
        RequestSendFileDataSource dataSource =
                new RequestSendFileDataSource(this, fileUri, destHost, destPort);
        TcpDataSender.sendData(destHost, destPort, dataSource);
    }

    private void handleActionSendFile(Uri fileUri, String destHost, int destPort) {
        SendFileDataSource dataSource = new SendFileDataSource(this, fileUri);
        TcpDataSender.sendData(destHost, destPort, dataSource);
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
}
