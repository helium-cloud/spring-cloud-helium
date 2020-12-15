package com.coral.learning.dubbo.api;

import java.io.Serializable;

public class MessageRequest implements Serializable {
    private String owner;
    private String tid;
    private String body;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
