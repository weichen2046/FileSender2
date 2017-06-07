package com.weichen2046.filesender2.ui;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class NotificationDialogHelperActivity extends BaseActivity {
    private static final String TAG = "NotificationDialog";
    private BottomSheetDialog mBottomSheetDialog;

    public static final String EXTRA_DATA = "extra_data";
    public static final String ACTION_DISMISS_NOTIFICATION_DIALOG
            = "action.filesender2.DISMISS_NOTIFICATION_DIALOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DISMISS_NOTIFICATION_DIALOG);
        registerReceiver(mDismissReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mDismissReceiver);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Bundle data = getIntent().getExtras().getBundle(EXTRA_DATA);
        Desktop device = data.getParcelable(EXTRA_AUTH_DEVICE);
        final int msgType = data.getInt(EXTRA_MSG_TYPE, 0);
        switch (msgType) {
            case MSG_TYPE_AUTH:
                handleAuthRequest(device);
                break;
            case MSG_TYPE_RECV_FILE:
                String[] fileIDs = data.getStringArray(EXTRA_FILE_IDS);
                String[] fileNames = data.getStringArray(EXTRA_FILE_NAMES);
                handleFileSendingRequest(device, fileIDs, fileNames);
                break;
            default:
                Log.w(TAG, "unknow msg type: " + msgType);
                break;
        }

    }

    private void handleAuthRequest(final Desktop device) {
        Resources rs = getResources();
        mBottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.sheet_title);
        tvTitle.setText(R.string.auth_request_title);
        TextView tvContent = (TextView) dialogView.findViewById(R.id.sheet_content);
        tvContent.setText(String.format(rs.getString(R.string.fmt_auth_request_text),
                device.getAddress()));
        Button btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                NotificationManager nm
                        = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(NotificationHelper.NOTIFICATION_DEVICE_AUTH_REQ);
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // accept auth request
                Intent service = new Intent(NotificationDialogHelperActivity.this,
                        UserConfirmationHandleService.class);
                service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_AUTH);
                service.putExtra(EXTRA_ACCEPT_STATE, true);
                service.putExtra(EXTRA_AUTH_DEVICE, device);
                startService(service);
                mBottomSheetDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // denial auth request
                Intent service = new Intent(NotificationDialogHelperActivity.this,
                        UserConfirmationHandleService.class);
                service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_AUTH);
                service.putExtra(EXTRA_ACCEPT_STATE, false);
                service.putExtra(EXTRA_AUTH_DEVICE, device);
                startService(service);
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.show();
    }

    private void handleFileSendingRequest(final Desktop device, final String[] fileIDs,
                                          final String[] fileNames) {
        Resources rs = getResources();
        mBottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.sheet_title);
        tvTitle.setText(R.string.file_sending_request);
        String contentText = fileNames.length == 1
                        ? fileNames[0]
                        : String.format(rs.getString(R.string.fmt_file_to_recv), fileNames.length);
        TextView tvContent = (TextView) dialogView.findViewById(R.id.sheet_content);
        tvContent.setText(contentText);
        Button btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        if (fileNames.length > 1) {
            btnOk.setText(R.string.request_details);
        }
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                NotificationManager nm
                        = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(NotificationHelper.NOTIFICATION_RECV_FILE_REQ);
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileNames.length > 1) {
                    Intent detailsActivity = new Intent(NotificationDialogHelperActivity.this,
                            PendingRecvFilesActivity.class);
                    detailsActivity.putExtra(EXTRA_AUTH_DEVICE, device);
                    detailsActivity.putExtra(EXTRA_FILE_IDS, fileIDs);
                    detailsActivity.putExtra(EXTRA_FILE_NAMES, fileNames);
                    startActivity(detailsActivity);
                } else {
                    Intent service = new Intent(NotificationDialogHelperActivity.this,
                            UserConfirmationHandleService.class);
                    service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_RECV_FILE);
                    service.putExtra(EXTRA_ACCEPT_STATE, true);
                    service.putExtra(EXTRA_AUTH_DEVICE, device);
                    service.putExtra(EXTRA_FILE_IDS, fileIDs);
                    service.putExtra(EXTRA_FILE_NAMES, fileNames);
                    startService(service);
                }
                mBottomSheetDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(NotificationDialogHelperActivity.this,
                        UserConfirmationHandleService.class);
                service.putExtra(EXTRA_MSG_TYPE, MSG_TYPE_RECV_FILE);
                service.putExtra(EXTRA_ACCEPT_STATE, false);
                service.putExtra(EXTRA_AUTH_DEVICE, device);
                service.putExtra(EXTRA_FILE_IDS, fileIDs);
                service.putExtra(EXTRA_FILE_NAMES, fileNames);
                startService(service);
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.show();
    }

    private BroadcastReceiver mDismissReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBottomSheetDialog != null) {
                mBottomSheetDialog.dismiss();
                mBottomSheetDialog = null;
            }
        }
    };
}
