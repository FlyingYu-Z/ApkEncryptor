package com.fly.apkencryptor.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.fly.apkencryptor.Interface.SocketCallBack;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.Custom;
import com.fly.apkencryptor.utils.SocketUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.DialogLoading;
import com.fly.apkencryptor.widget.ToastUtils;

import org.json.JSONObject;

public class ActivateKey extends AlertDialog {
    Context context;
    MainActivity activity;
    Conf conf;
    DialogLoading progress;
    TextView tv_price;

    String BuyKeyUrl;
    String price;


    public ActivateKey(Context context) {
        super(context);
        this.context=context;
        this.activity=(MainActivity)context;
        this.conf=new Conf(context);
        this.progress=new DialogLoading(context);
        View view=View.inflate(context, R.layout.view_activate_key,null);
        setView(view);
        AppCompatEditText ed_key=view.findViewById(R.id.view_activate_key_AppCompatEditText_key);
        Button btn_buyKey=view.findViewById(R.id.view_activate_key_Button_buyKey);
        Button btn_activate=view.findViewById(R.id.view_activate_key_Button_activate);
        tv_price=view.findViewById(R.id.view_activate_key_TextView_price);

        getPrice();

        btn_buyKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(BuyKeyUrl);
            }
        });

        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Key=ed_key.getText().toString();
                if(Key.isEmpty()){
                    ToastUtils.show(context.getString(R.string.input_can_not_be_empty));
                    return;
                }
                activateKey(Key);
            }
        });

    }


    private void activateKey(String Key){

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_ACTIVATE_KEY);
            jsonObject.put("Token", conf.getToken());
            jsonObject.put("Key", Key);

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
                            dismiss();
                            activity.getUserInfo();
                        } else {
                        }

                        ToastUtils.show(backJson.getString("msg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show(context.getString(R.string.the_network_is_busy));

                    }
                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show(context.getString(R.string.the_network_is_busy));
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





    public void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }



    private void getPrice(){

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_GET_PRICE);

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
                            BuyKeyUrl=backJson.getString("BuyKeyUrl");
                            price=backJson.getString("price");
                            tv_price.setText(price);
                            show();
                        } else {
                            ToastUtils.show(backJson.getString("msg"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show(context.getString(R.string.the_network_is_busy));

                    }
                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show(context.getString(R.string.the_network_is_busy));
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
