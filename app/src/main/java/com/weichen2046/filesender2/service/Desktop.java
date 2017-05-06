package com.weichen2046.filesender2.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chenwei on 2017/3/18.
 */

public class Desktop implements Parcelable {
    public String nickname;
    public String address;
    public int udpPort;
    public int tcpPort;
    public String accessToken;
    public String authToken;

    public Desktop() {
    }

    protected Desktop(Parcel in) {
        nickname = in.readString();
        address = in.readString();
        udpPort = in.readInt();
        tcpPort = in.readInt();
        accessToken = in.readString();
        authToken = in.readString();
    }

    public static final Creator<Desktop> CREATOR = new Creator<Desktop>() {
        @Override
        public Desktop createFromParcel(Parcel in) {
            return new Desktop(in);
        }

        @Override
        public Desktop[] newArray(int size) {
            return new Desktop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickname);
        dest.writeString(address);
        dest.writeInt(udpPort);
        dest.writeInt(tcpPort);
        dest.writeString(accessToken);
        dest.writeString(authToken);
    }

    public void update(Desktop desktop) {
        nickname = desktop.nickname;
        address = desktop.address;
        udpPort = desktop.udpPort;
        tcpPort = desktop.tcpPort;
        accessToken = desktop.accessToken;
        authToken = desktop.authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        Desktop right = (Desktop) o;
        return udpPort == right.udpPort && address.equals(right.address);
    }

    @Override
    public String toString() {
        return String.format(
                "Desktop [ nickname: %s, address: %s, udpPort: %d, tcpPort: %d, accessToken: %s, authToken: %s ]",
                nickname, address, udpPort, tcpPort, accessToken, authToken);
    }
}
