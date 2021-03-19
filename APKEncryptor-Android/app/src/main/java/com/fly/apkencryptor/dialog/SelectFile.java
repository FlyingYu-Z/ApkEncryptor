package com.fly.apkencryptor.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.bean.FileInfo;
import com.fly.apkencryptor.utils.APKUtils;
import com.fly.apkencryptor.utils.BitmapUtils;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.FileInfoSort;
import com.fly.apkencryptor.utils.FileSizeUtils;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.ImgHelper;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.ToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

public class SelectFile {

    Context context;
    Conf conf;
    Activity activity;
    String suffix;
    SelectFileCallBack callBack;

    boolean isShowEdit=false;

    TextView tv_path;
    ListView lv_file;
    LinearLayout ln_visible_edit;
    AppCompatEditText ed_name;
    TextView tv_suffix;

    AlertDialog dialog;

    public SelectFile(Context mContext, String mSuffix, SelectFileCallBack mCallBack) {
        this.context = mContext;
        this.activity = (Activity) context;
        this.conf = new Conf(context);
        this.suffix = mSuffix;
        this.callBack = mCallBack;

        init();

    }


    public SelectFile(Context mContext,boolean selectSave, String mSuffix, SelectFileCallBack mCallBack) {
        this.context = mContext;
        this.activity = (Activity) context;
        this.conf = new Conf(context);
        this.suffix = mSuffix;
        this.callBack = mCallBack;
        this.isShowEdit=selectSave;

        init();

    }


    private void init(){


        View view = View.inflate(context, R.layout.view_select_file, null);
        tv_path = view.findViewById(R.id.view_select_file_TextView_path);
        lv_file = view.findViewById(R.id.view_select_file_ListView);
        ln_visible_edit=view.findViewById(R.id.view_select_file_LinearLayout_select_file_to_save);
        ed_name=view.findViewById(R.id.view_select_file_AppCompatEditText_name);
        tv_suffix=view.findViewById(R.id.view_select_file_TextView_suffix);


        String btnName="";
        if(isShowEdit){
            ed_name.setText("myfile");
            tv_suffix.setText("."+suffix);
            ln_visible_edit.setVisibility(View.VISIBLE );
            btnName=context.getString(R.string.ok);
        }else {
            ln_visible_edit.setVisibility(View.GONE );
            btnName=context.getString(R.string.cancel);
        }

        setHeader(lv_file);
        final FileAdapter adapter = new FileAdapter(context, lv_file);
        lv_file.setAdapter(adapter);

        dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.select_the_file))
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel),null)
                .setPositiveButton(btnName, null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBack.onCancel();
            }
        });
        if(isShowEdit){
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        }else {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowEdit){
                    File file=new File(adapter.Path,ed_name.getText().toString()+"."+suffix);
                    if(file.exists()){
                        ToastUtils.show(context.getString(R.string.the_file_has_existed));
                        return;
                    }
                    dialog.dismiss();
                    callBack.onSelected(file.getAbsolutePath());
                }else {
                    dialog.dismiss();
                    callBack.onCancel();
                }
            }
        });




        View header = lv_file.findViewWithTag("header");
        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.goParent();

            }

        });


        lv_file.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                FileInfo fileInfo = (FileInfo) lv_file.getItemAtPosition(position);
                String filePath = fileInfo.Path;
                File file = new File(filePath);
                if (file.isDirectory()) {
                    adapter.getFiles(filePath);
                    return;
                }

                if(isShowEdit){
                    return;
                }

                if (!FileUtils.getSuffix(filePath).equals(suffix)) {
                    ToastUtils.show(String.format(context.getString(R.string.please_select_a_file_with_x_suffix), suffix));
                    return;
                }

                dialog.dismiss();
                callBack.onSelected(filePath);


            }


        });

    }

    public void setHeader(ListView listview) {
        View view = LinearLayout.inflate(context, R.layout.item_file, null);
        ImageView icon = view.findViewById(R.id.item_file_ImageView_icon);
        TextView name = view.findViewById(R.id.item_file_TextView_name);
        TextView time = view.findViewById(R.id.item_file_TextView_time);
        TextView size = view.findViewById(R.id.item_file_TextView_size);
        ((LinearLayout) time.getParent()).setVisibility(View.GONE);
        icon.setImageResource(R.mipmap.ic_folder);
        name.setText("…");
        view.setTag("header");

        listview.addHeaderView(view);


    }


    public class FileAdapter extends BaseAdapter {


        Context context;
        Activity activity;
        public List<FileInfo> fileList;
        ListView listview;
        public AsyncImageLoader asyncImageLoader;
        public String Path;

        public FileAdapter(final Context context, ListView listview) {
            this.context = context;
            this.activity = (Activity) context;
            this.fileList = new ArrayList<FileInfo>();
            this.listview = listview;
            asyncImageLoader = new AsyncImageLoader();

            Path = conf.getCuPath();

            getFiles(Path);

        }


        public void goParent() {
            fileList.clear();
            String path = "/";
            path = Path;

            if (!path.equals("/")) {
                path = new File(path).getParentFile().getAbsolutePath();
            }
            getFiles(path);


        }


        public void refresh() {
            getFiles(Path);


        }


        public void getFiles(String dir) {
            Path = dir;
            fileList.clear();
            notifyDataSetChanged();

            try {
                dir = conf.DesPath(dir);
                File DirFile = new File(dir);
                String[] files = null;

                if (DirFile.list() == null) {
                    dir = FileUtils.getSDPath();
                    files = new File(dir).list();
                } else {

                    files = DirFile.list();

                }
                conf.setCuPath(dir);

                for (int i = 0; i < files.length; i++) {
                    File file = new File(dir + files[i]);

                    if(!suffix.contains(FileUtils.getSuffix(file.getAbsolutePath()))){
                        continue;
                    }

                    FileInfo fileInfo = new FileInfo();
                    fileInfo.Path = dir + files[i];
                    fileInfo.Name = file.getName();
                    fileInfo.Time = FileUtils.getModifiedTime(file.getAbsolutePath());
                    if (file.isFile()) {
                        fileInfo.Size = FileSizeUtils.getAutoFileOrFilesSize(file.getAbsolutePath());
                    } else {
                        fileInfo.Size = "";
                    }


                    fileList.add(fileInfo);

                }


            } catch (Exception e) {
                fileList.clear();
                new alert(context, e.toString());
            }


            FileInfoSort.sort(fileList);

            tv_path.setText(Path);

            notifyDataSetChanged();

        }


        public void setData(ArrayList<FileInfo> fileList) {

            this.fileList = fileList;
            notifyDataSetChanged();

        }


        @Override
        public int getCount() {
            if (fileList != null && fileList.size() > 0) {
                return fileList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (fileList != null && fileList.size() > 0) {
                return fileList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        public List<FileInfo> getData() {
            return fileList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            FileInfoHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_file, null);
                holder = new FileInfoHolder();

                holder.Icon = (ImageView) convertView.findViewById(R.id.item_file_ImageView_icon);
                holder.Name = (TextView) convertView.findViewById(R.id.item_file_TextView_name);
                holder.Time = (TextView) convertView.findViewById(R.id.item_file_TextView_time);
                holder.Size = (TextView) convertView.findViewById(R.id.item_file_TextView_size);
                holder.State = (TextView) convertView.findViewById(R.id.item_file_TextView_state);

                convertView.setTag(holder);


            } else {
                holder = (FileInfoHolder) convertView.getTag();
            }


            FileInfo fileInfo = fileList.get(position);

            displayImage(position, fileInfo.Path, holder.Icon, holder.State);

            holder.Name.setText(fileInfo.Name);
            holder.Time.setText(fileInfo.Time);
            holder.Size.setText(fileInfo.Size);


            if (listview.isItemChecked(position)) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }


            return convertView;
        }


        private void displayImage(int position, String imageUrl, ImageView imageView, TextView state) {
            imageView.setTag(imageUrl);
            state.setText("");
            state.setTag("状态" + imageUrl);
            asyncImageLoader.loadImage(context, this, position, imageUrl, imageLoadListener);

        }

        public void loadImage() {
            int start = listview.getFirstVisiblePosition();
            int end = listview.getLastVisiblePosition();
            if (end >= getCount()) {
                end = getCount() - 1;
            }
            asyncImageLoader.setLoadLimit(start, end);
            asyncImageLoader.unlock();
        }


        OnImageLoadListener imageLoadListener = new OnImageLoadListener() {

            @Override
            public void onImageLoad(Integer t, Drawable drawable, String url) {
                View view = listview.findViewWithTag(url);
                if (view != null) {
                    ImageView iv = (ImageView) view.findViewById(R.id.item_file_ImageView_icon);
                    iv.setBackgroundDrawable(drawable);

                    TextView state = (TextView) listview.findViewWithTag("状态" + url).findViewById(R.id.item_file_TextView_state);
                    if (FileUtils.getSuffix(url).equals("apk") &&
                            APKUtils.isApkInstalled(context, APKUtils.getPkgName(context, url)) &&
                            state.getTag().equals("状态" + url) &&
                            new File(url).isFile()) {
                        state.setText(context.getString(R.string.installed));
                        state.setTextColor(Color.RED);
                    }

                }
            }

            @Override
            public void onError(Integer t, String url) {
                View view = listview.findViewWithTag(url);
                if (view != null) {
                    ImageView iv = (ImageView) view.findViewById(R.id.item_file_ImageView_icon);
                    iv.setBackgroundResource(R.drawable.ic_launcher);
                }
            }

        };


    }


    public interface OnImageLoadListener {
        public void onImageLoad(Integer t, Drawable drawable, String url);

        public void onError(Integer t, String url);
    }


    class AsyncImageLoader {

        private Object lock = new Object();
        private boolean mAllowLoad = true;
        private boolean firstLoad = true;
        private int mStartLoadLimit = 0;
        private int mStopLoadLimit = 0;
        final Handler handler = new Handler();

        private HashMap<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();


        public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
            if (startLoadLimit > stopLoadLimit) {
                return;
            }
            mStartLoadLimit = startLoadLimit;
            mStopLoadLimit = stopLoadLimit;
        }

        public void restore() {
            mAllowLoad = true;
            firstLoad = true;
        }

        public void lock() {
            mAllowLoad = false;
            firstLoad = false;
        }

        public void unlock() {
            mAllowLoad = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        public void loadImage(final Context context, final FileAdapter adapter, Integer t, String imageUrl, OnImageLoadListener listener) {
            final OnImageLoadListener mListener = listener;
            final String mImageUrl = imageUrl;
            final Integer mt = t;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (!mAllowLoad) {
                        synchronized (lock) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    if (mAllowLoad && firstLoad) {
                        loadImage(context, adapter, mImageUrl, mt, mListener);
                    }

                    if (mAllowLoad && mt <= mStopLoadLimit && mt >= mStartLoadLimit) {
                        loadImage(context, adapter, mImageUrl, mt, mListener);
                    }
                }

            }).start();
        }

        public void loadImage(Context context, FileAdapter adapter, final String mImageUrl, final Integer mt, final OnImageLoadListener mListener) {

            if (imageCache.containsKey(mImageUrl)) {
                SoftReference<Drawable> softReference = imageCache.get(mImageUrl);
                final Drawable d = softReference.get();
                if (d != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAllowLoad) {
                                mListener.onImageLoad(mt, d, mImageUrl);
                            }
                        }
                    });
                    return;
                }
            }
            try {


                Drawable drawable = null;

                File file = new File(mImageUrl);
                if (file.isFile()) {

                    String suffix = FileUtils.getSuffix(file.getAbsolutePath());
                    String path = file.getAbsolutePath();

                    if (suffix.equals("apk")) {
                        drawable = APKUtils.getApkIcon(context, path);
                    } else if (suffix.equals("png") || suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("gif")) {
                        drawable = new BitmapDrawable(BitmapUtils.decodeSampledBitmapFromSd(path, 100, 100));
                    } else if (suffix.equals("zip") || suffix.equals("jar") || suffix.equals("rar") || suffix.equals("7z")) {
                        drawable = ImgHelper.getDrawableFromResources(context, R.mipmap.ic_zip);
                    } else {
                        drawable = ImgHelper.getDrawableFromResources(context, R.mipmap.ic_file);
                    }
                } else {

                    drawable = ImgHelper.getDrawableFromResources(context, R.mipmap.ic_folder);
                }


                final Drawable d = drawable;
                if (d != null) {
                    imageCache.put(mImageUrl, new SoftReference<Drawable>(d));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mAllowLoad) {
                            mListener.onImageLoad(mt, d, mImageUrl);
                        }
                    }
                });
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onError(mt, mImageUrl);
                    }
                });
                e.printStackTrace();
            }
        }


    }


    public class FileInfoHolder {
        public ImageView Icon;
        public TextView Name;
        public TextView Time;
        public TextView Size;
        public TextView State;


    }


    public interface SelectFileCallBack {

        public void onSelected(String selectedPath);

        public void onCancel();


    }


}
