package com.github.snowdream.android.app;

/**
 * Created by snowdream on 1/3/14.
 */
public abstract class AbstractParser {
    public final static String TAG_UPDATE_INFO = "updateInfo";
    public final static String TAG_APP_NAME = "appName";
    public final static String TAG_APP_DESCRIPTION = "appDescription";
    public final static String TAG_PACKAGE_NAME = "packageName";
    public final static String TAG_VERSION_CODE = "versionCode";
    public final static String TAG_VERSION_NAME = "versionName";
    public final static String TAG_FORCE_UPDATE = "forceUpdate";
    public final static String TAG_AUTO_UPDATE = "autoUpdate";
    public final static String TAG_APK_URL = "apkUrl";
    public final static String TAG_UPDATE_TIPS = "updateTips";


    /**
     * Parse the UpdateInfo form the string
     *
     * @return UpdateInfo
     * @throws UpdateException
     */
    public abstract UpdateInfo parse(String content) throws UpdateException;
}
