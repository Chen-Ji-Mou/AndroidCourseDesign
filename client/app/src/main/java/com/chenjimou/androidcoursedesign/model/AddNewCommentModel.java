package com.chenjimou.androidcoursedesign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddNewCommentModel implements Serializable
{
    @JsonProperty("data")
    private DataDTO data;
    @JsonProperty("code")
    private int code;
    @JsonProperty("msg")
    private String msg;

    public DataDTO getData()
    {
        return data;
    }

    public void setData(DataDTO data)
    {
        this.data = data;
    }

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataDTO implements Serializable
    {
        @JsonProperty("id")
        private String id;
        @JsonProperty("postId")
        private String postId;
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("content")
        private String content;
        @JsonProperty("date")
        private long date;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getPostId()
        {
            return postId;
        }

        public void setPostId(String postId)
        {
            this.postId = postId;
        }

        public String getUserId()
        {
            return userId;
        }

        public void setUserId(String userId)
        {
            this.userId = userId;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public long getDate()
        {
            return date;
        }

        public void setDate(long date)
        {
            this.date = date;
        }
    }
}
