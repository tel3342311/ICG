package com.liteon.icampusguardian.util;

import com.google.gson.annotations.SerializedName;

public class CustomSkinJSON {

    @SerializedName("type")
    private String type;

    @SerializedName("filetype")
    private String fileType;

    @SerializedName("filesize")
    private int fileSize;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
