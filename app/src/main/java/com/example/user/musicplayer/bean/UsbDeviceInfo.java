package com.example.user.musicplayer.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright  : 2015-2033 Beijing Startimes Communication & Network Technology Co.Ltd
 * <p/>
 * Created by xiongl on 2016/9/10..
 * ClassName  :
 * Description  :
 */
public class UsbDeviceInfo implements Parcelable {
    private String absolutePath;
    private String fileName;

    public UsbDeviceInfo(String absolutePath, String fileName) {
        this.absolutePath = absolutePath;
        this.fileName = fileName;
    }

    protected UsbDeviceInfo(Parcel in) {
        absolutePath = in.readString();
        fileName = in.readString();
    }

    public static final Creator<UsbDeviceInfo> CREATOR = new Creator<UsbDeviceInfo>() {
        @Override
        public UsbDeviceInfo createFromParcel(Parcel in) {
            return new UsbDeviceInfo(in);
        }

        @Override
        public UsbDeviceInfo[] newArray(int size) {
            return new UsbDeviceInfo[size];
        }
    };

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(absolutePath);
        dest.writeString(fileName);
    }

    @Override
    public String toString() {
        return "UsbDeviceInfo{" +
                "absolutePath='" + absolutePath + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

