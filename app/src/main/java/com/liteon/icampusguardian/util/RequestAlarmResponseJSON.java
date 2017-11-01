package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class RequestAlarmResponseJSON {

    @SerializedName("type")
    private String type;

    @SerializedName("ack")
    private String ack;

    @SerializedName("data")
    public AlarmDataJSON.AlarmData[] alarmList;


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

    public AlarmDataJSON.AlarmData[] getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(AlarmDataJSON.AlarmData[] alarmList) {
        this.alarmList = alarmList;
    }
}
