package com.weichen2046.filesender2.networkutils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created by chenwei on 12/4/16.
 */

public class NetworkAddressHelper {

    private static final String TAG = "NetworkAddressHelper";

    private static final Pattern sIPv4Patt = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$");

    /**
     * Judge a ip address string is IP v4 or not.
     *
     * @param address IP address string.
     * @return Return true if address is a IP v4 address.
     */
    public static boolean isIPv4Address(String address) {
        return null != address && sIPv4Patt.matcher(address).matches();
    }

    /**
     * Get local IP address.
     *
     * @param ipv4 Retrun IP v4 address if true, otherwise return IP v6 address.
     * @return Return local IP address or null.
     */
    public static String getIPAddress(boolean ipv4) {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            for (; en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses();
                for (; enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String addr = inetAddress.getHostAddress().toUpperCase();
                        if (ipv4 && isIPv4Address(addr)) {
                            return addr;
                        } else if (!ipv4 && !isIPv4Address(addr)) {
                            return addr;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Get local broadcast address.
     *
     * @return Return local broadcast address or null.
     */
    public static String getBroadcastAddress() {
        try {
            Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
            for (; niEnum.hasMoreElements(); ) {
                NetworkInterface ni = niEnum.nextElement();
                if (!ni.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                        InetAddress broadcastAdd = interfaceAddress.getBroadcast();
                        if (broadcastAdd != null) {
                            String broadcastStr = broadcastAdd.toString();
                            return broadcastStr.substring(1);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
