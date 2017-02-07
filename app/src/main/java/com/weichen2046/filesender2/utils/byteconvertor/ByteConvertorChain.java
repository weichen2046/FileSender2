package com.weichen2046.filesender2.utils.byteconvertor;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by chenwei on 2017/2/7.
 */

public class ByteConvertorChain {
    private ListIterator<BytesConvertor> mIt;
    private LinkedList<BytesConvertor> mChain = new LinkedList<>();

    public void chain(BytesConvertor convertor) {
        mChain.add(convertor);
    }

    public BytesConvertor nextConvertor() {
        if (mIt == null) {
            mIt = mChain.listIterator();
        }
        if (mIt.hasNext()) {
            return mIt.next();
        }
        return null;
    }
}
