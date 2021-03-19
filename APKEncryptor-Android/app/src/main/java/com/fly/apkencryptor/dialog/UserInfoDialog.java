package com.fly.apkencryptor.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.UserInfo;

public class UserInfoDialog extends AlertDialog {

    Conf conf;

    public UserInfoDialog(@NonNull Context context) {
        super(context);

        conf=new Conf(context);

        View view=View.inflate(context, R.layout.view_user_info,null);

        TextView tv_Email=view.findViewById(R.id.view_user_info_TextView_email);
        TextView tv_VIP=view.findViewById(R.id.view_user_info_TextView_VIP);
        TextView tv_SignupTime=view.findViewById(R.id.view_user_info_TextView_signupTime);
        TextView tv_LastLoginTime=view.findViewById(R.id.view_user_info_TextView_lastLoginTime);


        String VIP="";

        if(UserInfo.getBoolValue("isVIP")){
            VIP=UserInfo.getStrValue("VIP");
        }else{
            VIP="Non VIP";
        }

        tv_Email.setText(context.getString(R.string.email)+":"+conf.getAccount());
        tv_VIP.setText("VIP:"+ VIP);
        tv_SignupTime.setText(context.getString(R.string.signup_time)+":"+UserInfo.getStrValue("SignupTime"));
        tv_LastLoginTime.setText(context.getString(R.string.last_login_time)+":"+UserInfo.getStrValue("LoginTime"));

        setView(view);

    }
}
