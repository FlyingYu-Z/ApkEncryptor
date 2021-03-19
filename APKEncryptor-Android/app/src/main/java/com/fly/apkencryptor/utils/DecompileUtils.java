package com.fly.apkencryptor.utils;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by  Rex on 2017/8/5.
 * 用于反编译修改或者添加布局的工具类
 * 绕过R文件从assets中直接加载
 * 未做特别提示 均只能加载编译后的！
 */

public class DecompileUtils {

    public static DecompileUtils utils = null;
    public static Context mContext = null;

    public static synchronized DecompileUtils getInstance(Context context) {
        if (utils == null) {
            utils = new DecompileUtils();
            mContext = context;
        }
        return utils;

    }

    /**
     * 通过assets下的路径加载 注意得加上assets/
     *
     * @param path "assets/.xml"
     * @return XmlPullParser
     */
    public XmlPullParser getLayoutXmlPullParser(String path) {
        XmlPullParser xmlPullParser = null;
        AssetManager assetManager = mContext.getAssets();
        try {
            xmlPullParser = assetManager.openXmlResourceParser("assets/" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlPullParser;
    }

    /**
     * 加载Drawable xml 编译后的
     *
     * @param name
     * @return
     */
    public Drawable getDrawableXmlPullParserAfter(String name) {
        Drawable drawable = null;
        AssetManager assetManager = mContext.getAssets();
        try {
            XmlPullParser xmlPullParser = assetManager.openXmlResourceParser("assets/" + name);
            drawable = Drawable.createFromXml(mContext.getResources(), xmlPullParser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 该方法可以加载编译前的
     *
     * @param name
     * @return
     */
    public Drawable getDrawableXmlInputStremBefore(String name) {

        Drawable drawable = null;
        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream inputStream = assetManager.open("assets/" + name);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }


    /**
     * @param filename
     * @return
     */
    public static Bitmap getBitmapForName(String filename) {
        Bitmap bitmap = null;
        try {
            InputStream is = mContext.getAssets().open(filename);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return bitmap;
        }

    }

    /**
     * @param filename
     * @return
     */
    public Bitmap getBitmapForFile(String filename) {
        return BitmapFactory.decodeFile(filename);
    }

}
