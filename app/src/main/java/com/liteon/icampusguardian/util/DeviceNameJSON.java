package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class DeviceNameJSON {

    @SerializedName("type")
    public String type;

    @SerializedName("name")
    public String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
