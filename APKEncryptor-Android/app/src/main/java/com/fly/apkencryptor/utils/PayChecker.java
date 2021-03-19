package com.fly.apkencryptor.utils;

import com.fly.apkencryptor.widget.ToastUtils;

public class PayChecker {

    public PayChecker(){

        boolean isPaid=SPUtils.getBoolean("conf","isPaid");

        switch (isPaid?1:0){
            case 1:

                break;

            case 0:
                //ToastUtils.show("This is not offical version.\nPlease downlaod it from Google Play");
                new Thread(){
                    @Override
                    public void run(){
                        try {

                            Thread.sleep(3000);
                            //CustomFun.exit();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;

        }

    }

}
