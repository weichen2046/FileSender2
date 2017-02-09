package com.weichen2046.filesender2.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.utils.RequestSendFileDataSource;
import com.weichen2046.filesender2.utils.SendFileDataSource;
import com.weichen2046.filesender2.utils.UdpDataSender;

public class SendFileService extends IntentService {
    private static final String ACTION_SEND_FILE
            = "com.weichen2046.filesender2.service.action.SEND_FILE";
    private static final String ACTION_REQEST_SEND_FILE
            = "com.weichen2046.filesender2.service.action.REQUEST_SEND_FILE";

    private static final String EXTRA_FILE_URI = "com.weichen2046.filesender2.service.extra.FILE_URI";
    private static final String EXTRA_DEST_HOST = "com.weichen2046.filesender2.service.extra.DEST_HOST";
    private static final String EXTRA_DEST_PORT = "com.weichen2046.filesender2.service.extra.DEST_PORT";

    public SendFileService() {
        super("SendFileService");
    }

    public static void startActionSendFile(Context context, Uri fileUri, String host, int port) {
        Intent intent = new Intent(context, SendFileService.class);
        intent.setAction(ACTION_SEND_FILE);
        intent.putExtra(EXTRA_FILE_URI, fileUri);
        intent.putExtra(EXTRA_DEST_HOST, host);
        intent.putExtra(EXTRA_DEST_PORT, port);
        context.startService(intent);
    }


    public static void startActionRequestSendFile(Context context, Uri fileUri, String host, int port) {
        Intent intent = new Intent(context, SendFileService.class);
        intent.setAction(ACTION_REQEST_SEND_FILE);
        intent.putExtra(EXTRA_FILE_URI, fileUri);
        intent.putExtra(EXTRA_DEST_HOST, host);
        intent.putExtra(EXTRA_DEST_PORT, port);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
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
            }
        }
    }

    private void handleActionSendFile(Uri fileUri, String destHost, int destPort) {
        SendFileDataSource dataSource = new SendFileDataSource(this, fileUri);
        UdpDataSender.sendData(destHost, destPort, dataSource);
    }

    private void handleActionRequestSendFile(Uri fileUri, String destHost, int destPort) {
        RequestSendFileDataSource dataSource =
                new RequestSendFileDataSource(this, fileUri, destHost, destPort);
        UdpDataSender.sendData(destHost, destPort, dataSource);
    }
}
