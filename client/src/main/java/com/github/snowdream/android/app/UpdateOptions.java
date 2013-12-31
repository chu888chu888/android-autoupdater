package com.github.snowdream.android.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.snowdream.android.app.R;

/**
 * Created by snowdream on 12/30/13.
 */
public final class UpdateOptions {
    private final UpdatePeriod updatePeriod;
    private final boolean forceUpdate;
    private final boolean checkUpdate;
    private final String checkUrl;
    private final Context context;

    private UpdateOptions(Builder builder) {
        this.updatePeriod = builder.updatePeriod;
        this.forceUpdate = builder.forceUpdate;
        this.checkUpdate = builder.checkUpdate;
        this.checkUrl = builder.checkUrl;
        this.context = builder.context;
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
        String PREFS_KEY = context.getResources().getString(R.string.preference_key_next_check_update_time);
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long next = sp.getLong(PREFS_KEY, -1);

        if (next == -1) {
            shouldCheckUpdate = true;
            return shouldCheckUpdate;
        }

        long period = 0;
        switch (updatePeriod) {
            case NEVER:
                period = -1;
                break;
            case EACH_ONE_DAY:
                period = 1 * 24 * 60 * 60 * 1000;
                break;
            case EACH_TWO_DAYS:
                period = 2 * 24 * 60 * 60 * 1000;
                break;
            case EACH_THREE_DAYS:
                period = 3 * 24 * 60 * 60 * 1000;
                break;
            case EACH_ONE_WEEK:
                period = 7 * 24 * 60 * 60 * 1000;
                break;
            case EACH_TWO_WEEKS:
                period = 14 * 24 * 60 * 60 * 1000;
                break;
            case EACH_THREE_WEEKS:
                period = 21 * 24 * 60 * 60 * 1000;
                break;
            case EACH_ONE_MONTH:
                period = 30 * 24 * 60 * 60 * 1000;
                break;
            case EACH_TIME:
            default:
                period = 0;
                break;
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
     * Get the check url
     *
     * @return
     */
    public String getCheckUrl() {
        return checkUrl;
    }

    public static class Builder {
        private UpdatePeriod updatePeriod = UpdatePeriod.EACH_TIME;
        private boolean forceUpdate = false;
        private String checkUrl = null;
        private Context context = null;
        private boolean checkUpdate = false;

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
        public Builder forceUpdate() {
            forceUpdate = true;
            return this;
        }

        /**
         * Should the client check for update?
         * You do not need call it Generally.
         *
         * @return
         */
        public Builder checkUpdate() {
            checkUpdate = true;
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
         * Builds configured {@link UpdateOptions} object
         */
        public UpdateOptions build() {
            return new UpdateOptions(this);
        }
    }
}
