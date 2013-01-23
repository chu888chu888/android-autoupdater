package com.github.snowdream.android.api.autoupdate;

import java.io.Serializable;


public class VersionInfo implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8160507600091925242L;
    
    /**
     * app name
     */
    private String appName = "";
    
    /**
     * app description
     */
    private String appDescription = "";

    /**
     * package name
     */
    private String packageName = "";
    
    /**
     * version code
     */
    private String versionCode = "";
    
    /**
     * version name
     */
    private String versionName = "";
    
    /**
     * whether to force update
     */
    private boolean forceUpdate = false;
  
    /**
     * the url for the new apk
     */
    private String apkUrl = "";
    
    /**
     * update tips
     */
    private String updateTips = "";

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the appDescription
     */
    public String getAppDescription() {
        return appDescription;
    }

    /**
     * @param appDescription the appDescription to set
     */
    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the versionCode
     */
    public String getVersionCode() {
        return versionCode;
    }

    /**
     * @param versionCode the versionCode to set
     */
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * @return the versionName
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * @param versionName the versionName to set
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * @return the forceUpdate
     */
    public boolean isForceUpdate() {
        return forceUpdate;
    }

    /**
     * @param forceUpdate the forceUpdate to set
     */
    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    /**
     * @return the apkUrl
     */
    public String getApkUrl() {
        return apkUrl;
    }

    /**
     * @param apkUrl the apkUrl to set
     */
    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    /**
     * @return the updateTips
     */
    public String getUpdateTips() {
        return updateTips;
    }

    /**
     * @param updateTips the updateTips to set
     */
    public void setUpdateTips(String updateTips) {
        this.updateTips = updateTips;
    }
}
