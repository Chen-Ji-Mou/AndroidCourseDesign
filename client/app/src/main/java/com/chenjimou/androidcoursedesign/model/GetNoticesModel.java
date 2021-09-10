package com.chenjimou.androidcoursedesign.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetNoticesModel implements Serializable
{
    @JsonProperty("code")
    private int code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private List<DataDTO> data;

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public List<DataDTO> getData()
    {
        return data;
    }

    public void setData(List<DataDTO> data)
    {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataDTO implements Serializable, Parcelable, Comparable<GetNoticesModel.DataDTO>
    {
        @JsonProperty("id")
        private String id;
        @JsonProperty("sender")
        private String sender;
        @JsonProperty("content")
        private String content;
        @JsonProperty("receiver")
        private String receiver;
        @JsonProperty("read")
        private int read;
        @JsonProperty("date")
        private long date;
        @JsonProperty("postId")
        private String postId;

        protected DataDTO(Parcel in)
        {
            id = in.readString();
            sender = in.readString();
            content = in.readString();
            receiver = in.readString();
            read = in.readInt();
            date = in.readLong();
            postId = in.readString();
        }

        public static final Creator<DataDTO> CREATOR = new Creator<DataDTO>()
        {
            @Override
            public DataDTO createFromParcel(Parcel in)
            {
                return new DataDTO(in);
            }

            @Override
            public DataDTO[] newArray(int size)
            {
                return new DataDTO[size];
            }
        };

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getSender()
        {
            return sender;
        }

        public void setSender(String sender)
        {
            this.sender = sender;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public String getReceiver()
        {
            return receiver;
        }

        public void setReceiver(String receiver)
        {
            this.receiver = receiver;
        }

        public int getRead()
        {
            return read;
        }

        public void setRead(int read)
        {
            this.read = read;
        }

        public long getDate()
        {
            return date;
        }

        public void setDate(long date)
        {
            this.date = date;
        }

        public String getPostId()
        {
            return postId;
        }

        public void setPostId(String postId)
        {
            this.postId = postId;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(id);
            dest.writeString(sender);
            dest.writeString(content);
            dest.writeString(receiver);
            dest.writeInt(read);
            dest.writeLong(date);
            dest.writeString(postId);
        }

        @Override
        public int compareTo(DataDTO o)
        {
            // 降序排列
            return (int)(o.getDate() - this.getDate());
        }
    }
}
