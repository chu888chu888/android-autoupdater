package com.github.snowdream.android.app;

import com.github.snowdream.android.util.concurrent.TaskListener;

/**
 * Created by snowdream on 12/30/13.
 */
public class UpdateListener<Integer, UpdateInfo> extends TaskListener<Integer, UpdateInfo> {

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
    public final void informUpdate() {
    }

    /**
     * user click to cancel the update
     */
    public final void informCancel() {
    }

    /**
     * user click to skip the update
     */
    public final void informSkip() {
    }

//
//    private static class UIHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
//
//            if (result == null || result.mTask == null || result.mTask.isCancelled()) {
//                Log.i("The asyncTask is not valid or cancelled!");
//                return;
//            }
//
//            switch (msg.what) {
//                case MESSAGE_POST_ERROR:
//                    ((AsycDownloadTask) result.mTask).OnError(result.mDownloadTask, result.mData);
//                    break;
//                case MESSAGE_POST_ADD:
//                    ((AsycDownloadTask) result.mTask).OnAdd(result.mDownloadTask);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
}
