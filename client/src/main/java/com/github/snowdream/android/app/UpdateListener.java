package com.github.snowdream.android.app;

import com.github.snowdream.android.util.concurrent.TaskListener;

/**
 * Created by snowdream on 12/30/13.
 */
public class UpdateListener extends TaskListener<Integer, UpdateInfo> {

    /**
     * show the update dialog
     *
     * @param info the info for the new app
     */
    public void onShowUpdateUI(UpdateInfo info) {
    }

    /**
     * It's the latest app,or there is no need to update.
     */
    public void onShowNoUpdateUI() {
    }

    /**
     * show the progress when downloading the new app
     */
    public void onShowUpdateProgressUI(DownloadTask task, int progress) {
    }

    /**
     * user click to confirm the update
     */
    public void informUpdate(UpdateInfo info) {
    }

    /**
     * user click to cancel the update
     */
    public void informCancel() {
    }

    /**
     * user click to skip the update
     */
    public void informSkip() {
    }

    /**
     * Exit the app here
     */
    public void ExitApp() {
    }
}
