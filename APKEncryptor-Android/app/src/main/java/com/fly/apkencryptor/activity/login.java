package com.fly.apkencryptor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.fly.apkencryptor.Interface.SocketCallBack;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.base.BaseActivity;
import com.fly.apkencryptor.utils.Custom;
import com.fly.apkencryptor.utils.ExceptionUtils;
import com.fly.apkencryptor.utils.SocketUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.ToastUtils;

import org.json.JSONObject;

public class login extends BaseActivity {

    AppCompatEditText ed_account;
    AppCompatEditText ed_password;


    private void init(){
        ed_account=find(R.id.activity_login_AppCompatEditText_account);
        ed_password=find(R.id.activity_login_AppCompatEditText_password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        this.Title=getString(R.string.login);

        if (!conf.getAccount().isEmpty()){
            ed_account.setText(conf.getAccount());
        }

    }

    public void onclick_startLogin(View view) {

        String Account=ed_account.getText().toString();
        String Password=ed_password.getText().toString();


        if(Account.isEmpty()||Password.isEmpty()){
            ToastUtils.show(getString(R.string.input_can_not_be_empty));
            return;
        }


        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_LOGIN);
            jsonObject.put("Account", Account);
            jsonObject.put("Password", Password);


            new SocketUtils(context, jsonObject, new SocketCallBack() {
                @Override
                public void onStart() {
                    progress.show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject backJson = new JSONObject(result);

                        if (backJson.getBoolean("result")) {
                            conf.setIsLogined(true);
                            conf.setToken(backJson.getString("Token"));
                            conf.setAccount(Account);
                            conf.setPassword(Password);
                            conf.setUserID(backJson.getString("ID"));
                            conf.setLoginKey(backJson.getString("LoginKey"));
                            Intent intent=new Intent(context,MainActivity.class);
                            intent.putExtra("update",true);
                            startActivity(intent);

                        } else {

                        }
                        ToastUtils.show(backJson.getString("msg"));

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show(getString(R.string.the_network_is_busy));

                    }
                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show(getString(R.string.the_network_is_busy));
                }

                @Override
                public void onFinished() {
                    progress.dismiss();
                }
            });


        }catch (Exception e){
            new alert(context,e.toString());
        }

    }

    public void onclick_enter_signup(View view) {
        StartActivity(signup.class);
    }

    public void onclick_enter_forgetPass(View view) {
        StartActivity(ForgetPass.class);
    }



}
