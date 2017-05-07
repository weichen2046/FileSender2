package com.weichen2046.filesender2.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.weichen2046.filesender2.R;

public class ResultBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ResultBroadcastReceiver";

    private static final String ACTION_SEND_FILE_COMPLETE
            = "action.filesender2.SEND_FILE_COMPLETE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        switch (action) {
            case ACTION_SEND_FILE_COMPLETE:
                Toast.makeText(context, R.string.file_send_complete, Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d(TAG, "unknown action for receiver: " + action);
                break;
        }
    }

    public static void nofitySendFileComplete(Context context) {
        Intent intent = new Intent(ACTION_SEND_FILE_COMPLETE);
        context.sendBroadcast(intent);
    }
}
