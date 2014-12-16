package com.example.zhoubo.cstwifi.Net;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;

import com.example.zhoubo.cstwifi.Log.DebugLog;
import com.example.zhoubo.cstwifi.Setting.UserSetting;
import com.example.zhoubo.cstwifi.Wifi.WifiAdmin;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhoubo on 14/12/9.
 */
public class LoginEngine {

    private static final String TESTURL = "http://www.baidu.com";
    private static final String FINGERPRINT = "百度一下，你就知道";

    private static final String SERVERURL = "http://192.0.0.6/";
    //cookie name
    private static final String COOKIENAME = "srun_login";
    //登陆 POST
    private static final String LoginActionUrl = "http://192.0.0.6/cgi-bin/do_login";
    //登陆 GET
    private static final String LoginDoneActionUrl = "http://192.0.0.6/login.html";
    //用户信息
    private static final String UserInfoActionUrl = "http://192.0.0.6/user_info.php?uid=";
    //注销 POST uid
    private static final String LogoutActionUrl = "http://192.0.0.6/cgi-bin/do_logout";
    //强制注销 POST
    private static final String ForceLogoutActionUrl = "http://192.0.0.6/cgi-bin/force_logout";


    //private static String uid;
    //private static String password;
    //private static String uToken;
    private static UserSetting setting;
    private static Context context;

    private static LoginEngine instance = null;

    private LoginEngine(Context context) {

        this.setting = UserSetting.instance(context);
        this.context = context;
        //uid = setting.getUid();
        //password = setting.getPass();
        //uToken = setting.getUserToken();

    }

    public static synchronized LoginEngine instance(Context context) {
        if (instance == null) {
            DebugLog.d("init instance loginEngine");
            instance = new LoginEngine(context);
        }
        return instance;
    }

    public boolean NeedLogin() {

        try {
            String getStr = TESTURL;
            HttpGet get = new HttpGet(getStr);
            HttpResponse response = new DefaultHttpClient().execute(get);

            if (response == null) {
                return true;
            }

            int rc = response.getStatusLine().getStatusCode();
            if (rc == 200) {
                String result = EntityUtils.toString(response.getEntity(), "GB2312");
                return !result.contains(FINGERPRINT);
            } else {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;


    }

    public boolean Login() {
        String result = "";
        String time = System.currentTimeMillis() / 1000 / 60 + "";
        DebugLog.d("first time is = " + time);
        WifiAdmin wifi = new WifiAdmin(setting.getContext());

        boolean hasError = false;

        try {
            for (int i = 0; i < 2; i++) {

                HttpPost post = new HttpPost(LoginActionUrl);

                post.addHeader("Content-Type", "application/x-www-form-urlencoded");
                post.addHeader("User-Agent", "my session");

                String str = "username=" + setting.getUid() + "&password=";
                DebugLog.d("str time is = " + time);
                str += URLEncoder.encode(encrypt(setting.getPass(), time), "UTF-8");
                str += "&drop=0&type=2&n=16&mac=";
                DebugLog.d("wifi mac address = " + wifi.getMacAddress());
                str += URLEncoder.encode(encrypt(wifi.getMacAddress(), time), "UTF-8");
                DebugLog.d("finally login post string = " + str);

                post.setEntity(new StringEntity(str));
                HttpResponse response = new DefaultHttpClient().execute(post);

                int rc = response.getStatusLine().getStatusCode();
                if (rc == 200) {
                    result = EntityUtils.toString(response.getEntity());
                    String[] res = result.split("@");
                    DebugLog.d("login response = " + result);
                    if (res.length > 1) {
                        //get the token
                        DebugLog.d("get the token = " + res[1]);
                        int nt = Integer.parseInt(res[1]);
                        time = nt / 60 + "";
                        DebugLog.d("second time is = " + time);

                    } else {
                        //something wrong?
                        DebugLog.d("break");
                        break;
                    }
                } else {
                    result = rc + "";
                    DebugLog.d("second?" + result);
                }
            }


            if (result.split(",").length > 1) {
                //get the utoken
                setting.setUerToken(result.split(",")[0]);
                DebugLog.d("get uToken,and login success " + result.split(",")[0]);
                hasError = true;
            } else {
                DebugLog.d("登陆失败" + result.split("@")[0]);
                hasError = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }//end try


        return hasError;

    }


    public boolean Logout() {
        try {
            HttpPost post = new HttpPost(LogoutActionUrl);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addHeader("User-Agent", "my session");
            String postStr = "uid=" + setting.getUserToken();

            post.setEntity(new StringEntity(postStr));

            HttpResponse response = new DefaultHttpClient().execute(post);
            int rc = response.getStatusLine().getStatusCode();
            if (rc == 200) {
                DebugLog.d("logout success : " + EntityUtils.toString(response.getEntity()));

                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean ForceLogout() {
        try {
            HttpPost post = new HttpPost(ForceLogoutActionUrl);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addHeader("User-Agent", "my session");
            String postStr = "username=" + setting.getUid() + "&password="+setting.getPass()+"&drop=1&type=1&n=1";

            post.setEntity(new StringEntity(postStr));

            HttpResponse response = new DefaultHttpClient().execute(post);
            int rc = response.getStatusLine().getStatusCode();
            if (rc == 200) {
                DebugLog.d("force logout success : " + EntityUtils.toString(response.getEntity()));
                return EntityUtils.toString(response.getEntity()).contains("logout_ok");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean UserInfo() {
        try {


            DebugLog.d("user token"+setting.getUserToken());
            String getStr = UserInfoActionUrl + setting.getUserToken();
            HttpGet get = new HttpGet(getStr);
            HttpResponse response = new DefaultHttpClient().execute(get);
            int rc = response.getStatusLine().getStatusCode();
            if (rc == 200) {

                String result = EntityUtils.toString(response.getEntity(), "GB2312");

                DebugLog.d(result);

                setting.setIp(getIp(result));
                setting.setOnLineTime(getOnLineTime(result));

                setting.setDownloadData(getDownloadData(result));
                setting.setUploadData(getUploadData(result));

                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }




    private String getIp(String source) {
        String regex = "<p>IP地址</p></td>[\\s\\S]*?<td><p>(.*?)</p></td>";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            DebugLog.d(matcher.group(1).trim());
            return matcher.group(1).trim();
        }
        return "";
    }

    private String getDownloadData(String source) {
        String regex = "<p>计费入流量</p></td>[\\s\\S]*?<td><p>(.*?)</p></td>";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            DebugLog.d(matcher.group(1).trim());
            return matcher.group(1).trim();
        }
        return "";
    }

    private String getUploadData(String source) {
        String regex = "<p>计费出流量</p></td>[\\s\\S]*?<td><p>(.*?)</p></td>";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            DebugLog.d(matcher.group(1).trim());
            return matcher.group(1).trim();
        }
        return "";
    }

    private String getOnLineTime(String source) {
        String regex = "<p>在线时长</p></td>[\\s\\S]*?<td><p>(.*?)</p></td>";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            DebugLog.d(matcher.group(1).trim());
            return matcher.group(1).trim();
        }
        return "";
    }

    private String PostStringBuilder(String time) {

        this.setting = UserSetting.instance(context);

        String str = "username=" + setting.getUid() + "&password=";
        WifiAdmin wifi = new WifiAdmin(setting.getContext());

        try {
            str += URLEncoder.encode(encrypt(setting.getPass(), time), "UTF-8");

            str += "&drop=0&type=2&n=16&mac=";

            DebugLog.d("wifi mac address = " + wifi.getMacAddress());
            str += URLEncoder.encode(encrypt(wifi.getMacAddress(), time), "UTF-8");

            DebugLog.d("finally login post string = " + str);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


    private String encrypt(String str, String key) {

        String res = "";
        for (int i = 0; i < str.length(); i++) {
            int ki = (int) (key.charAt(key.length() - i % key.length() - 1));
            int pi = (int) (str.charAt(i));
            ki = ki ^ pi;
            res += buildkey(ki, i % 2);
        }
        return res;
    }

    private String buildkey(int num, int reverse) {
        String ret = "";
        int _low = num & 0x0f;

        int _high = num >> 4;
        _high = _high & 0x0f;

        if (reverse == 0) {
            char temp1 = (char) (_low + 0x36);
            char temp2 = (char) (_high + 0x63);

            ret = temp1 + "" + temp2;
        } else {
            char temp1 = (char) (_high + 0x63);
            char temp2 = (char) (_low + 0x36);

            ret = temp1 + "" + temp2;
        }
        return ret;
    }
}
