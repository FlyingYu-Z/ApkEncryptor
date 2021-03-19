package com.fly.apkencryptor.utils;


public class FActivityInfo
{
    public String name = "";
    public String lable = "", theme = null;
    public boolean isMain = false;

    public FActivityInfo()
	{
    }
    public FActivityInfo(String name, String lable, String theme)
	{
        this.name = name;
        this.lable = lable;
        this.theme = theme;
    }
}

