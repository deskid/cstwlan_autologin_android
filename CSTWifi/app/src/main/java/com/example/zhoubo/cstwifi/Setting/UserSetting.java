package com.example.zhoubo.cstwifi.Setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.zhoubo.cstwifi.Log.DebugLog;

/**
 * Created by zhoubo on 14/12/9.
 */
public class UserSetting {


    private static final String SP_NAME = "USER_SETTING";
    private static final String UID = "uid";
    private static final String PASS = "pass";
    private static final String IS_FIRST_TIME_LOGIN = "is_first_time_login";
    private static final String LOGIN_STATE = "login_state";

    private static UserSetting instance;

    private static String uid;
    private static String password;
    private static String uToken;
    private static boolean isFirstTimeLogin;
    private static boolean isLogin;


    private static String ip;
    private static String onLineTime;
    private static String downloadData;
    private static String uploadData;

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        UserSetting.ip = ip;
    }

    public static String getOnLineTime() {
        return onLineTime;
    }

    public static void setOnLineTime(String onLineTime) {
        UserSetting.onLineTime = onLineTime;
    }

    public static String getDownloadData() {
        return downloadData;
    }

    public static void setDownloadData(String downloadData) {
        UserSetting.downloadData = downloadData;
    }

    public static String getUploadData() {
        return uploadData;
    }

    public static void setUploadData(String uploadData) {
        UserSetting.uploadData = uploadData;
    }

    private static Context context;

    private static SharedPreferences sp;
    private static SharedPreferences.Editor spEditor;

    private UserSetting(Context context) {

        this.context = context;
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        spEditor = sp.edit();


        // 默认打开 第一次登陆
        isFirstTimeLogin = sp.getBoolean(IS_FIRST_TIME_LOGIN,true);

        //默认处于未登陆状态

        isLogin = sp.getBoolean(LOGIN_STATE,false);

        uid = sp.getString(UID, "");
        password = sp.getString(PASS, "");

    }

    public static synchronized UserSetting instance(Context context) {
        if (instance == null) {
            instance = new UserSetting(context.getApplicationContext());
            DebugLog.d("init Instance of Setting");
        }

        return instance;
    }

    public static boolean isLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        UserSetting.isLogin = isLogin;
    }

    public String getUserToken() {
        return uToken;
    }

    public void setUerToken(String _uToken) {
        uToken = _uToken;
    }

    public Context getContext() {
        return context;
    }


    public static boolean isFirstTimeLogin() {
        return isFirstTimeLogin;
    }

    public static void setFirstTimeLogin(boolean isFirstTimeLogin) {
        UserSetting.isFirstTimeLogin = isFirstTimeLogin;
    }

    public void updateAccount(String uid, String password) {
        this.uid = uid;
        this.password = password;

        setUid(uid);
        setPass(password);
    }

    public void setUid(String uid) {
        DebugLog.d("last login uid is set as " + uid);
        writeValueForKey(UID, uid);
    }

    public String getUid() {
        return uid;
    }


    public void setPass(String pass) {
        DebugLog.d("last login pass is set as " + pass);
        writeValueForKey(PASS, pass);
    }

    public String getPass() {
        return password;
    }


    public void cleanAllSettings() {

        spEditor.clear();
        spEditor.commit();
    }

    private void writeValueForKey(String key, Object value) {

        DebugLog.d("write to sp: " + key + " : " + value);


        if (value instanceof Boolean) {
            spEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            spEditor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            spEditor.putInt(key, (Integer) value);
        }
        spEditor.commit();
    }
}
