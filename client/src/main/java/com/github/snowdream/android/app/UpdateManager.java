package com.github.snowdream.android.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.JsonSyntaxException;


/**
 * Created by snowdream on 12/30/13.
 */
public class UpdateManager {
    /**
     * Start checking for update
     */
    protected static final int MSG_START = 0;
    /**
     * Finish checking for update
     */
    protected static final int MSG_FINISH = 1;
    /**
     * There is no need to update.
     */
    protected static final int MSG_SHOW_NO_UPDATE_UI = 2;
    /**
     * There is no need to update.
     */
    protected static final int MSG_SHOW_UPDATE_UI = 3;
    /**
     * There is no need to update.
     */
    protected static final int MSG_SHOW_UPDATE_PROGRESS_UI = 4;

    /**
     * click to update
     */
    protected static final int MSG_INFORM_UPDATE = 10;
    /**
     * click to cancel the update
     */
    protected static final int MSG_INFORM_CANCEL = 11;
    /**
     * click to skip the update
     */
    protected static final int MSG_INFORM_SKIP = 12;
    /**
     * error occurs
     */
    protected static final int MSG_ERROR = 6;
    private Context context = null;
    private AbstractUpdateListener listener = null;
    private UpdateOptions options = null;
    private UIHandler handler = new UIHandler();
    private DownloadTask downloadTask = null;

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
        check(context, options, null);
    }

    public void check(Context context, UpdateOptions options, AbstractUpdateListener listener) {
        if (context != null) {
            this.context = context;
        }

        if (this.context == null) {
            Log.w("The Context is NUll!");
            handler.obtainMessage(
                    MSG_ERROR,
                    new UpdateException(UpdateException.CONTEXT_NOT_VALID)).sendToTarget();
            return;
        }

        if (listener == null) {
            listener = new DefaultUpdateListener();
        }
        this.listener = listener;

        if (options == null) {
            Log.w("The UpdateOptions is NUll!");
            handler.obtainMessage(
                    MSG_ERROR,
                    new UpdateException(UpdateException.UPDATE_OPTIONS_NOT_VALID)).sendToTarget();
            return;
        }
        this.options = options;

        //set the handler and the update options to the AbstractUpdateListener,etc
        listener.setContext(context);
        listener.setHandler(handler);
        listener.setUpdateOptions(options);

        if (options.shouldCheckUpdate()) {
            AsycCheckUpdateTask checkUpdateTask = new AsycCheckUpdateTask(listener);
            checkUpdateTask.execute(options.getCheckUrl());
        } else {
            handler.obtainMessage(
                    MSG_SHOW_NO_UPDATE_UI).sendToTarget();
        }
    }

    private class AsycCheckUpdateTask extends AsyncTask<String, Integer, UpdateInfo> {
        private AsycCheckUpdateTask() {
        }

        public AsycCheckUpdateTask(AbstractUpdateListener listener) {
            super(listener);
        }

        @Override
        protected UpdateInfo doInBackground(String... urls) {
            if (urls.length <= 0) {
                Log.e("There is no url.");
                handler.obtainMessage(
                        MSG_SHOW_NO_UPDATE_UI).sendToTarget();
                return null;
            }

            String url = urls[0];
            if (!URLUtil.isNetworkUrl(url)) {
                Log.e("There is no url.");
                handler.obtainMessage(
                        MSG_SHOW_NO_UPDATE_UI).sendToTarget();
                return null;
            }

            UpdateInfo info = null;
            String xml = null;
            String json = null;
            switch (options.getUpdateFormat()) {
                case XML:
                    try {
                        xml = HttpRequest.get(url)
                                .followRedirects(true)
                                .accept("application/xml")
                                .acceptCharset(HttpRequest.CHARSET_UTF8)
                                .trustAllCerts()
                                .trustAllHosts()
                                .body(HttpRequest.CHARSET_UTF8);

                        UpdateXmlParser xmlParser = new UpdateXmlParser();
                        info = xmlParser.parse(xml);
                    } catch (HttpRequest.HttpRequestException e) {
                        e.printStackTrace();
                        Log.e("HttpRequest.HttpRequestExceptio", e);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        Log.e("JsonSyntaxException", e);
                    } catch (UpdateException e) {
                        e.printStackTrace();
                        Log.e("UpdateException", e);
                    }
                    break;
                case JSON:
                default:
                    try {
                        json = HttpRequest.get(url)
                                .followRedirects(true)
                                .accept("application/json")
                                .acceptCharset(HttpRequest.CHARSET_UTF8)
                                .trustAllCerts()
                                .trustAllHosts()
                                .body(HttpRequest.CHARSET_UTF8);

                        UpdateJsonParser jsonParser = new UpdateJsonParser();
                        info = jsonParser.parse(json);
                    } catch (HttpRequest.HttpRequestException e) {
                        e.printStackTrace();
                        Log.e("HttpRequest.HttpRequestExceptio", e);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        Log.e("JsonSyntaxException", e);
                    } catch (UpdateException e) {
                        e.printStackTrace();
                        Log.e("UpdateException", e);
                    }
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

                    if (options.shouldCheckPackageName() && !packageName.equals(updateInfo.getPackageName())) {
                        ((AbstractUpdateListener) listener).onShowNoUpdateUI();
                        return;
                    }
                    String PREFS_NAME = context.getResources().getString(R.string.preference_name);
                    String PREFS_KEY_SKIP_CHECK_UPDATE_VERSION_CODE = context.getResources().getString(R.string.preference_key_skip_check_update_version_code);
                    SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String skip_version_code = sp.getString(PREFS_KEY_SKIP_CHECK_UPDATE_VERSION_CODE, "-1");

                    if (Integer.parseInt(updateInfo.getVersionCode()) > versionCode) {
                        if (!updateInfo.isForceUpdate() && !options.shouldForceUpdate() && skip_version_code.equalsIgnoreCase(updateInfo.getVersionCode())) {
                            ((AbstractUpdateListener) listener).onShowNoUpdateUI();
                        } else {
                            if (options.shouldAutoUpdate() || updateInfo.isAutoUpdate()) {
                                informUpdate(updateInfo);
                            } else {
                                ((AbstractUpdateListener) listener).onShowUpdateUI(updateInfo);
                            }
                        }
                    } else {
                        ((AbstractUpdateListener) listener).onShowNoUpdateUI();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.e("can not get the package info", e);
                }
            }
        }
    }

    /**
     * user click to confirm the update
     */
    private void informUpdate(final UpdateInfo info) {
        if (info == null) {
            return;
        }

        DownloadManager manager = new DownloadManager(context);

        downloadTask = new DownloadTask(context);
        downloadTask.setUrl(info.getApkUrl());

        manager.start(downloadTask, new DownloadListener<Integer, DownloadTask>() {
            @Override
            public void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                handler.obtainMessage(
                        MSG_SHOW_UPDATE_PROGRESS_UI, values[0], -1,info).sendToTarget();
            }

            @Override
            public void onSuccess(DownloadTask downloadTask) {
                super.onSuccess(downloadTask);
                if (downloadTask != null && !TextUtils.isEmpty(downloadTask.getPath())) {
                    install(context, downloadTask.getPath());

                    caculateNextTime(null);
                }
            }
        });
    }

    /**
     * caculate the next time to update
     */
    private void caculateNextTime(UpdateInfo info) {
        long now = System.currentTimeMillis();

        String PREFS_NAME = context.getResources().getString(R.string.preference_name);
        String PREFS_KEY_NEXT_CHECK_UPDATE_TIME = context.getResources().getString(R.string.preference_key_next_check_update_time);
        String PREFS_KEY_SKIP_CHECK_UPDATE_VERSION_CODE = context.getResources().getString(R.string.preference_key_skip_check_update_version_code);
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        long period = 0;
        UpdatePeriod updatePeriod = options.getUpdatePeriod();
        if (updatePeriod != null) {
            period = updatePeriod.getPeriodMillis();
        }

        long next = now + period;
        editor.putLong(PREFS_KEY_NEXT_CHECK_UPDATE_TIME, next);
        if (info != null) {
            editor.putString(PREFS_KEY_SKIP_CHECK_UPDATE_VERSION_CODE, info.getVersionCode());
        }
        editor.commit();
    }

    /**
     * user click to cancel the update
     */
    private void informCancel(final UpdateInfo info) {
    }

    /**
     * user click to skip the update
     */
    private void informSkip(final UpdateInfo info) {
        if (info == null) {
            return;
        }

        if (!options.shouldForceUpdate() && !info.isForceUpdate()) {
            caculateNextTime(info);
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
                        listener.onShowUpdateProgressUI((UpdateInfo) msg.obj,downloadTask, msg.arg1);
                    }
                    break;
                case MSG_INFORM_UPDATE:
                    informUpdate((UpdateInfo) msg.obj);
                    break;
                case MSG_INFORM_CANCEL:
                    informCancel((UpdateInfo) msg.obj);
                    break;
                case MSG_INFORM_SKIP:
                    informSkip((UpdateInfo) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
