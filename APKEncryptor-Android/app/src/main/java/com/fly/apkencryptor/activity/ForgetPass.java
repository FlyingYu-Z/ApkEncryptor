package com.fly.apkencryptor.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

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

public class ForgetPass extends BaseActivity {


    LinearLayout ln_inputEmail;
    LinearLayout ln_inputCode;

    AppCompatEditText ed_email;
    AppCompatEditText ed_code;
    AppCompatEditText ed_password;
    AppCompatEditText ed_confirm_password;


    String Email;
    String Code;
    String Password;
    String ConfirmPassword;


    private void init(){
        ln_inputEmail=find(R.id.activity_forget_pass_LinearLayout_inputEmail);
        ln_inputCode=find(R.id.activity_forget_pass_LinearLayout_inputCode);

        ed_email=find(R.id.activity_forget_pass_AppCompatEditText_email);
        ed_code=find(R.id.activity_forget_pass_AppCompatEditText_code);
        ed_password=find(R.id.activity_forget_pass_AppCompatEditText_Password);
        ed_confirm_password=find(R.id.activity_forget_pass_AppCompatEditText_Confirm_Password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        init();
        this.Title=getString(R.string.forget_password);

        ln_inputCode.setVisibility(View.GONE);
    }


    private void getValue(){
        Email=ed_email.getText().toString();
        Code=ed_code.getText().toString();
        Password=ed_password.getText().toString();
        ConfirmPassword =ed_confirm_password.getText().toString();

    }

    public void onclick_send_code(View view) {
        getValue();

        if(Email.isEmpty()||!Email.contains("@")){
            ToastUtils.show(getString(R.string.please_input_the_correct_email_address));
            return;
        }

        showSendDialog();

    }


    private void showSendDialog(){

        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle(context.getString(R.string.tips))
                .setMessage(getString(R.string.send_code_change_pass))
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
            jsonObject.put("Type", Custom.TYPE_CHANGE_PASS_SEND_CODE);
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
                            ln_inputEmail.setVisibility(View.GONE);
                            ln_inputCode.setVisibility(View.VISIBLE);

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


    private void changePass(){

        getValue();

        if(!Password.equals(ConfirmPassword)){
            ToastUtils.show(getString(R.string.the_two_passwords_are_inconsistent));
            return;
        }


        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_CHANGE_PASS_BY_CODE);
            jsonObject.put("Email", Email);
            jsonObject.put("newPass", Password);
            jsonObject.put("Code", Code);

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
                            conf.setIsLogined(false);
                            conf.setLoginKey("");
                            showFinish(backJson.getString("msg"));
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

    private void showFinish(String msg){


        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle(context.getString(R.string.tips))
                .setMessage(msg)
                .setCancelable(false)

                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).create();
        dialog.show();


    }

    public void onclick_verify_code(View view) {
        changePass();
    }
}
