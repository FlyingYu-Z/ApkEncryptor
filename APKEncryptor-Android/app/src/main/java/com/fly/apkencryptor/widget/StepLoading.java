package com.fly.apkencryptor.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fly.apkencryptor.R;

import java.util.ArrayList;
import java.util.List;


public class StepLoading extends LinearLayout {


    Context context;
    List<StepInfo> steps=new ArrayList<>();

    public StepLoading(Context context) {
        super(context);
        init();
    }


    public StepLoading(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StepLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    public StepLoading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init(){
        this.setOrientation(VERTICAL);
        this.context=this.getContext();
    }


    public void iniSteps(List<StepInfo> mSteps){
        this.steps=mSteps;
        this.removeAllViews();

        for(int i=0;i<steps.size();i++){

            StepInfo stepInfo=steps.get(i);

            this.addView(getView(stepInfo.Name,stepInfo.Id));

        }



    }



    public void setStepStatus(final int Id,final int status){

        ((Activity)this.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {


                View view=findViewWithTag(Id);
                view.setVisibility(VISIBLE);

                ProgressBar progressBar=view.findViewById(R.id.item_loading_ProgressBar);
                ImageView imageView=view.findViewById(R.id.item_loading_ImageView);

                if(status==1){
                    progressBar.setVisibility(GONE);
                    imageView.setVisibility(VISIBLE);
                }

                if(status==0){
                    progressBar.setVisibility(GONE);
                    imageView.setVisibility(VISIBLE);
                    imageView.setImageResource(R.mipmap.ic_failure);
                    imageView.setColorFilter(Color.RED);

                }

                if(status==2){
                    progressBar.setVisibility(VISIBLE);
                    imageView.setVisibility(GONE);
                }


            }
        });



    }

    public final static int Success=1;
    public final static int Failure=0;
    public final static int Running=2;


    private View getView(String name,int id){
        View view=LinearLayout.inflate(this.getContext(),R.layout.item_loading,null);


        ProgressBar progressBar=view.findViewById(R.id.item_loading_ProgressBar);
        ImageView imageView=view.findViewById(R.id.item_loading_ImageView);
        TextView textView=view.findViewById(R.id.item_loading_TextView_step);
        textView.setText(name);

        view.setTag(id);
        view.setVisibility(GONE);

        return view;
    }



    public static class StepInfo{
        String Name="";
        int Id=0;

        public StepInfo(String mName,int mId){
            this.Name=mName;
            this.Id=mId;

        }

    }




}
