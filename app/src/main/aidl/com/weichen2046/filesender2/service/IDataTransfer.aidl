// IDataTransfer.aidl
package com.weichen2046.filesender2.service;

//import android.net.Uri;

interface IDataTransfer {

    /**
     * Send file to PC specified by the destHost and destPort.
     * This call will use a TCP connection to send the file to PC.
     *
     * @param fileUri The file to be sent.
     * @param destHost The destination PC host address in String format.
     * @param destHost The destination PC listen port.
     */
    void sendFileToPc(in Uri fileUri, String destHost, int destPort);
}
