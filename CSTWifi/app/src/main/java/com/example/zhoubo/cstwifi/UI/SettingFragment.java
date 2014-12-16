package com.example.zhoubo.cstwifi.UI;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhoubo.cstwifi.Log.DebugLog;
import com.example.zhoubo.cstwifi.Net.LoginEngine;
import com.example.zhoubo.cstwifi.R;
import com.example.zhoubo.cstwifi.Setting.UserSetting;

/**
 * Created by zhoubo on 14/12/9.
 */
public class SettingFragment extends Fragment {

    private Button mUpdateBtn;
    private EditText mUidText;
    private EditText mPassword;



    private static SettingFragment instance = null;

    public static SettingFragment instance() {

        if (instance == null) {
            instance = new SettingFragment();
        }
        return instance;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_setting_fragment, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserSetting setting = UserSetting.instance(this.getActivity().getApplicationContext());

        mUpdateBtn = (Button)this.getActivity().findViewById(R.id.update_button_user_setting_fragment);
        mUidText = (EditText)this.getActivity().findViewById(R.id.userID_editext_user_setting_fragment);
        mPassword = (EditText)this.getActivity().findViewById(R.id.userpass_editext_user_setting_fragment);


        mUidText.setText(setting.getUid());

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserSetting setting = UserSetting.instance(SettingFragment.this.getActivity().getApplicationContext());
                setting.setUid(mUidText.getText().toString());
                setting.setPass(mPassword.getText().toString());
                DebugLog.d("用户信息更新成功: "+setting.getUid()+" "+setting.getPass());

                Toast.makeText(SettingFragment.this.getActivity().getApplicationContext(), "用户信息更新成功", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

}
