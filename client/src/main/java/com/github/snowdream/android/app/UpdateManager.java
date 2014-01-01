package com.github.snowdream.android.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.snowdream.android.util.Log;
import com.github.snowdream.android.util.concurrent.AsyncTask;
import com.google.gson.Gson;


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
    private static final int MSG_SHOW_NO_UPDATE_UI = 2;
    /**
     * There is no need to update.
     */
    private static final int MSG_SHOW_UPDATE_UI = 3;
    /**
     * There is no need to update.
     */
    private static final int MSG_SHOW_UPDATE_PROGRESS_UI = 4;

    /**
     * click to update
     */
    private static final int MSG_UPDATE = 10;
    /**
     * click to cancel the update
     */
    private static final int MSG_CANCEL = 11;
    /**
     * click to skip the update
     */
    private static final int MSG_SKIP = 12;
    /**
     * error occurs
     */
    private static final int MSG_ERROR = 6;
    private Context context = null;
    private UpdateListener listener = null;
    private UpdateOptions options = null;
    private UIHandler handler = new UIHandler();
    private DownloadTask downloadTask = null;
    private UpdateListener updateListener = new UpdateListener() {
        @Override
        public void onShowUpdateUI(UpdateInfo info) {
            super.onShowUpdateUI(info);
        }

        @Override
        public void onShowNoUpdateUI() {
            super.onShowNoUpdateUI();
        }

        @Override
        public void onShowUpdateProgressUI(DownloadTask task, int progress) {
            super.onShowUpdateProgressUI(task, progress);
        }

        @Override
        public void informSkip() {
            super.informSkip();
        }

        @Override
        public void informUpdate(UpdateInfo info) {
            if (info == null) {
                return;
            }

            DownloadManager manager = new DownloadManager(context);

            downloadTask = new DownloadTask(context);
            downloadTask.setUrl(info.getApkUrl());

            manager.add(downloadTask, new DownloadListener<Integer, DownloadTask>() {
                @Override
                public void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                    handler.obtainMessage(
                            MSG_SHOW_UPDATE_PROGRESS_UI, values[0], -1);
                }

                @Override
                public void onSuccess(DownloadTask downloadTask) {
                    super.onSuccess(downloadTask);
                    if (downloadTask != null && !TextUtils.isEmpty(downloadTask.getPath())) {
                        install(context, downloadTask.getPath());
                    }
                }
            });
        }

        @Override
        public void informCancel() {
        }

        @Override
        public void ExitApp() {
            if (context != null) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    };

    /**
     * install the apk
     *
     * @param context
     * @param filePath
     */
    private static void install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

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
                    MSG_SHOW_NO_UPDATE_UI);
        }
    }

    private class AsycCheckUpdateTask extends AsyncTask<String, Integer, UpdateInfo> {
        private AsycCheckUpdateTask() {
        }

        public AsycCheckUpdateTask(UpdateListener listener) {
            super(listener);
        }

        @Override
        protected UpdateInfo doInBackground(String... urls) {
            if (urls.length <= 0) {
                Log.e("There is no url.");
                handler.obtainMessage(
                        MSG_SHOW_NO_UPDATE_UI);
                return null;
            }

            String url = urls[0];
            if (URLUtil.isNetworkUrl(url)) {
                Log.e("There is no url.");
                handler.obtainMessage(
                        MSG_SHOW_NO_UPDATE_UI);
                return null;
            }

            UpdateInfo info = null;
            String xml = null;
            String json = null;
            switch (options.getUpdateFormat()) {
                case XML:
                    xml = HttpRequest.get(url).followRedirects(true).accept("application/xml").acceptCharset(HttpRequest.CHARSET_UTF8).body(HttpRequest.CHARSET_UTF8);

                    if (!TextUtils.isEmpty(xml)) {
                        // XMLSerializer xmlSerializer = new XMLSerializer();
                        // json = xmlSerializer.read( xml );
                    }
                    break;
                case JSON:
                default:
                    json = HttpRequest.get(url).followRedirects(true).accept("application/json").acceptCharset(HttpRequest.CHARSET_UTF8).body(HttpRequest.CHARSET_UTF8);
                    Gson gson = new Gson();
                    info = gson.fromJson(json, UpdateInfo.class);
                    break;
            }

            return info;
        }

        @Override
        protected void onPostExecute(UpdateInfo updateInfo) {
            super.onPostExecute(updateInfo);
            if (context != null && updateInfo != null) {
                try {
                    PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    Integer versionCode = pinfo.versionCode; // 1
                    String versionName = pinfo.versionName; // 1.0
                    String packageName = context.getPackageName();

                    if (options.shouldCheckPackageName() && !updateInfo.getPackageName().equals(packageName)) {
                        ((UpdateListener) listener).onShowNoUpdateUI();
                        return;
                    }

                    if (Integer.parseInt(updateInfo.getVersionCode()) > versionCode) {
                        ((UpdateListener) listener).onShowUpdateUI(updateInfo);
                    } else {
                        ((UpdateListener) listener).onShowNoUpdateUI();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.e("can not get the package info", e);
                }
            }
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
                case MSG_SHOW_NO_UPDATE_UI:
                    listener.onShowNoUpdateUI();
                    break;
                case MSG_SHOW_UPDATE_PROGRESS_UI:
                    if (downloadTask != null) {
                        listener.onShowUpdateProgressUI(downloadTask, msg.arg1);
                    }
                    break;
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
