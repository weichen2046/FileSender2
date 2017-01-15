// IDataTransfer.aidl
package com.weichen2046.filesender2.service;

//import android.net.Uri;

interface IDataTransfer {

    /**
     * Send file to PC specified by the destHost and destPort.
     * This call will use a TCP connection to send the file to PC.
     *
     * @param fileUri The uri of the file to be sent.
     * @param destHost The destination PC host address in String format.
     * @param destPort The destination PC listen port.
     */
    void sendFileToPc(in Uri fileUri, String destHost, int destPort);

    /**
     * Send the file sending request to PC for confirm. We will start to send file if PC confirmed
     * our sending request.
     *
     * @param fileUri The uri of the file to be sent.
     * @param destHost The destination PC host address in String format.
     * @param destPort The destination PC listen port.
     */
    void requestToSendFile(in Uri fileUri, String destHost, int destPort);
}
