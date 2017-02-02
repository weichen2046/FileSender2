package com.weichen2046.filesender2.db;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenwei on 2017/1/31.
 */

public class FileSendingObj {
    public long id;
    public String uuid;
    public String uri;
    public String host;
    public int port;
    public long createtimestamp;

    private Uri mUri;
    private Date mCreateDate;
    private SimpleDateFormat mDateFormat;

    public Date getCreateDate() {
        if (mCreateDate == null) {
            mCreateDate = new Date(createtimestamp);
        }
        return mCreateDate;
    }

    public Uri getUri() {
        if (mUri == null) {
            mUri = Uri.parse(uri);
        }
        return mUri;
    }

    @Override
    public String toString() {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
        return String.format("id: %s, uuid: %s, uri: %s, host: %s, port: %s, createtime: %s", id,
                uuid, uri, host, port, mDateFormat.format(getCreateDate()));
    }
}
