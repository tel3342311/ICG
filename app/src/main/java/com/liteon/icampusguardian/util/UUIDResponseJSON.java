package com.liteon.icampusguardian.util;


import com.google.gson.annotations.SerializedName;

public class UUIDResponseJSON {

    @SerializedName("type")
    private String type;

    @SerializedName("ack")
    private String ack;

    @SerializedName("uuid")
    private String uuid;

    public String getType() {
        return type;
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setType(String type) {

        this.type = type;
    }
}
