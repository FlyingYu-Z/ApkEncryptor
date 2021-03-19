package com.fly.apkencryptor.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.base.BaseActivity;
import com.fly.apkencryptor.utils.BYProtectUtils;
import com.fly.apkencryptor.utils.alert;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OpenSourceList extends BaseActivity
{
    
    Context context;
    ListView listview;

    List<OSInfo> osList=new ArrayList<>();
    OSAdapter adapter;
    
    public void init(){
        context=this;
        listview=findViewById(R.id.activity_opensourcelist_ListView);
        
        adapter=new OSAdapter(context);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensourcelist);
        init();

        this.Title=getString(R.string.open_source_license);

        String content= BYProtectUtils.readAssetsTxt("OpenSourceList.json");

        new alert(context,content);
        try{
            JSONObject json=new JSONObject(content);
            Iterator keys=json.keys();
            while (keys.hasNext()){
                String key=keys.next().toString();

                JSONObject item=new JSONObject(json.getString(key));

                OSInfo osInfo=new OSInfo();
                osInfo.Name=item.getString("Name");
                osInfo.License=item.getString("License");
                osInfo.Url=item.getString("Url");
                osInfo.Description=item.getString("Description");

                osList.add(osInfo);


            }

            adapter.setData(osList);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    openUrl(osList.get(position).Url);

                }


            });


        }catch (Exception e){
            new alert(context,e.toString());
        }

        

        
    }
    
    
    
    
    
    


    public class OSAdapter extends BaseAdapter
    {


        Context context;
        public List<OSInfo> osList;
        public String Path;

        public OSAdapter(final Context context)
        {
            this.context = context;
            this.osList = new ArrayList<OSInfo>();
            
        }


        public void setData(List<OSInfo> osList)
        {

            this.osList = osList;
            notifyDataSetChanged();

        }


        @Override
        public int getCount()
        {
            if (osList != null && osList.size() > 0)
            {
                return osList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            if (osList != null && osList.size() > 0)
            {
                return osList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public boolean hasStableIds()
        {
            return true ;
        }



        public List<OSInfo> getData()
        {
            return osList;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            OSHolder holder=null;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_opensource, null);
                holder = new OSHolder();
                
                holder.Name = (TextView)convertView.findViewById(R.id.item_open_source_TextView_Name);
                holder.License = (TextView)convertView.findViewById(R.id.item_open_source_TextView_License);
                holder.Url = (TextView)convertView.findViewById(R.id.item_open_source_TextView_Url);
                holder.Description = (TextView)convertView.findViewById(R.id.item_open_source_TextView_Description);

                convertView.setTag(holder);


            }
            else
            {
                holder = (OSHolder) convertView.getTag();
            }



            OSInfo OSInfo=osList.get(position);
            
            holder.Name.setText(OSInfo.Name);
            holder.License.setText(OSInfo.License);
            holder.Url.setText(OSInfo.Url);
            holder.Description.setText(OSInfo.Description);
            
            
            return convertView;
        }

        
    }
    
    class OSHolder{
        public TextView Name;
        public TextView License;
        public TextView Url;
        public TextView Description;

    }
    
    
    class OSInfo{
        public String Name="";
        public String Url="";
        public String License="";
        public String Description="";
        
    }
    
    

    public void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
    
}
