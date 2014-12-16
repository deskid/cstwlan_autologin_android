package com.example.zhoubo.cstwifi.UI;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhoubo.cstwifi.Log.DebugLog;
import com.example.zhoubo.cstwifi.Net.LoginEngine;
import com.example.zhoubo.cstwifi.R;
import com.example.zhoubo.cstwifi.Setting.UserSetting;
import com.example.zhoubo.cstwifi.Wifi.WifiAdmin;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Fragment mCurrentFragment;

    private UserSetting userSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        userSetting = UserSetting.instance(this);

        WifiAdmin wifi = new WifiAdmin(userSetting.getContext());

        if (!wifi.isWifiOpen()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("CST_WLAN不可用");
            builder.setMessage("网络不可用，如果继续，请先打开WIFI连接到CST_WLAN");
            builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = null;
                    /**
                     * 判断手机系统的版本！如果API大于10 就是3.0+
                     * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                     */
                    if (android.os.Build.VERSION.SDK_INT > 10) {
                        intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                    } else {
                        intent = new Intent();
                        ComponentName component = new ComponentName(
                                "com.android.settings",
                                "com.android.settings.WirelessSettings");
                        intent.setComponent(component);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create();
            builder.show();

        }
        else
        {
            if (!wifi.connectWifi("CST_WLAN")) {
                Toast.makeText(this, "CST_WLAN 链接失败", Toast.LENGTH_LONG).show();
            }
        }


        if (!userSetting.isLogin()) {
            switchContent(LoginFragment.instance());
        } else {
            switchContent(ConnectionInfoFragment.instance());
        }


    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch (position) {
            case 0:// 登陆界面
                DebugLog.d("登陆界面");
                switchContent(LoginFragment.instance());
                break;

            case 1:// 连接信息
                DebugLog.d("连接信息");
                switchContent(ConnectionInfoFragment.instance());
                break;
            case 2:// 账号管理
                DebugLog.d("账号管理");
                switchContent(SettingFragment.instance());
        }
    }

    private void switchContent(Fragment fragment) {
        mCurrentFragment = fragment;

        getFragmentManager().beginTransaction()
                .replace(R.id.container, mCurrentFragment).commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        //actionBar.setDisplayShowHomeEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO add pairent activity for action
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            return true;
        }
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
