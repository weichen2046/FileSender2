package com.weichen2046.filesender2.network;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by chenwei on 12/10/16.
 */

public class DesktopManager {

    private ArrayList<DesktopMachine> mDesktops = new ArrayList<>();

    private static class SingletonHolder {
        public static DesktopManager INSTANCE = new DesktopManager();
    }

    public static DesktopManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Add new desktop or replace the exist desktop with the new one.
     *
     * @param desktop The new desktop to add.
     * @return The new added desktop or the replaced desktop.
     */
    public DesktopMachine add(DesktopMachine desktop) {
        int index = this.mDesktops.indexOf(desktop);
        if (index == -1) {
            mDesktops.add(desktop);
            return desktop;
        } else {
            DesktopMachine old = mDesktops.get(index);
            mDesktops.set(index, desktop);
            return old;
        }
    }

    /**
     * Remove pc by address of {@code InetAddress}.
     *
     * @param addr {@code InetAddress} format address.
     * @return The removed pc or null.
     */
    public DesktopMachine remove(InetAddress addr) {
        DesktopMachine desktop = null;
        int index = findByAddr(addr);
        if (index != -1) {
            desktop = mDesktops.remove(index);
        }
        return desktop;
    }

    /**
     * Remove pc by address of {@code String}.
     *
     * @param addr {@code String} format address.
     * @return The removed pc or null.
     */
    public DesktopMachine remove(String addr) {
        DesktopMachine desktop = null;
        int index = findByAddr(addr);
        if (index != -1) {
            desktop = mDesktops.remove(index);
        }
        return desktop;
    }

    /**
     * Clear all added pcs.
     */
    public void clear() {
        mDesktops.clear();
    }

    /**
     * Get the pc's count.
     *
     * @return The pc's count.
     */
    public int getCount() {
        return mDesktops.size();
    }

    public DesktopMachine getDesktopMachine(int position) {
        return mDesktops.get(position);
    }

    private int findByAddr(InetAddress addr) {
        for (int i = 0; i < mDesktops.size(); i++) {
            if (mDesktops.get(i).addr.equals(addr)) {
                return i;
            }
        }
        return -1;
    }

    private int findByAddr(String addr) {
        for (int i = 0; i < mDesktops.size(); i++) {
            if (mDesktops.get(i).addr.getHostAddress().equals(addr)) {
                return i;
            }
        }
        return -1;
    }

}
