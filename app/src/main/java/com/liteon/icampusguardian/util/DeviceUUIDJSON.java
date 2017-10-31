package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class DeviceUUIDJSON {

    @SerializedName("type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
