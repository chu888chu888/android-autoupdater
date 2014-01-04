package com.github.snowdream.android.app;

import android.text.TextUtils;

import com.github.snowdream.android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by snowdream on 1/3/14.
 */
public class UpdateXmlParser extends AbstractParser {
    /**
     * Parse the UpdateInfo form the string
     *
     * @param content
     * @return UpdateInfo
     * @throws com.github.snowdream.android.app.UpdateException
     */
    @Override
    public UpdateInfo parse(String content) throws UpdateException {
        UpdateInfo info = null;

        if (TextUtils.isEmpty(content)) {
            throw new UpdateException(UpdateException.PARSE_ERROR);
        }

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(content));

            info = parseUpdateInfo(xpp);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e("XmlPullParserException", e);
            throw new UpdateException(UpdateException.PARSE_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e);
            throw new UpdateException(UpdateException.PARSE_ERROR);
        }

        return info;
    }

    /**
     * Parse UpdateInfo
     *
     * @param xpp
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private UpdateInfo parseUpdateInfo(XmlPullParser xpp) throws XmlPullParserException, IOException {
        UpdateInfo info = null;
        String currentTag = null;

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    currentTag = xpp.getName();
                    if (currentTag.equals(TAG_UPDATE_INFO)) {
                        info = new UpdateInfo();
                    } else if (currentTag.equals(TAG_APP_NAME)) {
                        if (info != null) {
                            info.setAppName(xpp.nextText());
                        }
                    } else if (currentTag.equals(TAG_APP_DESCRIPTION)) {
                        if (info != null) {
                            info.setAppDescription(xpp.nextText());
                        }
                    } else if (currentTag.equals(TAG_PACKAGE_NAME)) {
                        if (info != null) {
                            info.setPackageName(xpp.nextText());
                        }
                    } else if (currentTag.equals(TAG_VERSION_CODE)) {
                        if (info != null) {
                            info.setVersionCode(xpp.nextText());
                        }
                    } else if (currentTag.equals(TAG_VERSION_NAME)) {
                        if (info != null) {
                            info.setVersionName(xpp.nextText());
                        }
                    } else if (currentTag.equals(TAG_FORCE_UPDATE)) {
                        if (info != null) {
                            info.setForceUpdate(xpp.nextText() == "true" ? true : false);
                        }
                    } else if (currentTag.equals(TAG_AUTO_UPDATE)) {
                        if (info != null) {
                            info.setAutoUpdate(xpp.nextText() == "true" ? true : false);
                        }
                    } else if (currentTag.equals(TAG_APK_URL)) {
                        if (info != null) {
                            info.setApkUrl(xpp.nextText());
                        }
                    } else if (currentTag.equals(TAG_UPDATE_TIPS)) {
                        Map<String, String> map = parseUpdateTips(xpp);
                        if (info != null) {
                            info.setUpdateTips(map);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                case XmlPullParser.TEXT:
                    break;
                default:
                    break;
            }
            eventType = xpp.next();
        }

        return info;
    }

    /**
     * Parse UpdateTips
     *
     * @param xpp
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Map<String, String> parseUpdateTips(XmlPullParser xpp) throws XmlPullParserException, IOException {
        Map<String, String> updateTips = new HashMap<String, String>();
        String currentTag = null;
        String currentValue = null;

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    currentTag = xpp.getName();
                    if (currentTag.equals(TAG_UPDATE_TIPS)) {
                        updateTips = new HashMap<String, String>();
                    } else {
                        currentValue = xpp.nextText();
                        updateTips.put(currentTag, currentValue);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                case XmlPullParser.TEXT:
                    break;
                default:
                    break;
            }
            eventType = xpp.next();
        }
        return updateTips;
    }
}