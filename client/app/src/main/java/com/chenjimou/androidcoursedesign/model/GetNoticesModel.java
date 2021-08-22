package com.chenjimou.androidcoursedesign.model;

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
    public static class DataDTO implements Serializable
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
    }
}
