package com.weichen2046.filesender2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.DesktopManager;
import com.weichen2046.filesender2.service.IDesktopManager;
import com.weichen2046.filesender2.service.ITcpDataMonitor;
import com.weichen2046.filesender2.service.IUdpDataMonitor;
import com.weichen2046.filesender2.service.IDesktopDiscoverer;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;
import com.weichen2046.filesender2.ui.DesktopListActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private boolean mBoundToService = false;
    private IServiceManager mServiceManager = null;
    private IDesktopDiscoverer mDesktopDiscoverer = null;
    private IUdpDataMonitor mUdpDataMonitor = null;
    private ITcpDataMonitor mTcpDataMonitor = null;
    private IDesktopManager mDesktopManager = null;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

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

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        Intent intent = new Intent(this, ServiceManager.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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

        if (mBoundToService) {
            mBoundToService = false;
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

            unbindService(mServiceConnection);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mBoundToService = true;
            mServiceManager = IServiceManager.Stub.asInterface(service);

            MyApplication.getInstance().setServiceManager(mServiceManager);

            try {
                IBinder binder = mServiceManager.getService(ServiceManager.SERVICE_UDP_DATA_MONITOR);
                mUdpDataMonitor = IUdpDataMonitor.Stub.asInterface(binder);
                mUdpDataMonitor.start();

                binder = mServiceManager.getService(ServiceManager.SERVICE_TCP_DATA_MONITOR);
                mTcpDataMonitor = ITcpDataMonitor.Stub.asInterface(binder);
                mTcpDataMonitor.start();

                binder = mServiceManager.getService(ServiceManager.SERVICE_DESKTOP_DISCOVERER);
                mDesktopDiscoverer = IDesktopDiscoverer.Stub.asInterface(binder);
                mDesktopDiscoverer.sayHello(null, INetworkDefs.DESKTOP_UDP_LISTEN_PORT);

                binder = mServiceManager.getService(ServiceManager.SERVICE_DESKTOP_MANAGER);
                mDesktopManager = IDesktopManager.Stub.asInterface(binder);
                List<Desktop> desktops = mDesktopManager.getAllDesktops();

                // for debug
                if (false) {
                    Desktop debug = new Desktop();
                    debug.address = "10.101.2.248";
                    for (int i=0; i<100; i++) {
                        desktops.add(debug);
                    }
                }

                mAdapter.setData(desktops);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            MyApplication.getInstance().setServiceManager(null);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiverForDesktop);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // register desktop change broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(DesktopManager.ACTION_DESKTOP_CHANGES);
        registerReceiver(mReceiverForDesktop, filter);
    }

    private BroadcastReceiver mReceiverForDesktop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mDesktopManager == null) {
                return;
            }
            try {
                mAdapter.setData(mDesktopManager.getAllDesktops());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<Desktop> mDesktops = new ArrayList<>();

        public void setData(List<Desktop> desktops) {
            mDesktops.clear();
            mDesktops.addAll(desktops);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.desktop_list_item3,
                    parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Desktop desktop = mDesktops.get(position);
            holder.mIcon.setImageResource(R.drawable.ic_menu_share);
            holder.mName.setText(desktop.address);
            holder.mIp.setText(desktop.address);
        }

        @Override
        public int getItemCount() {
            return mDesktops.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mIcon;
            public TextView mName;
            public TextView mIp;
            public ViewHolder(View view) {
                super(view);
                mIcon = (ImageView) view.findViewById(R.id.icon);
                mName = (TextView) view.findViewById(R.id.name);
                mIp = (TextView) view.findViewById(R.id.ip);
            }
        }
    }
}
