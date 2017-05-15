package com.weichen2046.filesender2.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.weichen2046.filesender2.ui.BaseActivity;
import com.weichen2046.filesender2.ui.NotificationDialogHelperActivity;

import static com.weichen2046.filesender2.ui.NotificationDialogHelperActivity.EXTRA_DATA;

public class NotificationHelperReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isAppShowing = BaseActivity.isAppShowingUnlocked();
        if (isAppShowing) {
            Intent dialogActivity = new Intent(context, NotificationDialogHelperActivity.class);
            dialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogActivity.putExtra(EXTRA_DATA, (Bundle) intent.getExtras().clone());
            context.startActivity(dialogActivity);
        }
    }
}
