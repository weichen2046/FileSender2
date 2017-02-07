package com.weichen2046.filesender2.utils;

import com.weichen2046.filesender2.utils.byteconvertor.BytesConvertor;
import com.weichen2046.filesender2.utils.byteconvertor.ByteConvertorChain;

/**
 * Created by chenwei on 2017/2/4.
 */

public abstract class ByteDataSource {
    protected static final String TAG = "ByteDataSource";

    private ByteConvertorChain mChain = new ByteConvertorChain();
    private BytesConvertor mConvertor;

    /**
     * Get bytes from the data source.
     *
     * @return bytes of data, null means all data read.
     */
    public final byte[] getData() {
        if (mConvertor == null) {
            return null;
        }
        byte[] data = mConvertor.getBytes();
        // current convertor finished, read data from next convertor
        if (data == null || data.length == 0) {
            nextConvertorAndInitUnchecked();
            return getData();
        }
        return data;
    }

    public final boolean init() {
        boolean res = onInit();
        // get the first convertor
        nextConvertorAndInitUnchecked();
        return res;
    }

    public final void destroy() {
        onDestroy();
    }

    public final void fillData(BytesConvertor convertor) {
        mChain.chain(convertor);
    }

    public boolean onInit() {
        return true;
    }

    public void onDestroy() {
    }

    private void nextConvertorAndInitUnchecked() {
        mConvertor = mChain.nextConvertor();
        if (mConvertor != null) {
            mConvertor.init();
        }
    }
}
