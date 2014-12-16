package com.example.zhoubo.cstwifi.UI;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhoubo.cstwifi.Log.DebugLog;
import com.example.zhoubo.cstwifi.Net.LoginEngine;
import com.example.zhoubo.cstwifi.R;
import com.example.zhoubo.cstwifi.Setting.UserSetting;
import com.example.zhoubo.cstwifi.Wifi.WifiAdmin;

public class LoginFragment extends Fragment {


    private AutoCompleteTextView mUid;
    private TextView mPassword;
    private Button mLoginBtn;

    private static LoginFragment instance = null;

    public static LoginFragment instance() {
        if (instance == null) {
            instance = new LoginFragment();
        }
        return instance;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.add_user_fragment, container, false);


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLoginBtn = (Button) this.getActivity().findViewById(R.id.log_in_button_login_fragment);
        mUid = (AutoCompleteTextView) this.getActivity().findViewById(R.id.uid_login_fragment);
        mPassword = (TextView) this.getActivity().findViewById(R.id.password_login_fragment);

        UserSetting setting = UserSetting.instance(this.getActivity().getApplicationContext());

        if (!setting.getUid().equals("")) {
            mUid.setText(setting.getUid());
            mPassword.setText(setting.getPass());

        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserSetting setting = UserSetting.instance(LoginFragment.this.getActivity().getApplicationContext());

                setting.setUid(mUid.getText().toString());
                setting.setPass(mPassword.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();

                        LoginEngine loginEngine = LoginEngine.instance(LoginFragment.this.getActivity().getApplicationContext());
                        UserSetting setting = UserSetting.instance(LoginFragment.this.getActivity().getApplicationContext());



                        if (!loginEngine.NeedLogin()) {
                            setting.setIsLogin(true);

                            Toast.makeText(LoginFragment.this.getActivity().getApplicationContext(), "你已经登陆，无需重复登陆", Toast.LENGTH_LONG).show();
                        } else if (loginEngine.Login()) {
                            DebugLog.d("登陆成功");
                            setting.setIsLogin(true);

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager
                                    .beginTransaction();

                            fragmentTransaction.replace(R.id.container, ConnectionInfoFragment.instance());
                            fragmentTransaction.commit();
                            Toast.makeText(LoginFragment.this.getActivity().getApplicationContext(), "登陆成功", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(LoginFragment.this.getActivity().getApplicationContext(), "登陆出现问题", Toast.LENGTH_LONG).show();


                        }
                        Looper.loop();
                    }
                }).start();


            }
        });
    }
}
