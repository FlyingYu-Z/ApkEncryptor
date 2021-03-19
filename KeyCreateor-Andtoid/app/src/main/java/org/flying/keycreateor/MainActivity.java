package org.flying.keycreateor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.flying.keycreateor.Interface.SocketCallBack;
import org.flying.keycreateor.base.BaseActivity;
import org.flying.keycreateor.ui.DialogLoading;
import org.flying.keycreateor.utils.Custom;
import org.flying.keycreateor.utils.ListUtil;
import org.flying.keycreateor.utils.SocketUtils;
import org.flying.keycreateor.ui.ToastUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    Context context;
    DialogLoading progress;

    private ListView listview;
    private FloatingActionButton activityCardlistFloatingActionButton;

    MyAdapter myAdapter;
    ArrayList<KeyInfo> keyList = new ArrayList<KeyInfo>();
    private boolean isFinished = false;
    SmartRefreshLayout smartRefreshLayout;



    public void init(){

        context = this;
        progress = new DialogLoading(context, R.style.CustomDialog);
        smartRefreshLayout = find(R.id.activity_cardlist_SmartRefreshLayout);
        listview = (ListView) findViewById(R.id.activity_cardlist_ListView);
        activityCardlistFloatingActionButton = (FloatingActionButton) findViewById(R.id.activity_cardlist_floatingActionButton);

        myAdapter = new MyAdapter(context);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardlist);
        init();

        startRequestPermission();


        activityCardlistFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createKeysDialog();
            }
        });

        listview.setAdapter(myAdapter);

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                clearChoosed();
                getKeys(true);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getKeys(false);
            }
        });

        smartRefreshLayout.autoRefresh();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (listview.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {


                    if (listview.isItemChecked(position)) {
                        listview.setItemChecked(position, true);
                    } else {
                        listview.setItemChecked(position, false);
                    }

                    if (listview.getCheckedItemCount() == 0) {
                        clearChoosed();
                    }
                    myAdapter.notifyDataSetChanged();


                }


            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (listview.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE) {
                    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listview.setItemChecked(position, true);
                    myAdapter.notifyDataSetChanged();


                } else {


                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenu().add(1, 1, 1, "复制");
                    popupMenu.getMenu().add(2, 2, 2, "删除");
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case 1:
                                    break;

                                case 2:
                                    deleteCardsTip();
                                    break;
                            }

                            return true;
                        }
                    });


                }


                return true;
            }
        });



    }


    int KeyType=0;
    private void createKeysDialog() {

        View view=View.inflate(context,R.layout.dialog_create_keys,null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("生成卡密")
                .setView(view)
                .setCancelable(true)
                .create();
        dialog.show();

        RadioGroup rg_type=view.findViewById(R.id.dialog_create_keys_RadioGroup_dateType);
        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.dialog_create_keys_RadioButton_month:
                        KeyType=1;
                        break;

                    case R.id.dialog_create_keys_RadioButton_season:
                        KeyType=2;
                        break;

                    case R.id.dialog_create_keys_RadioButton_year:
                        KeyType=3;
                        break;

                }

            }
        });

        EditText ed_sum=view.findViewById(R.id.dialog_create_keys_EditText_sum);
        EditText ed_mark=view.findViewById(R.id.dialog_create_keys_EditText_mark);

        Button btn_create=view.findViewById(R.id.dialog_create_keys_Button_create);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int sum = Integer.parseInt(ed_sum.getText().toString());
                    String mark = ed_mark.getText().toString();

                    if(sum<=0){
                        return;
                    }

                    if(KeyType==0){
                        return;
                    }

                    createKeys(dialog, sum, KeyType, mark);
                }catch (Exception e){
                    ToastUtils.show(e.toString());
                }

            }
        });


    }

    private void createKeys(AlertDialog dialog,int sum, int type,String mark){
        try {
            JSONObject json = new JSONObject();
            json.put("Type", Custom.TYPE_MANAGE_KEY);
            json.put("Code", 2);
            json.put("Sum", sum);
            json.put("KeyType", type);
            json.put("Mark", mark);

            new SocketUtils(context, json, new SocketCallBack() {
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
                            getKeys(true);
                        }
                        ToastUtils.show(backJson.getString("msg"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.show("服务器繁忙");
                    }

                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show("网络连接失败");
                }

                @Override
                public void onFinished() {
                    progress.dismiss();
                }
            });
        } catch (Exception e) {
            ToastUtils.show("网络连接失败");
        }

    }


    //开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions((Activity) this, permissions, 321);
    }

    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };



    public void setClipBoardText(String str)
    {

        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(null, str));

        }
    }



    public void deleteCardsTip() {

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < listview.getCount(); i++) {
            if (listview.isItemChecked(i)) {
                KeyInfo keyInfo = keyList.get(i);
                list.add(keyInfo.ID);
            }
        }

        String result = ListUtil.ListToString(list);


        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle("温馨提示")
                .setMessage("确定要删除这" + list.size() + "项吗？")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteKeys(result);
                    }
                })
                .create();
        dialog.show();


    }


    private void deleteKeys(String list){
        try {
            JSONObject json = new JSONObject();
            json.put("Type", Custom.TYPE_MANAGE_KEY);
            json.put("Code", 3);
            json.put("List", list);

            new SocketUtils(context, json, new SocketCallBack() {
                @Override
                public void onStart() {
                    progress.show();
                }

                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject backJson = new JSONObject(result);
                        if (backJson.getBoolean("result")) {
                            getKeys(true);
                        }
                        ToastUtils.show(backJson.getString("msg"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.show("服务器繁忙");
                    }

                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show("网络连接失败");
                }

                @Override
                public void onFinished() {
                    progress.dismiss();
                }
            });
        } catch (Exception e) {
            ToastUtils.show("网络连接失败");
        }

    }




    public void clearChoosed() {

        listview.clearChoices();
        listview.setChoiceMode(ListView.CHOICE_MODE_NONE);
        for (int i = 0; i < listview.getCount(); i++) {
            listview.setItemChecked(i, false);
        }

        myAdapter.notifyDataSetChanged();
    }

    public void chooseAll() {

        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        for (int i = 0; i < listview.getCount(); i++) {
            listview.setItemChecked(i, true);
        }
    }


    public void getKeys(final boolean isRefresh) {

        int cuSize = keyList.size();
        if (isRefresh) {
            cuSize = 0;
        }
        try {
            JSONObject json = new JSONObject();
            json.put("Type", Custom.TYPE_MANAGE_KEY);
            json.put("cuSize", cuSize);
            json.put("Code", 1);
            new SocketUtils(context, json, new SocketCallBack() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject backJson = new JSONObject(result);
                        if (backJson.getBoolean("result")) {
                            if (isRefresh) {
                                keyList.clear();
                            }

                            String Array = new JSONObject(result).getString("Array");
                            JSONArray array = new JSONArray(Array);

                            if (array.length() < 50) {
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();
                            }

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);

                                String ID=json.getString("ID");
                                String Key=json.getString("Key");
                                String Type=json.getString("Type");
                                String UserID=json.getString("UserID");
                                String isUsed=json.getString("isUsed");
                                String Mark=json.getString("Mark");
                                String CreateTime=json.getString("CreateTime");
                                String ActivateTime=json.optString("ActivateTime","");


                                KeyInfo keyInfo = new KeyInfo();
                                keyInfo.ID = ID;
                                keyInfo.Key = Key;
                                keyInfo.Type = Integer.parseInt(Type);
                                keyInfo.UserID = Integer.parseInt(UserID);
                                keyInfo.isUsed = Integer.parseInt(isUsed);
                                keyInfo.Mark = Mark;
                                keyInfo.CreateTime=CreateTime;
                                keyInfo.ActivateTime=ActivateTime;

                                keyList.add(keyInfo);

                            }
                            clearChoosed();
                            myAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.show("服务器繁忙");
                    }

                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show("网络连接失败");
                }

                @Override
                public void onFinished() {
                    if (isRefresh) {
                        smartRefreshLayout.finishRefresh();
                    } else {
                        smartRefreshLayout.finishLoadMore();
                    }

                }
            });
        } catch (Exception e) {
            ToastUtils.show("网络连接失败");
        }
    }

    class MyAdapter extends BaseAdapter {
        Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (keyList != null && keyList.size() > 0) {
                return keyList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (keyList != null && keyList.size() > 0) {
                return keyList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_key, null);
                holder = new Holder();

                holder.Key = (TextView) convertView.findViewById(R.id.item_key_Key);
                holder.isUsed = (TextView) convertView.findViewById(R.id.item_key_isUsed);
                holder.Type = (TextView) convertView.findViewById(R.id.item_key_TextView_type);
                holder.Copy = (Button) convertView.findViewById(R.id.item_key_Copy);
                holder.Mark = (TextView) convertView.findViewById(R.id.item_key_TextView_mark);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            final KeyInfo keyInfo = keyList.get(position);
            holder.Key.setText(keyInfo.Key);

            if (keyInfo.isUsed == 1) {
                holder.isUsed.setText("已使用");
                holder.isUsed.setTextColor(Color.RED);
            } else {
                holder.isUsed.setText("未使用");
                holder.isUsed.setTextColor(Color.GRAY);
            }

            if(keyInfo.Type==1){
                holder.Type.setText("月卡");
                holder.Type.setTextColor(Color.RED);
            }

            if(keyInfo.Type==2){
                holder.Type.setText("季卡");
                holder.Type.setTextColor(Color.BLACK);
            }

            if(keyInfo.Type==3){
                holder.Type.setText("年卡");
                holder.Type.setTextColor(Color.GREEN);
            }


            if(!keyInfo.Mark.isEmpty()){
                holder.Mark.setText("备注："+ keyInfo.Mark);
            }



            holder.Copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClipBoardText(keyInfo.Key);
                    ToastUtils.show("卡密已复制到剪切板");
                }
            });


            if (listview.isItemChecked(position)) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }


            return convertView;
        }


    }


    class KeyInfo {
        public String ID;
        public String Key;
        public int Type;
        public int UserID;
        public int isUsed;
        public String Mark;
        public String CreateTime;
        public String ActivateTime;


    }

    class Holder {
        public TextView Key;
        public TextView isUsed;
        public TextView Type;
        public Button Copy;
        public TextView Mark;

    }



}
