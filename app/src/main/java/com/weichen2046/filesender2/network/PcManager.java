package com.weichen2046.filesender2.network;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by chenwei on 12/10/16.
 */

public class PcManager {

    private ArrayList<PcData> mPcs = new ArrayList<>();

    private static class SingletonHolder {
        public static PcManager INSTANCE = new PcManager();
    }

    public static PcManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Add new pc or replace the exist pc with the new one.
     *
     * @param pc The new pc to add.
     * @return The new added pc or the replaced pc.
     */
    public PcData add(PcData pc) {
        int index = this.mPcs.indexOf(pc);
        if (index == -1) {
            mPcs.add(pc);
            return pc;
        } else {
            PcData old = mPcs.get(index);
            mPcs.set(index, pc);
            return old;
        }
    }

    /**
     * Remove pc by address of {@code InetAddress}.
     *
     * @param addr {@code InetAddress} format address.
     * @return The removed pc or null.
     */
    public PcData remove(InetAddress addr) {
        PcData pc = null;
        int index = findByAddr(addr);
        if (index != -1) {
            pc = mPcs.get(index);
        }
        return pc;
    }

    /**
     * Remove pc by address of {@code String}.
     *
     * @param addr {@code String} format address.
     * @return The removed pc or null.
     */
    public PcData remove(String addr) {
        PcData pc = null;
        int index = findByAddr(addr);
        if (index != -1) {
            pc = mPcs.get(index);
        }
        return pc;
    }

    /**
     * Clear all added pcs.
     */
    public void clear() {
        mPcs.clear();
    }

    /**
     * Get the pc's count.
     *
     * @return The pc's count.
     */
    public int getCount() {
        return mPcs.size();
    }

    public PcData getPc(int position) {
        return mPcs.get(position);
    }

    private int findByAddr(InetAddress addr) {
        for (int i = 0; i < mPcs.size(); i++) {
            if (mPcs.get(i).addr.equals(addr)) {
                return i;
            }
        }
        return -1;
    }

    private int findByAddr(String addr) {
        for (int i = 0; i < mPcs.size(); i++) {
            if (mPcs.get(i).addr.getHostAddress().equals(addr)) {
                return i;
            }
        }
        return -1;
    }

}
