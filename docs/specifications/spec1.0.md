#Android Autoupdate Specification V1.0

##INTRODUCTION
It specify how the client check the update info from the server.  
When the client send a request to the server,the server answer a response with the current update info.Then the client will compare the update info with the local info,and decide whether the client should update.  
It support two formats: *xml* and *json*. which format will be used,is decided by the [Content-Type](http://www.w3.org/Protocols/rfc1341/4_Content-Type.html).

##DOWNLOAD
[Android Autoupdate Specification V1.0](.)

##SYNTAX
The server can answer a response as follows:
###xml
```xml
 <?xml version=”1.0” encoding=”UTF-8”?>
<updateInfo>
    <appName>android-explorer</appName>
    <appDescription>A Free File Manager for Android</appDescription>
    <packageName>com.github.snowdream.android.apps.explorer</packageName>
    <versionCode>2</versionCode>  
    <versionName>2.0</versionName>  
    <forceUpdate>false</forceUpdate>  
    <apkUrl>http://helloworld-snowdream.herokuapp.com/explorer-2.0.apk</apkUrl>
    <updateTips>
        <default>update tips</default>
        <en>update tips</en>
        <zh>升级提示</zh>
        <zh_CN>升级提示</zh_CN>
        <zh_TW>升级提示</zh_TW>
        <zh_HK>升级提示</zh_HK>
        ...
    </updateTips>
</updateInfo>  
```

###json
```json
{
    "appName": "android-explorer",
    "appDescription": "A Free File Manager for Android",
    "packageName": "com.github.snowdream.android.apps.explorer",
    "versionCode": "2",
    "versionName": "2.0",
    "forceUpdate": false,
    "apkUrl": "http://helloworld-snowdream.herokuapp.com/explorer-2.0.apk",
   " updateTips": {
            "default": "update tips",
            "en":  "update tips",
            "zh":  "升级提示",
            "zh_CN": "升级提示",
            "zh_TW": "升級提示",
           " zh_HK": "升级提示",
                 ...
    }
}
```
* *updateInfo*: the entry of update info
* *appName*: the name of the app
* *appDescription*: the description of the app
* *versionCode*: the versionCode for the new version of app
* *versionName*: the versionName for the new version of app
* *forceUpdate*: whether the client should update.if true,then when the user cancel the update,exit the application. 
* *apkUrl*: the url for the new version of app
* *updateTips*: the update tips for the new version of app







