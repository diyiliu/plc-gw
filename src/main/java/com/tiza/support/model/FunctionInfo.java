package com.tiza.support.model;

import java.util.List;
import java.util.Map;

/**
 * Description: FunctionInfo
 * Author: DIYILIU
 * Update: 2016-04-21 11:25
 */
public class FunctionInfo {

    private String softVersion;
    private String softName;
    private String modelCode;
    private String functionXml;

    private Map<String, CanPackage> canPackages;

    // can包 packageId 长度（占字节数）
    private int pidLength;

    public FunctionInfo() {

    }

    public FunctionInfo(String softVersion, String softName, String modelCode, String functionXml) {
        this.softVersion = softVersion;
        this.softName = softName;
        this.modelCode = modelCode;
        this.functionXml = functionXml;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public String getSoftName() {
        return softName;
    }

    public void setSoftName(String softName) {
        this.softName = softName;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getFunctionXml() {
        return functionXml;
    }

    public void setFunctionXml(String functionXml) {
        this.functionXml = functionXml;
    }

    public Map<String, CanPackage> getCanPackages() {
        return canPackages;
    }

    public void setCanPackages(Map<String, CanPackage> canPackages) {
        this.canPackages = canPackages;
    }

    public int getPidLength() {
        return pidLength;
    }

    public void setPidLength(int pidLength) {
        this.pidLength = pidLength;
    }
}
