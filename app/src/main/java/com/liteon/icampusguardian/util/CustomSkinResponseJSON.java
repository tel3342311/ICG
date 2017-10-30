package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class CustomSkinResponseJSON {

    @SerializedName("type")
    private String type;

    @SerializedName("ack")
    private String ack;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }
}
