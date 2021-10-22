package com.chenjimou.androidcoursedesign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCommentsModel implements Serializable
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
    public static class DataDTO implements Serializable, Comparable<GetCommentsModel.DataDTO>
    {
        @JsonProperty("id")
        private String id;
        @JsonProperty("content")
        private String content;
        @JsonProperty("date")
        private long date;
        @JsonProperty("postId")
        private String postId;
        @JsonProperty("userId")
        private String userId;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
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

        @Override
        public int compareTo(DataDTO o)
        {
            // 降序排列
            if (o.getDate() - this.getDate() >= 0)
                return 1;
            else
                return -1;
        }
    }
}
