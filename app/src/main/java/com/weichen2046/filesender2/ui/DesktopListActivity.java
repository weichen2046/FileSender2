package com.weichen2046.filesender2.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weichen2046.filesender2.R;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.RemoteDevicesManager;
import com.weichen2046.filesender2.service.IRemoteDevicesManager;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManagerInternal;
import com.weichen2046.filesender2.service.SocketTaskService;

import java.util.ArrayList;
import java.util.List;

public class DesktopListActivity extends BaseActivity {

    private static final String TAG = "DesktopListActivity";

    private IServiceManager mServiceManager;
    private IRemoteDevicesManager mDevicesManager;
    private boolean mBoundToService;

    private ListView mDesktopListView;
    private DesktopAdapter mDesktopAdapter;

    private ProgressDialog mDialog = null;
    private static final int PROGRESS_DIALOG_DELAY = 200;
    private static final int PROGRESS_DIALOG_TIMEOUT = 10 * 1000;
    private static final int MSG_SHOW_PROGRESS_DIALOG = 1;
    private static final int MSG_PROGRESS_DIALOG_TIMEOUT = 2;

    private static final int REQUEST_CODE_FOR_FILE_TO_SEND = 1;

    private Desktop mSelectedDesktop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop_list);

        mDesktopAdapter = new DesktopAdapter(this, R.layout.desktop_list_item);
        mDesktopListView = (ListView) findViewById(R.id.desktop_list);
        mDesktopListView.setAdapter(mDesktopAdapter);
        mDesktopListView.setOnItemClickListener(mItemClickListener);

        Intent intent = new Intent(this, ServiceManagerInternal.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        Message msg = mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG);
        mHandler.sendMessageDelayed(msg, PROGRESS_DIALOG_DELAY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.setFlags(upIntent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, upIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            mSelectedDesktop = (Desktop) mDesktopAdapter.getItem(pos);
            Intent openFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                openFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            } else {
                openFile = new Intent(Intent.ACTION_GET_CONTENT);
            }
            openFile.setType("*/*");
            openFile.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(openFile, REQUEST_CODE_FOR_FILE_TO_SEND);
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_CODE_FOR_FILE_TO_SEND) {
            final Intent resultIntent = data;
            Uri uri = resultIntent.getData();
            SocketTaskService.startActionRequestSendFile(this, uri, mSelectedDesktop);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register desktop change broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(RemoteDevicesManager.ACTION_DESKTOP_CHANGES);
        registerReceiver(mReceiverForDesktop, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister desktop change broadcast
        unregisterReceiver(mReceiverForDesktop);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBoundToService) {
            mBoundToService = false;
            unbindService(mServiceConnection);
        }

        if (mDialog != null) {
            mHandler.removeMessages(MSG_PROGRESS_DIALOG_TIMEOUT);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private BroadcastReceiver mReceiverForDesktop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mDevicesManager == null) {
                return;
            }
            try {
                mDesktopAdapter.setData(mDevicesManager.getAllDesktops());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundToService = true;
            mServiceManager = IServiceManager.Stub.asInterface(service);

            try {
                mDevicesManager = IRemoteDevicesManager.Stub.asInterface(
                        mServiceManager.getService(ServiceManagerInternal.SERVICE_DEVICES_MANAGER));
                mDesktopAdapter.setData(mDevicesManager.getAllDesktops());

                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                } else {
                    mHandler.removeMessages(MSG_SHOW_PROGRESS_DIALOG);
                }

                // TODO: register desktop change notify
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_PROGRESS_DIALOG:
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    mDialog = new ProgressDialog(DesktopListActivity.this);
                    mDialog.setTitle(R.string.loading_desktop_title);
                    String message = getResources().getString(R.string.loading_desktop_message);
                    mDialog.setMessage(message);
                    mDialog.setIndeterminate(true);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setCancelable(false);
                    mDialog.show();
                    Message dismissMsg = mHandler.obtainMessage(MSG_PROGRESS_DIALOG_TIMEOUT);
                    mHandler.sendMessageDelayed(dismissMsg, PROGRESS_DIALOG_TIMEOUT);
                    break;
                case MSG_PROGRESS_DIALOG_TIMEOUT:
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    break;
            }
        }
    };

    private static class DesktopAdapter extends BaseAdapter {

        private ArrayList<Desktop> mDesktops = new ArrayList<>();
        private LayoutInflater mInflater = null;
        private int mResourceId = 0;

        public DesktopAdapter(Context context, int resId) {
            mInflater = LayoutInflater.from(context);
            mResourceId = resId;
        }

        public void setData(List<Desktop> desktops) {
            mDesktops.clear();
            mDesktops.addAll(desktops);
            // for debug
            if (false) {
                Desktop debug = new Desktop();
                debug.setAddress("10.101.2.248");
                for (int i=0; i<100; i++) {
                    mDesktops.add(debug);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount called, count: " + mDesktops.size());
            return mDesktops.size();
        }

        @Override
        public Object getItem(int position) {
            return mDesktops.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (null == convertView) {
                view = mInflater.inflate(mResourceId, null);
                holder = new ViewHolder();
                holder.mIcon = (ImageView) view.findViewById(R.id.icon);
                holder.mName = (TextView) view.findViewById(R.id.name);
                holder.mIp = (TextView) view.findViewById(R.id.ip);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            Desktop desktop = (Desktop) getItem(position);
            holder.mIcon.setImageResource(R.drawable.ic_menu_share);
            holder.mName.setText(desktop.getAddress());
            holder.mIp.setText(desktop.getAddress());
            Log.d(TAG, "getView called, desktop.ip: " + holder.mName);
            return view;
        }

        private static class ViewHolder {
            ImageView mIcon;
            TextView mName;
            TextView mIp;
        }
    }
}
