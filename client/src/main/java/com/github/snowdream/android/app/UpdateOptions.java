package com.github.snowdream.android.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by snowdream on 12/30/13.
 */
public final class UpdateOptions {
    private final UpdatePeriod updatePeriod;
    private final boolean forceUpdate;
    private final boolean autoUpdate;
    private final boolean checkUpdate;
    private final boolean checkPackageName;
    private final String checkUrl;
    private final Context context;
    private final UpdateFormat updateFormat;

    private UpdateOptions(Builder builder) {
        this.updatePeriod = builder.updatePeriod;
        this.forceUpdate = builder.forceUpdate;
        this.checkUpdate = builder.checkUpdate;
        this.autoUpdate = builder.autoUpdate;
        this.checkPackageName = builder.checkPackageName;
        this.checkUrl = builder.checkUrl;
        this.context = builder.context;
        this.updateFormat = builder.updateFormat;
    }

    /**
     * Should the Client check for update?
     *
     * @return
     */
    public boolean shouldCheckUpdate() {
        boolean shouldCheckUpdate = false;

        if (checkUpdate) {
            shouldCheckUpdate = true;
            return shouldCheckUpdate;
        }

        if (context == null) {
            shouldCheckUpdate = false;
            return shouldCheckUpdate;
        }

        long now = System.currentTimeMillis();

        String PREFS_NAME = context.getResources().getString(R.string.preference_name);
        String PREFS_KEY_NEXT_CHECK_UPDATE_TIME = context.getResources().getString(R.string.preference_key_next_check_update_time);
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long next = sp.getLong(PREFS_KEY_NEXT_CHECK_UPDATE_TIME, -1);

        if (next == -1) {
            shouldCheckUpdate = true;
            return shouldCheckUpdate;
        }

        long period = 0;

        if (updatePeriod != null){
            period = updatePeriod.getPeriodMillis();
        }

        if (period == 0) { //each time
            shouldCheckUpdate = true;
        } else if (now + period >= next) { //It's time to check for update.
            shouldCheckUpdate = true;
        } else {
            shouldCheckUpdate = false;
        }

        return shouldCheckUpdate;
    }

    /**
     * Should the client force update
     *
     * @return
     */
    public boolean shouldForceUpdate() {
        return forceUpdate;
    }

    /**
     * Should the client auto update
     *
     * @return
     */
    public boolean shouldAutoUpdate() {
        return autoUpdate;
    }

    /**
     * Should the client check PackageName
     *
     * @return
     */
    public boolean shouldCheckPackageName() {
        return checkPackageName;
    }

    /**
     * Get the check url
     *
     * @return
     */
    public String getCheckUrl() {
        return checkUrl;
    }

    /**
     * Get the update period
     *
     * @return
     */
    public UpdatePeriod getUpdatePeriod() {
        return updatePeriod;
    }

    /**
     * Get the update format
     *
     * @return
     */
    public UpdateFormat getUpdateFormat() {
        return updateFormat;
    }

    public static class Builder {
        private UpdatePeriod updatePeriod = new UpdatePeriod(UpdatePeriod.EACH_TIME);
        private boolean forceUpdate = false;
        private boolean autoUpdate = false;
        private String checkUrl = null;
        private Context context = null;
        private boolean checkUpdate = false;
        private boolean checkPackageName = true;
        private UpdateFormat updateFormat = UpdateFormat.JSON;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * Set the period {@link com.github.snowdream.android.app.UpdatePeriod}
         *
         * @param updatePeriod
         * @return Builder
         */
        public Builder updatePeriod(UpdatePeriod updatePeriod) {
            this.updatePeriod = updatePeriod;
            return this;
        }

        /**
         * if new app is available, and the forceupdate is true,then the user must upgrade,
         * otherwise the app is not available.
         *
         * @return Builder
         */
        public Builder forceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
            return this;
        }

        /**
         * if new app is available, and the autoUpdate is true,then the client
         * will check update and upgrade automatically.
         *
         * @return Builder
         */
        public Builder autoUpdate(boolean autoUpdate) {
            this.autoUpdate = autoUpdate;
            return this;
        }

        /**
         * Should the client check for update?
         * You do not need call it Generally.
         *
         * @return
         */
        public Builder checkUpdate(boolean checkUpdate) {
            this.checkUpdate = checkUpdate;
            return this;
        }

        /**
         * Where to check for update.<br/>
         * For example:http://helloworld-snowdream.herokuapp.com/update.xml
         *
         * @param checkUrl
         * @return Builder
         */
        public Builder checkUrl(String checkUrl) {
            this.checkUrl = checkUrl;
            return this;
        }

        /**
         * Whether check the package name. if true,then the package name of the local app should be the same as
         * new app.if not set,true default.
         * @param checkPackageName
         * @return
         */
        public Builder checkPackageName(boolean checkPackageName){
            this.checkPackageName = checkPackageName;
            return this;
        }

        /**
         * Set the format  {@link com.github.snowdream.android.app.UpdateFormat}
         *
         * @param updateFormat
         * @return
         */
        public Builder updateFormat(UpdateFormat updateFormat) {
            this.updateFormat = updateFormat;
            return this;
        }


        /**
         * Builds configured {@link UpdateOptions} object
         */
        public UpdateOptions build() {
            return new UpdateOptions(this);
        }
    }
}
