package com.fly.apkencryptor.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.fly.apkencryptor.Interface.SocketCallBack;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.base.BaseActivity;
import com.fly.apkencryptor.utils.Custom;
import com.fly.apkencryptor.utils.SocketUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.ToastUtils;

import org.json.JSONObject;

public class signup extends BaseActivity {

    AppCompatEditText ed_account;
    AppCompatEditText ed_password;
    AppCompatEditText ed_confirm_password;

    String Email;
    String Password;


    private void init(){

        ed_account=find(R.id.activity_signup_AppCompatEditText_email);
        ed_password=find(R.id.activity_signup_AppCompatEditText_password);
        ed_confirm_password=find(R.id.activity_signup_AppCompatEditText_password_again);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        this.Title=getString(R.string.signup);


    }


    public void onclick_startSignup(View view) {
        Email=ed_account.getText().toString();
        Password=ed_password.getText().toString();
        String ConfirmPassword=ed_confirm_password.getText().toString();

        if(!Email.contains("@")){
            ToastUtils.show(getString(R.string.please_input_the_correct_email_address));
            return;
        }

        if(!Password.equals(ConfirmPassword)){
            ToastUtils.show(getString(R.string.the_two_passwords_are_inconsistent));
            return;
        }

        verifyServer();

    }

    //验证服务器是否允许注册
    private void verifyServer(){


        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_IS_ALLOW_SIGNUP);
            jsonObject.put("Email", Email);

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
                            showSendDialog();
                        } else {
                            ToastUtils.show(backJson.getString("msg"));
                        }

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

    private void showSendDialog(){

        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle(context.getString(R.string.tips))
                .setMessage(getString(R.string.send_code_signup))
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getString(R.string.ok), null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(dialog);
            }
        });

    }

    private void sendEmail(AlertDialog dialog){

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_SIGNUP_SEND_CODE);
            jsonObject.put("Email", Email);

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
                            dialog.dismiss();
                            showInputCode();
                        } else {
                            ToastUtils.show(backJson.getString("msg"));
                        }

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


    private void showInputCode(){
        View view = View.inflate(context,R.layout.view_signup_verify_code,null);

        AppCompatEditText ed_code=view.findViewById(R.id.view_signup_verify_code_AppCompatEditText_code);
        TextView tv_label=view.findViewById(R.id.view_signup_verify_code_TextView_label);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getString(R.string.ok), null)
                .create();
        dialog.show();

        ed_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code=ed_code.getText().toString();
                tv_label.setText(code.length()+"/"+6);
                if(code.length()>6){
                    tv_label.setTextColor(Color.RED);
                }else{
                    tv_label.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code=ed_code.getText().toString();
                if(code.length()!=6){
                    ToastUtils.show(getString(R.string.error));
                    return;
                }
                signup(dialog,code);
            }
        });



    }


    private void signup(AlertDialog dialog,String code){


        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_SIGNUP);
            jsonObject.put("Email", Email);
            jsonObject.put("Password", Password);
            jsonObject.put("Code", code);

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
                            dialog.dismiss();
                            new alert(context,getString(R.string.signup_successfully));
                        } else {
                            ToastUtils.show(backJson.getString("msg"));
                        }

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


}
