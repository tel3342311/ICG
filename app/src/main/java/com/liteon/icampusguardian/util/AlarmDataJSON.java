package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class AlarmDataJSON {

    @SerializedName("type")
    public String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @SerializedName("data")
    public AlarmData[] alarmList;



    public AlarmData[] getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(AlarmData[] alarmList) {
        this.alarmList = alarmList;
    }

    public static class AlarmData {
        @SerializedName("alarmid")
        public String alarmId;

        @SerializedName("action")
        public String action;

        @SerializedName("hour")
        public String hour;

        @SerializedName("minutes")
        public String minutes;

        @SerializedName("repeat")
        public String repeat;

        @SerializedName("alarmtitle")
        public String alarmtitle;

        public String getAlarmId() {
            return alarmId;
        }

        public void setAlarmId(String alarmId) {
            this.alarmId = alarmId;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public String getMinutes() {
            return minutes;
        }

        public void setMinutes(String minutes) {
            this.minutes = minutes;
        }

        public String getRepeat() {
            return repeat;
        }

        public void setRepeat(String repeat) {
            this.repeat = repeat;
        }

        public String getAlarmtitle() {
            return alarmtitle;
        }

        public void setAlarmtitle(String alarmtitle) {
            this.alarmtitle = alarmtitle;
        }
    }
}


