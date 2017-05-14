package com.weichen2046.filesender2.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.weichen2046.filesender2.MyApplication;
import com.weichen2046.filesender2.R;
import com.weichen2046.filesender2.service.Desktop;
import com.weichen2046.filesender2.service.SocketTaskService;
import com.weichen2046.filesender2.utils.NotificationHelper;

import java.util.ArrayList;

import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_AUTH_DEVICE;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_FILE_IDS;
import static com.weichen2046.filesender2.service.UserConfirmationHandleService.EXTRA_FILE_NAMES;

public class PendingRecvFilesActivity extends AppCompatActivity {

    private static final String TAG = "PendingRecvFiles";

    private Desktop mDevice;
    private String[] mFileIDs;
    private String[] mFileNames;
    private ArrayList<String> mSelectedFiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_recv_files);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmRecvFiles();
                finish();
            }
        });

        // dismiss notification
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NotificationHelper.NOTIFICATION_RECV_FILE_REQ);

        Intent startIntent = getIntent();
        if (startIntent == null) {
            Log.w(TAG, "start intent is null");
            finish();
            return;
        }
        Bundle bundle = startIntent.getExtras();
        if (bundle == null) {
            Log.w(TAG, "bundle data is null");
            finish();
            return;
        }
        mDevice = bundle.getParcelable(EXTRA_AUTH_DEVICE);
        mFileIDs = bundle.getStringArray(EXTRA_FILE_IDS);
        mFileNames = bundle.getStringArray(EXTRA_FILE_NAMES);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pending_recv_files, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_denial_all:
                denialAllFiles();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void denialAllFiles() {
        SocketTaskService.confirmFileSendingRequest(MyApplication.getInstance(), mDevice, mFileIDs,
                false);
        finish();
    }

    private void confirmRecvFiles() {
        // if selected files size equals 0, send denial message back to remote device
        // else send selected files id back to remote device
        String[] fileIds = mSelectedFiles.toArray(new String[0]);
        if (fileIds.length == 0) {
            denialAllFiles();
            return;
        }
        SocketTaskService.confirmFileSendingRequest(MyApplication.getInstance(), mDevice, fileIds,
                true);
        finish();
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiving_file_item,
                    parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            String fileName = mFileNames[position];
            holder.mFileName.setText(fileName);
            holder.mFileId = mFileIDs[position];
        }

        @Override
        public int getItemCount() {
            return mFileNames != null ? mFileNames.length : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public CheckBox mSelected;
            public TextView mFileName;
            public String mFileId;

            public ViewHolder(View itemView) {
                super(itemView);
                mSelected = (CheckBox) itemView.findViewById(R.id.file_selected);
                mFileName = (TextView) itemView.findViewById(R.id.file_name);

                mSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked && !mSelectedFiles.contains(mFileId)) {
                            mSelectedFiles.add(mFileId);
                            return;
                        }
                        if (!isChecked && mSelectedFiles.contains(mFileId)) {
                            mSelectedFiles.remove(mFileId);
                            return;
                        }
                    }
                });
            }
        }
    }

}
