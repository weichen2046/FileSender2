package com.weichen2046.filesender2.utils.byteconvertor;

import android.content.Context;
import android.net.Uri;

/**
 * Created by chenwei on 2017/2/7.
 */

public class UriFileBytesConvertor extends BytesConvertor {
    private Context mContext;
    private Uri mFileUri;

    public UriFileBytesConvertor(Context context, Uri fileUri) {
        mContext = context;
        mFileUri = fileUri;
    }

    @Override
    public byte[] onGetBytes() {
        markEnded();
        return null;
    }
}
