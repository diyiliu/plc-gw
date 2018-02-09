package com.tiza.support.model;

/**
 * Description: DeviceInfo
 * Author: DIYILIU
 * Update: 2018-01-30 11:13
 */
public class DeviceInfo {

    private int id;
    private String dtuId;
    private String softVersion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDtuId() {
        return dtuId;
    }

    public void setDtuId(String dtuId) {
        this.dtuId = dtuId;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }
}
