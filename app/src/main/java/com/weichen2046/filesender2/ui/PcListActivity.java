package com.weichen2046.filesender2.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weichen2046.filesender2.R;
import com.weichen2046.filesender2.network.PcData;
import com.weichen2046.filesender2.network.PcManager;

public class PcListActivity extends AppCompatActivity {

    private static final String TAG = "PcListActivity";

    private ListView mPcListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_list);

        mPcListView = (ListView) findViewById(R.id.pc_list);

        BaseAdapter adapter = new PcAdapter(this, R.layout.pc_list_item);

        mPcListView.setAdapter(adapter);
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
            PcData pc = (PcData) getItem(position);
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
}
