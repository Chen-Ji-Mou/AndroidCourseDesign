package com.chenjimou.androidcoursedesign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadNoticeModel implements Serializable
{
    @JsonProperty("data")
    private Object data;
    @JsonProperty("code")
    private int code;
    @JsonProperty("msg")
    private String msg;
}
