package com.weichen2046.filesender2.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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
import com.weichen2046.filesender2.network.Pc;
import com.weichen2046.filesender2.network.PcManager;
import com.weichen2046.filesender2.service.IDataTransfer;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;

public class PcListActivity extends AppCompatActivity {

    private static final String TAG = "PcListActivity";

    private ListView mPcListView;
    private IServiceManager mServiceManager;
    private IDataTransfer mTransfer;

    private boolean mBoundToService;

    private Pc mSelectedPc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_list);

        mPcListView = (ListView) findViewById(R.id.pc_list);

        BaseAdapter adapter = new PcAdapter(this, R.layout.pc_list_item);

        mPcListView.setAdapter(adapter);
        mPcListView.setOnItemClickListener(mItemClickListener);

        Intent intent = new Intent(this, ServiceManager.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
            mSelectedPc = PcManager.getInstance().getPc(pos);

            Intent openFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                openFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            } else {
                openFile = new Intent(Intent.ACTION_GET_CONTENT);
            }
            openFile.setType("*/*");
            openFile.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(openFile, 1);
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_CANCELED) {
            return;
        }

        final Intent resultIntent = data;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Uri uri = resultIntent.getData();
                    // TODO: store uri to local database, store selected pc information also
                    mTransfer.requestToSendFile(uri, mSelectedPc.addr.getHostAddress(), mSelectedPc.listenPort);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBoundToService) {
            mBoundToService = false;
            unbindService(mServiceConnection);
        }
    }

    private static class PcAdapter extends BaseAdapter {

        private PcManager mManager = PcManager.getInstance();
        private LayoutInflater mInflater = null;
        private int mResourceId = 0;

        public PcAdapter(Context context, int resId) {
            mInflater = LayoutInflater.from(context);
            mResourceId = resId;
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount called, count: " + mManager.getCount());
            return mManager.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mManager.getPc(position);
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
            Pc pc = (Pc) getItem(position);
            holder.mIcon.setImageResource(R.drawable.ic_menu_share);
            holder.mName.setText(pc.name);
            holder.mIp.setText(pc.addr.toString());
            Log.d(TAG, "getView called, pc.ip: " + holder.mName);
            return view;
        }

        private static class ViewHolder {
            ImageView mIcon;
            TextView mName;
            TextView mIp;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mBoundToService = true;
            mServiceManager = IServiceManager.Stub.asInterface(service);

            try {
                IBinder binder = mServiceManager.getService(ServiceManager.SERVICE_DATA_TRANSFER);
                mTransfer = IDataTransfer.Stub.asInterface(binder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}
