package com.chenjimou.androidcoursedesign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostSpaceModel implements Serializable
{
    @JsonProperty("data")
    private DataDTO data;
    @JsonProperty("code")
    private int code;
    @JsonProperty("msg")
    private String msg;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataDTO implements Serializable
    {
        @JsonProperty("id")
        private String id;
    }
}
