package com.github.snowdream.android.app;

import android.content.Context;
import android.content.Intent;

/**
 * Created by snowdream on 1/2/14.
 */
public class DefaultUpdateListener extends AbstractUpdateListener {

    @Override
    public void onShowUpdateUI(UpdateInfo info) {

    }

    @Override
    public void onShowNoUpdateUI() {

    }

    @Override
    public void onShowUpdateProgressUI(DownloadTask task, int progress) {

    }

    @Override
    public void ExitApp() {
        Context context = getContext();
        if (context != null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
