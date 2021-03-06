package com.weichen2046.filesender2.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weichen2046.filesender2.R;
import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.RemoteDevicesManager;
import com.weichen2046.filesender2.service.IRemoteDevicesManager;
import com.weichen2046.filesender2.service.ITcpDataMonitor;
import com.weichen2046.filesender2.service.IUdpDataMonitor;
import com.weichen2046.filesender2.service.IRemoteDeviceDiscoverer;
import com.weichen2046.filesender2.service.ServiceManager;
import com.weichen2046.filesender2.service.SocketTaskService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private boolean mRegisteredReceiver = false;
    private IRemoteDeviceDiscoverer mDesktopDiscoverer = null;
    private IUdpDataMonitor mUdpDataMonitor = null;
    private ITcpDataMonitor mTcpDataMonitor = null;
    private IRemoteDevicesManager mDevicesManager = null;

    private TextView mEmptyView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    private static final int REQUEST_CODE_FOR_FILE_TO_SEND = 1;
    private Desktop mSelectedDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mEmptyView = (TextView) findViewById(R.id.empty_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mUdpDataMonitor = ServiceManager.INSTANCE.getUdpDataMonitor();
        mTcpDataMonitor = ServiceManager.INSTANCE.getTcpDataMonitor();
        mDesktopDiscoverer = ServiceManager.INSTANCE.getDeviceDiscoverer();
        mDevicesManager = ServiceManager.INSTANCE.getRemoteDevicesManager();
        try {
            mUdpDataMonitor.start();
            mTcpDataMonitor.start();
            mDesktopDiscoverer.sayHello(null, INetworkDefs.DESKTOP_UDP_LISTEN_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDevices();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent called.");
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_start_monitor) {
            if (mUdpDataMonitor != null) {
                try {
                    boolean res = mUdpDataMonitor.start();
                    Log.d(TAG, "start broadcast monitor: " + res);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (mTcpDataMonitor != null) {
                try {
                    mTcpDataMonitor.start();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if (id == R.id.action_stop_monitor) {
            if (mUdpDataMonitor != null) {
                try {
                    boolean res = mUdpDataMonitor.stop();
                    Log.d(TAG, "stop broadcast monitor: " + res);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (mTcpDataMonitor != null) {
                try {
                    mTcpDataMonitor.stop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_desktop_list) {
            Intent desktopListIntent = new Intent(this, DesktopListActivity.class);
            startActivity(desktopListIntent);
        } else if (id == R.id.nav_settings) {
            Log.d(TAG, "Settings menu clicked");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != mUdpDataMonitor) {
                mUdpDataMonitor.stop();
            }
            if (null != mTcpDataMonitor) {
                mTcpDataMonitor.stop();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mRegisteredReceiver) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDevices();
                }
            });
            mRegisteredReceiver = true;
            // register desktop change broadcast
            IntentFilter filter = new IntentFilter();
            filter.addAction(RemoteDevicesManager.ACTION_DESKTOP_CHANGES);
            registerReceiver(mReceiverForDesktop, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRegisteredReceiver) {
            mRegisteredReceiver = false;
            unregisterReceiver(mReceiverForDesktop);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_CODE_FOR_FILE_TO_SEND) {
            final Intent resultIntent = data;
            Uri uri = resultIntent.getData();
            SocketTaskService.startActionRequestSendFile(this, uri, mSelectedDevice);
        }
    }

    private BroadcastReceiver mReceiverForDesktop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateDevices();
        }
    };

    private void updateDevices() {
        if (mDevicesManager == null) {
            return;
        }
        try {
            List<Desktop> devices = mDevicesManager.getAllDesktops();
            // for debug
            if (false) {
                Desktop debug = new Desktop();
                debug.setAddress("10.101.2.248");
                for (int i=0; i<100; i++) {
                    devices.add(debug);
                }
            }
            if (devices.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            mAdapter.setData(devices);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<Desktop> mDesktops = new ArrayList<>();

        public void setData(List<Desktop> desktops) {
            mDesktops.clear();
            mDesktops.addAll(desktops);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_device_item,
                    parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Desktop desktop = mDesktops.get(position);
            Glide.with(MainActivity.this).load(R.drawable.material_design_demo_img).fitCenter().into(holder.mIcon);
            if (!TextUtils.isEmpty(desktop.getNickname())) {
                holder.mName.setText(desktop.getNickname());
            }
            if (!TextUtils.isEmpty(desktop.getAddress())) {
                holder.mIp.setText(desktop.getAddress());
            }
            holder.mShare.setEnabled(!TextUtils.isEmpty(desktop.getAuthToken()));
            holder.mShareListener.setDevice(desktop);
            holder.mShare.setOnClickListener(holder.mShareListener);
        }

        @Override
        public int getItemCount() {
            return mDesktops.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mIcon;
            public TextView mName;
            public TextView mIp;
            public Button mShare;
            public Button mDetails;

            public DeviceOnClickListener mShareListener;

            public class DeviceOnClickListener implements View.OnClickListener {

                private Desktop mDevice;

                public void setDevice(Desktop device) {
                    mDevice = device;
                }

                @Override
                public void onClick(View v) {
                    mSelectedDevice = mDevice;
                    Log.d(TAG, "clicked device: " + mDevice);
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
            }

            public ViewHolder(View view) {
                super(view);
                mShareListener = new DeviceOnClickListener();
                mIcon = (ImageView) view.findViewById(R.id.connected_device_icon);
                mName = (TextView) view.findViewById(R.id.connected_device_name);
                mIp = (TextView) view.findViewById(R.id.connected_device_address);
                mShare = (Button) view.findViewById(R.id.btn_connected_device_share);
                mDetails = (Button) view.findViewById(R.id.btn_connected_device_details);
            }
        }
    }
}
