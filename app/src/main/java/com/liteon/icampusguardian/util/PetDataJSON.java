package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class PetDataJSON {

    @SerializedName("type")
    private String type;
    @SerializedName("action")
    private String action;
    @SerializedName("pettype")
    private String petType;
    @SerializedName("petname")
    private String petName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }
}
