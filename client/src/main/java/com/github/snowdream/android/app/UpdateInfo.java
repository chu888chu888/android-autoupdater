package com.github.snowdream.android.app;

import java.util.Map;

/**
 * Created by snowdream on 12/31/13.
 */
public class UpdateInfo {
    private String appName = null;
    private String appDescription = null;
    private String packageName = null;
    private String versionCode = null;
    private String versionName = null;
    private boolean forceUpdate = false;
    private boolean autoUpdate = false;
    private String apkUrl = null;
    private Map<String, String> updateTips = null;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public Map<String, String> getUpdateTips() {
        return updateTips;
    }

    public void setUpdateTips(Map<String, String> updateTips) {
        this.updateTips = updateTips;
    }
}
