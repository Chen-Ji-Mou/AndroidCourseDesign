package com.chenjimou.androidcoursedesign.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PictureFromDeviceModel implements Parcelable
{
    private long id;
    private String path;

    public PictureFromDeviceModel() { }

    protected PictureFromDeviceModel(Parcel in)
    {
        id = in.readLong();
        path = in.readString();
    }

    public static final Creator<PictureFromDeviceModel> CREATOR = new Creator<PictureFromDeviceModel>()
    {
        @Override
        public PictureFromDeviceModel createFromParcel(Parcel in)
        {
            return new PictureFromDeviceModel(in);
        }

        @Override
        public PictureFromDeviceModel[] newArray(int size)
        {
            return new PictureFromDeviceModel[size];
        }
    };

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(id);
        dest.writeString(path);
    }
}
