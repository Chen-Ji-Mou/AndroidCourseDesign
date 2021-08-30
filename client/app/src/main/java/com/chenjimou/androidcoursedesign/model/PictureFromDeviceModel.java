package com.chenjimou.androidcoursedesign.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PictureFromDeviceModel implements Parcelable
{
    private int id;
    private String path;
    private String name;

    public PictureFromDeviceModel() { }

    protected PictureFromDeviceModel(Parcel in)
    {
        id = in.readInt();
        path = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeString(name);
    }

    @Override
    public int describeContents()
    {
        return 0;
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

    public int getId()
    {
        return id;
    }

    public void setId(int id)
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
}
