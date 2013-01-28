android-autoupdate
===========

android lib - autoupdate  

1、upload the new apk to the webserver,get the apk url,as follows.  
http://helloworld-snowdream.herokuapp.com/temp.apk   

2、edit the update.xml   
<?xml version="1.0" encoding="utf-8"?>  

<versionInfo>  
	<appName>temp</appName>  
	<appDescription>a wonderful app</appDescription>  
	<packageName>com.snowdream.updatenow</packageName>  
	<versionCode>2</versionCode>  
	<versionName>2.0</versionName>  
	<forceUpdate>false</forceUpdate>  
	<apkUrl>http://helloworld-snowdream.herokuapp.com/temp.apk</apkUrl>  
	<updateTips>update</updateTips>  
</versionInfo>  

3、upload the update.xml to the webserver,get the file url,as follows.  
http://helloworld-snowdream.herokuapp.com/update.xml  


4、get the lib in you project. write this into the pom file.   
<dependency>  
  <groupId>com.github.snowdream.android</groupId>  
  <artifactId>autoupdate</artifactId>  
  <version>0.0.3</version>  
  <type>apklib</type>  
</dependency>  

5、where you want to check update,write as follows.  
UpdateAPI update = new UpdateAPI(this);  
update.setmUpdateUrl("http://helloworld-snowdream.herokuapp.com/update.xml");  
update.check();  
