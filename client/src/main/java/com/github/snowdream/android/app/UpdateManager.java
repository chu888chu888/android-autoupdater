package com.github.snowdream.android.app;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.URLUtil;

import com.github.snowdream.android.util.Log;
import com.github.snowdream.android.util.concurrent.AsyncTask;

/**
 * Created by snowdream on 12/30/13.
 */
public class UpdateManager {
    /**
     * Start checking for update
     */
    private static final int MSG_START = 0;
    /**
     * Finish checking for update
     */
    private static final int MSG_FINISH = 1;
    /**
     * There is no need to update.
     */
    private static final int MSG_NO_NEED_UPDATE = 2;
    /**
     * click to update
     */
    private static final int MSG_UPDATE = 3;
    /**
     * click to cancel the update
     */
    private static final int MSG_CANCEL = 4;
    /**
     * click to skip the update
     */
    private static final int MSG_SKIP = 5;
    /**
     * error occurs
     */
    private static final int MSG_ERROR = 6;
    private Context context = null;
    private UpdateListener listener = null;
    private UpdateOptions options = null;
    private UIHandler handler = new UIHandler();
    private UpdateListener updateListener = new UpdateListener() {

    };

    private UpdateManager() {
    }

    public UpdateManager(Context context) {
        super();
        this.context = context;
    }

    public void check(Context context, UpdateOptions options) {
        check(context, options, updateListener);
    }

    public void check(Context context, UpdateOptions options, UpdateListener listener) {
        if (this.context == null && context == null) {
            Log.w("The Context is NUll!");
            handler.obtainMessage(
                    MSG_ERROR,
                    new UpdateException(UpdateException.CONTEXT_NOT_VALID));
            return;
        }

        if (listener == null) {
            this.listener = updateListener;
        }

        if (options == null) {
            Log.w("The UpdateOptions is NUll!");
            handler.obtainMessage(
                    MSG_ERROR,
                    new UpdateException(UpdateException.UPDATE_OPTIONS_NOT_VALID));
            return;
        }

        if (options.shouldCheckUpdate()) {
            AsycCheckUpdateTask checkUpdateTask = new AsycCheckUpdateTask(listener);
            checkUpdateTask.execute(options.getCheckUrl());
        } else {
            handler.obtainMessage(
                    MSG_NO_NEED_UPDATE);
        }
    }

    private class AsycCheckUpdateTask extends AsyncTask<String, Integer, UpdateInfo> {
        private AsycCheckUpdateTask() {
        }

        public AsycCheckUpdateTask(UpdateListener<Integer, UpdateInfo> listener) {
            super(listener);
        }

        @Override
        protected UpdateInfo doInBackground(String... urls) {
            if (urls.length <= 0) {
                Log.e("There is no url.");
                handler.obtainMessage(
                        MSG_NO_NEED_UPDATE);
                return null;
            }

            String url = urls[0];
            if (URLUtil.isNetworkUrl(url)){
                Log.e("There is no url.");
                handler.obtainMessage(
                        MSG_NO_NEED_UPDATE);
                return null;
            };



            return null;
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    listener.onStart();
                    break;
                case MSG_ERROR:
                    if (msg.obj != null) {
                        listener.onError((Throwable) msg.obj);
                    }
                    break;
                case MSG_FINISH:
                    listener.onFinish();
                    break;
                case MSG_NO_NEED_UPDATE:
                    listener.onShowNoUpdateUI();
                case MSG_UPDATE:
                    break;
                case MSG_CANCEL:
                    break;
                case MSG_SKIP:
                    break;
                default:
                    break;
            }
        }
    }
}
