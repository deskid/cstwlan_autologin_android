package com.example.zhoubo.cstwifi.UI;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhoubo.cstwifi.Log.DebugLog;
import com.example.zhoubo.cstwifi.Net.LoginEngine;
import com.example.zhoubo.cstwifi.R;
import com.example.zhoubo.cstwifi.Setting.UserSetting;

/**
 * Created by zhoubo on 14/12/9.
 */
public class ConnectionInfoFragment extends Fragment {

    private TextView mIp;
    private TextView mDownloadDataAmount;
    private TextView mUploadDataAmount;
    private TextView mOnLineTime;

    private Button refreshBtn;
    private Button logoutBtn;
    private Button forceLogoutBtn;

    private static ConnectionInfoFragment instance = null;

    public static ConnectionInfoFragment instance() {

        if (instance == null) {
            instance = new ConnectionInfoFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.logout_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIp = (TextView) this.getActivity().findViewById(R.id.user_info_ip_logout_fragment);
        mDownloadDataAmount = (TextView) this.getActivity().findViewById(R.id.user_info_download_data_logout_fragment);
        mUploadDataAmount = (TextView) this.getActivity().findViewById(R.id.user_info_upload_data_logout_fragment);
        mOnLineTime = (TextView) this.getActivity().findViewById(R.id.user_info_time_logout_fragment);

        refreshBtn = (Button) this.getActivity().findViewById(R.id.refresh_button_logout_fragment);
        logoutBtn = (Button) this.getActivity().findViewById(R.id.log_out_button_logout_fragment);
        forceLogoutBtn = (Button)this.getActivity().findViewById(R.id.log_out_button_force_logout_fragment);


        UserSetting setting = UserSetting.instance(ConnectionInfoFragment.this.getActivity().getApplicationContext());
        mIp.setText(setting.getIp());
        mDownloadDataAmount.setText(setting.getDownloadData());
        mUploadDataAmount.setText(setting.getUploadData());
        mOnLineTime.setText(setting.getOnLineTime());


        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSetting setting = UserSetting.instance(ConnectionInfoFragment.this.getActivity().getApplicationContext());
                if (!setting.isLogin())
                {
                    Toast.makeText(ConnectionInfoFragment.this.getActivity().getApplicationContext(), "请先登陆", Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager
                            .beginTransaction();

                    fragmentTransaction.replace(R.id.container, LoginFragment.instance());
                    fragmentTransaction.commit();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        LoginEngine loginEngine = LoginEngine.instance(ConnectionInfoFragment.this.getActivity().getApplicationContext());

                        if (loginEngine.UserInfo()) {
                            DebugLog.d("刷新成功");

                        } else {
                            Toast.makeText(ConnectionInfoFragment.this.getActivity().getApplicationContext(), "刷新出现问题", Toast.LENGTH_LONG).show();
                        }

                        Looper.loop();
                    }
                }).start();



                mIp.setText(setting.getIp());
                mDownloadDataAmount.setText(setting.getDownloadData());
                mUploadDataAmount.setText(setting.getUploadData());
                mOnLineTime.setText(setting.getOnLineTime());

            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        LoginEngine loginEngine = LoginEngine.instance(ConnectionInfoFragment.this.getActivity().getApplicationContext());
                        UserSetting setting = UserSetting.instance(ConnectionInfoFragment.this.getActivity().getApplicationContext());
                        if (loginEngine.Logout()) {
                            DebugLog.d("注销成功");
                            Toast.makeText(ConnectionInfoFragment.this.getActivity().getApplicationContext(), "注销成功", Toast.LENGTH_LONG).show();
                            setting.setIsLogin(false);

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager
                                    .beginTransaction();

                            fragmentTransaction.replace(R.id.container, LoginFragment.instance());
                            fragmentTransaction.commit();

                        } else {
                            Toast.makeText(ConnectionInfoFragment.this.getActivity().getApplicationContext(), "注销出现问题", Toast.LENGTH_LONG).show();
                        }

                        Looper.loop();
                    }
                }).start();


            }
        });


        forceLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();

                        LoginEngine loginEngine = LoginEngine.instance(ConnectionInfoFragment.this.getActivity().getApplicationContext());
                        UserSetting setting = UserSetting.instance(ConnectionInfoFragment.this.getActivity().getApplicationContext());
                        if (loginEngine.ForceLogout()) {
                            DebugLog.d("强制注销成功");
                            Toast.makeText(ConnectionInfoFragment.this.getActivity().getApplicationContext(), "强制注销成功", Toast.LENGTH_LONG).show();
                            setting.setIsLogin(false);

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager
                                    .beginTransaction();

                            fragmentTransaction.replace(R.id.container, LoginFragment.instance());
                            fragmentTransaction.commit();

                        } else {
                            Toast.makeText(ConnectionInfoFragment.this.getActivity().getApplicationContext(), "强制注销成功", Toast.LENGTH_LONG).show();
                        }

                        Looper.loop();
                    }
                }).start();


            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

}

