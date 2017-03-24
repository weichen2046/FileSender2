package com.weichen2046.filesender2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.weichen2046.filesender2.network.INetworkDefs;
import com.weichen2046.filesender2.service.IBroadcastMonitor;
import com.weichen2046.filesender2.service.IPCDiscoverer;
import com.weichen2046.filesender2.service.IServiceManager;
import com.weichen2046.filesender2.service.ServiceManager;
import com.weichen2046.filesender2.ui.PcListActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private boolean mBoundToService = false;
    private IServiceManager mServiceManager = null;
    private IPCDiscoverer mPCDiscoverer = null;
    private IBroadcastMonitor mBroadcastMonitor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.action_start_bmonitor) {
            if (mBroadcastMonitor != null) {
                try {
                    boolean res = mBroadcastMonitor.start();
                    Log.d(TAG, "start broadcast monitor: " + res);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if (id == R.id.action_stop_bmonitor) {
            if (mBroadcastMonitor != null) {
                try {
                    boolean res = mBroadcastMonitor.stop();
                    Log.d(TAG, "stop broadcast monitor: " + res);
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

        if (id == R.id.nav_pc_list) {
            Intent pcListIntent = new Intent(this, PcListActivity.class);
            startActivity(pcListIntent);
        } else if (id == R.id.nav_choose_file) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
                if (null != mBroadcastMonitor) {
                    mBroadcastMonitor.stop();
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
                IBinder binder = mServiceManager.getService(ServiceManager.SERVICE_BROADCAST_MONITOR);
                mBroadcastMonitor = IBroadcastMonitor.Stub.asInterface(binder);
                mBroadcastMonitor.start();

                binder = mServiceManager.getService(ServiceManager.SERVICE_PC_DISCOVERER);
                mPCDiscoverer = IPCDiscoverer.Stub.asInterface(binder);
                mPCDiscoverer.sayHello(null, INetworkDefs.DESKTOP_UDP_LISTEN_PORT);
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
}
