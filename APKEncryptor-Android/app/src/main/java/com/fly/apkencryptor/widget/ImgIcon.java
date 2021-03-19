package com.fly.apkencryptor.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


public class ImgIcon extends ImageView
{
    Context context;
    private int srcId;
    
    public ImgIcon(Context mContext){
        super(mContext);
        this.context=mContext;
        
        
        
    }

    
    public ImgIcon(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setClickable(true);
        
        
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_back);
        //this.setImageBitmap(bitmap);
        

    }
    
    
    public ImgIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attrName = attrs.getAttributeName(i);//获取属性名称
            switch (attrName) {
                    //根据属性获取资源ID
                case "src":
                    srcId = attrs.getAttributeResourceValue(i, 0);
                    break;
            }
        }
    }
    
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
                //按下
            case MotionEvent.ACTION_DOWN:
                this.setBackgroundColor(Color.parseColor("#BFE5E7"));
                break;
                
                
                //移动
            case MotionEvent.ACTION_MOVE:
                this.setBackgroundColor(Color.parseColor("#00000000"));
                if (srcId != 0)
                    this.setImageResource(srcId);
                
                break;
                
                
                //抬起
            case MotionEvent.ACTION_UP:
                this.setBackgroundColor(Color.parseColor("#00000000"));
                if (srcId != 0)
                    this.setImageResource(srcId);
                break;
        }
        return super.onTouchEvent(event);
    }
    
    
}
