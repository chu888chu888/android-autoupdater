package com.github.snowdream.android.api.autoupdate;

import com.github.snowdream.android.http.AsyncHttpClient;
import com.github.snowdream.android.http.AsyncHttpResponseHandler;
import com.github.snowdream.android.http.RequestParams;
import com.github.snowdream.android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class UpdateAPI implements IUpdateCheckListener{
    private static final int MSG_ONSTART = 0;
    private static final int MSG_ONFINISH = 1;
    private static final int MSG_ONSUCCESS = 2;
    private static final int MSG_ONFAILURE = 3;

    /**
     * The update file url,eg.a xml file in the web server
     */
    private String mUpdateUrl = "";

    /**
     * context
     */
    private Context mContext = null;

    /**
     * versioninfo
     */
    private VersionInfo oldInfo = null;
    
    private DownloadCompleteReceiver receiver = null;  

    private DownloadManager manager = null;
    
    
    /**
     * UIHandler
     */
    private UIHandler handler = null;

    static class UIHandler extends Handler {
        private WeakReference<IUpdateCheckListener> wr = null;

        public UIHandler(IUpdateCheckListener update) {
            this.wr = new WeakReference<IUpdateCheckListener>(update);
        }


        public void handleMessage(android.os.Message msg){
            IUpdateCheckListener listener = wr.get();
            if (listener == null) {
                return;
            }

            switch (msg.what) {
                case MSG_ONSTART:
                    listener.OnStart();
                    break;
                case MSG_ONFINISH:
                    listener.OnFinish();
                    break;
                case MSG_ONSUCCESS:
                    Object object = msg.obj;
                    if (object == null) {
                        return;
                    }

                    if (object instanceof Boolean) {
                        Boolean isUpdate = (Boolean)object;
                        listener.OnSuccess(isUpdate);
                    }else if (object instanceof VersionInfo){
                        VersionInfo versionInfo = (VersionInfo)object;
                        listener.OnSuccess(versionInfo);
                    }
                    break;
                case MSG_ONFAILURE:
                    Object obj = msg.obj;
                    if (obj == null) {
                        return;
                    }

                    if (obj instanceof UpdateException) {
                        UpdateException e = (UpdateException)obj;
                        listener.OnFailure(e);
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @SuppressWarnings("unused")
    private UpdateAPI() {
    }

    public UpdateAPI(Context context){
        mContext = context;
        
        oldInfo = new VersionInfo();
        checkCurrentVersionInfo();
    }
    
    
    /**
     * check whether to update
     */
    public void check(){
        check(this);
    }

    /**
     * check whether to update
     */
    public void check(IUpdateCheckListener listener) {
        handler  = new UIHandler(listener);
        if (oldInfo == null || mUpdateUrl == null) {
            return;
        }

        RequestParams requestParams = new RequestParams();
        requestParams.put("packageName", oldInfo.getPackageName());
        requestParams.put("versionCode", oldInfo.getVersionCode());
        requestParams.put("versionName", oldInfo.getVersionName());

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(mUpdateUrl, new AsyncHttpResponseHandler(){

            @Override
            public void onFailure(Throwable arg0, String arg1) {
                super.onFailure(arg0, arg1);
                handler.sendMessage(Message.obtain(handler,MSG_ONFAILURE,new UpdateException()));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                handler.sendMessage(Message.obtain(handler,MSG_ONFINISH,null));
            }

            @Override
            public void onStart() {
                super.onStart();
                handler.sendMessage(Message.obtain(handler,MSG_ONSTART,null));
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                System.out.println(response);
                if (TextUtils.isEmpty(response)) {
                    handler.sendMessage(Message.obtain(handler,MSG_ONFAILURE,new UpdateException()));
                    return;
                }

                VersionInfo info = null;


                //得到 DocumentBuilderFactory 对象, 由该对象可以得到 DocumentBuilder 对象
                DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
                try {
                    ByteArrayInputStream stream = new ByteArrayInputStream(response.getBytes("utf-8"));

                    //得到DocumentBuilder对象
                    DocumentBuilder builder=factory.newDocumentBuilder();
                    //得到代表整个xml的Document对象
                    Document document=builder.parse(stream);
                    //得到 "根节点" 
                    Element root=document.getDocumentElement();
                    //获取根节点的所有items的节点
                    NodeList items= root.getChildNodes();

                    //遍历所有节点
                    int size = items.getLength();

                    if (size < 0) {
                        Log.e("Reponse Error!");
                    }

                    info = new VersionInfo();

                    for(int i=0;i<size;i++)
                    {
                        Node item=(Node)items.item(i);
                        if (item.getNodeName().equalsIgnoreCase("appName")) {
                            info.setAppName(item.getFirstChild().getNodeValue());
                        }else if (item.getNodeName().equalsIgnoreCase("appDescription")) {
                            info.setAppDescription(item.getFirstChild().getNodeValue());
                        }else if (item.getNodeName().equalsIgnoreCase("packageName")) {
                            info.setPackageName(item.getFirstChild().getNodeValue());
                        }else if (item.getNodeName().equalsIgnoreCase("versionCode")) {
                            info.setVersionCode(item.getFirstChild().getNodeValue());
                        }else if (item.getNodeName().equalsIgnoreCase("versionName")) {
                            info.setVersionName(item.getFirstChild().getNodeValue());
                        }else if (item.getNodeName().equalsIgnoreCase("forceUpdate")) {
                            info.setForceUpdate(item.getFirstChild().getNodeValue().equalsIgnoreCase("true")? true:false);
                        }else if (item.getNodeName().equalsIgnoreCase("apkUrl")) {
                            info.setApkUrl(item.getFirstChild().getNodeValue());
                        }else if (item.getNodeName().equalsIgnoreCase("updateTips")) {
                            info.setUpdateTips(item.getFirstChild().getNodeValue());
                        }
                    } 

                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                handler.sendMessage(Message.obtain(handler,MSG_ONSUCCESS,info));

                if (oldInfo != null && info != null) {
                    if (Integer.parseInt(info.getVersionCode()) > Integer.parseInt(oldInfo.getVersionCode())) {
                        handler.sendMessage(Message.obtain(handler,MSG_ONSUCCESS,true));
                    }else {
                        handler.sendMessage(Message.obtain(handler,MSG_ONSUCCESS,false));
                    }
                }else {
                    handler.sendMessage(Message.obtain(handler,MSG_ONSUCCESS,false));
                }
            }
        });
    }

    private void finishCheck(final VersionInfo newInfo){
        if(oldInfo != null && newInfo != null){
            try {
                if (Integer.parseInt(newInfo.getVersionCode()) > Integer.parseInt(oldInfo.getVersionCode())) {
                    doNewVersionUpdate(newInfo);
                } else {
                    notNewVersionShow(newInfo);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void notNewVersionShow(final VersionInfo newInfo) {
        if (mContext == null ||newInfo == null || oldInfo == null) {
            return;
        }
        
            

        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(oldInfo.getVersionName());
        sb.append(" Code:");
        sb.append(oldInfo.getVersionCode());
        sb.append(",\n已是最新版,无需更新!");
        Dialog dialog = new AlertDialog.Builder(mContext)
        .setTitle("软件更新").setMessage(sb.toString())// 设置内容
        .setPositiveButton("确定",// 设置确定按钮
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                    int which) {
            }

        }).create();// 创建
        // 显示对话框
        if (!((Activity)mContext).isFinishing()) {
        dialog.show();
        }
    }

    private void doNewVersionUpdate(final VersionInfo newInfo) {
        if (mContext == null ||newInfo == null || oldInfo == null) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(oldInfo.getVersionName());
        sb.append(" Code:");
        sb.append(oldInfo.getVersionCode());
        sb.append(", 发现新版本:");
        sb.append(newInfo.getVersionName());
        sb.append(" Code:");
        sb.append(newInfo.getVersionCode());
        sb.append(", 是否更新?");
        Dialog dialog = new AlertDialog.Builder(mContext)
        .setTitle("软件更新")
        .setMessage(sb.toString())
        // 设置内容
        .setPositiveButton("更新",// 设置确定按钮
                new DialogInterface.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(DialogInterface dialog,
                    int which) {
                if (mContext != null) {
                    manager =(DownloadManager)mContext.getSystemService(Activity.DOWNLOAD_SERVICE);
                    receiver = new DownloadCompleteReceiver();  
                    mContext.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));  
                }
                
               //创建下载请求  
                DownloadManager.Request down=new DownloadManager.Request (Uri.parse(newInfo.getApkUrl()));  
                //设置允许使用的网络类型，这里是移动网络和wifi都可以  
                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);  
                //禁止发出通知，既后台下载  
                down.setShowRunningNotification(true);  
                down.setTitle(newInfo.getAppName());
                down.setDescription(newInfo.getAppDescription());
                //不显示下载界面  
                down.setVisibleInDownloadsUi(true);  
                //设置下载后文件存放的位置  
                down.setDestinationInExternalFilesDir(mContext, null, "temp.apk");  
                //将下载请求放入队列  
                manager.enqueue(down);  
            }

        })
        .setNegativeButton("暂不更新",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                    int whichButton) {
                // 点击"取消"按钮之后退出程序
                if (newInfo.isForceUpdate()) {
                    ((Activity) mContext).finish();
                }
            }
        }).create();
        
        // 显示对话框
        if (!((Activity)mContext).isFinishing()) {
        dialog.show();
        }
    }

    /**
     * check and get the current version info
     */
    private void checkCurrentVersionInfo(){
        if (mContext == null) {
            return;
        }

        String packageName = "";
        String versionCode = "";
        String versionName = "";

        PackageInfo info = null;

        try {
            info = mContext.getPackageManager().getPackageInfo(mContext.getApplicationContext().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        if (info != null) {
            packageName = info.packageName;
            versionCode = info.versionCode+"";
            versionName = info.versionName;

            oldInfo.setPackageName(packageName);
            oldInfo.setVersionName(versionName);
            oldInfo.setVersionCode(versionCode);
        }
    }

    @Override
    public void OnStart() {

    }

    @Override
    public void OnFinish() {

    }

    @Override
    public void OnSuccess(Boolean isUpdate) {

    }

    @Override
    public void OnSuccess(VersionInfo versionInfo) {
        finishCheck(versionInfo);
    }

    @Override
    public void OnFailure(UpdateException e) {
        e.printStackTrace();
    }

    /**
     * @param mUpdateUrl the mUpdateUrl to set
     */
    public void setmUpdateUrl(String mUpdateUrl) {
        this.mUpdateUrl = mUpdateUrl;
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){  
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);  
                Log.v(" download complete! id : "+downId); 
                
                install(downId);
                
                if (mContext != null) {
                    mContext.unregisterReceiver(receiver);  
                }
            }  
        }  
    }  

    void install(long id) {
        String fileName = "";
        
        DownloadManager.Query query = new DownloadManager.Query(); 
        query.setFilterById(id); 
        Cursor c = manager.query(query); 
        if(c.moveToFirst()) { 
            fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        }
        
        if (!TextUtils.isEmpty(fileName)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(fileName),
                    "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        }
    }
}
