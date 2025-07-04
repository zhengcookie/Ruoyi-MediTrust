package com.ruoyi.system.domain;

import java.math.BigInteger;

public class TraceInfo {
    private String traceNumber;
    private String traceId;
    private String sender;
    private String receiver;
    private BigInteger sendTime;
    private BigInteger receiveTime;
    private String state;
    private String method;


    public String getTraceNumber() {
        return traceNumber;
    }

    public void setTraceNumber(String traceNumber) {
        this.traceNumber = traceNumber;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public BigInteger getSendTime() {
        return sendTime;
    }

    public void setSendTime(BigInteger sendTime) {
        this.sendTime = sendTime;
    }

    public BigInteger getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(BigInteger receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


}
