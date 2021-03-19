package cn.beingyi.sub.ui;

import android.content.Context;
import android.widget.Toast;

public class JniAlert {

    public JniAlert(){



    }



    public void show(Context context,String msg){
        new alert(context,msg);
    }


    public void test(){
        int i=0;
        switch (i){
            case 1:
                System.out.println(i);
                break;

            case 2:
                return;
        }

    }

}
