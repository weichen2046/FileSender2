package com.weichen2046.filesender2.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.weichen2046.filesender2.networklib.NetworkAddressHelper;
import com.weichen2046.filesender2.ui.receiver.ResultBroadcastReceiver;
import com.weichen2046.filesender2.utils.udpdatasource.ConfirmDesktopAuthRequestDataSource;
import com.weichen2046.filesender2.utils.udpdatasource.ConfirmExchangeTcpPortDataSource;
import com.weichen2046.filesender2.utils.tcpdatasource.RequestSendFileDataSource;
import com.weichen2046.filesender2.utils.tcpdatasource.SendFileDataSource;
import com.weichen2046.filesender2.utils.TcpDataSender;
import com.weichen2046.filesender2.utils.UdpDataSender;
import com.weichen2046.filesender2.utils.tcpdatasource.ConfirmSendingFileRequestDataSource;
import com.weichen2046.filesender2.utils.udpdatasource.SayHelloDataSource;

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
    private static final String ACTION_SAY_HELLO_DATA
            = "action.filesender2.ACTION_SAY_HELLO_DATA";

    private static final String EXTRA_FILE_URI      = "extra_file_uri";
    private static final String EXTRA_DEST_HOST     = "extra_dest_host";
    private static final String EXTRA_DEST_PORT     = "extra_dest_port";
    private static final String EXTRA_DESKTOP       = "extra_desktop";
    private static final String EXTRA_DEVICE_CONFIRM_STATE = "extra_desktop_confirm_state";
    private static final String EXTRA_FILE_IDS      = "extra_file_ids";
    private static final String EXTRA_ADDRESS       = "extra_listen_address";
    private static final String EXTRA_UDP_PORT      = "extra_udp_port";
    private static final String EXTRA_AUTH_TOKEN    = "extra_auth_token";

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
        } else if (ACTION_SAY_HELLO_DATA.equals(action)) {
            final String address = intent.getStringExtra(EXTRA_ADDRESS);
            final int udpPort = intent.getIntExtra(EXTRA_UDP_PORT, 0);
            final String tmpAuthToken = intent.getStringExtra(EXTRA_AUTH_TOKEN);
            handleSayHello(address, udpPort, tmpAuthToken);
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

    public static void sayHello(Context context, String listenAddress, int udpListenPort,
                                String tmpAuthToken) {
        Intent intent = getServiceIntent(context, ACTION_SAY_HELLO_DATA);
        intent.putExtra(EXTRA_ADDRESS, listenAddress);
        intent.putExtra(EXTRA_UDP_PORT, udpListenPort);
        intent.putExtra(EXTRA_AUTH_TOKEN, tmpAuthToken);
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
        TcpDataSender.sendDataSync(desktop.getAddress(), desktop.getTcpPort(), dataSource);
    }

    private void handleActionSendFile(Uri fileUri, Desktop desktop) {
        SendFileDataSource dataSource = new SendFileDataSource(this, fileUri, desktop);
        TcpDataSender.sendDataSync(desktop.getAddress(), desktop.getTcpPort(), dataSource);
        ResultBroadcastReceiver.nofitySendFileComplete(this);
    }

    private void handleConfirmDesktopAuthRequest(Desktop desktop, boolean accept) {
        ConfirmDesktopAuthRequestDataSource dataSource =
                new ConfirmDesktopAuthRequestDataSource(desktop, accept);
        UdpDataSender.sendData(desktop.getAddress(), desktop.getUdpPort(), dataSource);
    }

    private void handleConfirmExchangeTcpPort(Desktop desktop) {
        ConfirmExchangeTcpPortDataSource dataSource = new ConfirmExchangeTcpPortDataSource(desktop);
        UdpDataSender.sendData(desktop.getAddress(), desktop.getUdpPort(), dataSource);
    }

    private void handleConfirmSendingFileRequest(Desktop device, String[] fileIDs, boolean accept) {
        ConfirmSendingFileRequestDataSource dataSource =
                new ConfirmSendingFileRequestDataSource(device, fileIDs, accept);
        TcpDataSender.sendDataSync(device.getAddress(), device.getTcpPort(), dataSource);
    }

    private void handleSayHello(String address, int udpPort, String tempAuthToken) {
        String destAddress = address;
        if (TextUtils.isEmpty(destAddress)) {
            destAddress =  NetworkAddressHelper.getBroadcastAddress();
        }
        if (TextUtils.isEmpty(destAddress)) {
            Log.w(TAG, "say hello failed, can not get destination address");
            return;
        }
        SayHelloDataSource dataSource = new SayHelloDataSource(tempAuthToken);
        UdpDataSender.sendData(destAddress, udpPort, dataSource);
    }
}
