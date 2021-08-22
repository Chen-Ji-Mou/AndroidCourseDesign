package com.chenjimou.androidcoursedesign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPictureModel implements Serializable
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
        @JsonProperty("base64")
        private String base64;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getBase64()
        {
            return base64;
        }

        public void setBase64(String base64)
        {
            this.base64 = base64;
        }
    }
}
