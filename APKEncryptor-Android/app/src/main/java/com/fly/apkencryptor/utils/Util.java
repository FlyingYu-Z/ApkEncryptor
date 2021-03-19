package com.fly.apkencryptor.utils;


import com.fly.apkencryptor.application.MyApp;

public class Util
{
  public static int dp2px(float dpValue) {
      float scale = MyApp.getContext().getResources().getDisplayMetrics().density;
      return (int) (dpValue * scale + 0.5f);

   }



}
