package com.joyhonest.jh_drone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joyhonest.jh_fpv.MyNode;
import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.R;
import com.joyhonest.jh_ui.Storage;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class drone_grid_Activity extends AppCompatActivity  implements View.OnClickListener {


    private Button  btn_Back;
    private Button  btn_Select;
    private Button  btn_Delete;
    private GridView  gridView;
    private ImageView imageView_Type;

    boolean bExit;
    RelativeLayout DeleteAert;

    private MyAdapter myAdapter;
    private List<String> mList;
    private List<MyNode> nodes;
    private List<String> local_PhotolistFiles; // = MyApp.getInstance().local_PhotoList;
    private List<String> local_VideolistFiles; // = MyApp.getInstance().local_VideoList;

    private Button alart_cancel;
    private Button alart_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_grid);
        btn_Back = findViewById(R.id.btn_Back);
        btn_Select = findViewById(R.id.btn_Select);
        btn_Delete= findViewById(R.id.btn_Delete);
        gridView = findViewById(R.id.GridView);
        alart_cancel = findViewById(R.id.alart_cancel);
        alart_ok = findViewById(R.id.alart_ok);

        imageView_Type = findViewById(R.id.imageView_Type);
        DeleteAert = findViewById(R.id.DeleteAert);
        DeleteAert.setVisibility(View.INVISIBLE);

        if(JH_App.bBrowPhoto)
        {
            imageView_Type.setBackgroundResource(R.mipmap.jhd_photoicon);
        }
        else
        {
            imageView_Type.setBackgroundResource(R.mipmap.jhd_recordicon);
        }

        local_PhotolistFiles = new ArrayList<String>();
        local_VideolistFiles = new ArrayList<String>();

        WindowManager manger = getWindowManager();
        Display display = manger.getDefaultDisplay();
        //屏幕宽度
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x; //display.getWidth();
        screenWidth -=Storage.dip2px(this,15)*2;
        gridView.setNumColumns((int) (screenWidth / Storage.dip2px(this, 100)) - 1);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                F_GridItem_Clicked(parent,view,position,id);
            }
        });


        btn_Back.setOnClickListener(this);
        btn_Select.setOnClickListener(this);
        btn_Delete.setOnClickListener(this);
        alart_cancel.setOnClickListener(this);
        alart_ok.setOnClickListener(this);


        bExit = false;
        bEdit = false;

        _Init_Theard init = new _Init_Theard();
        init.start();
        EventBus.getDefault().register(this);

    }


    private class _Init_Theard extends Thread {
        @Override
        public void run() {
            F_Init();
        }
    }

    class MapComparator implements Comparator<String> {
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }

    }

    private boolean isSelectmode()
    {
        boolean bisSelectmode=false;
        if(nodes!=null)
        {
            for(MyNode node :nodes)
            {
                if(node.nSelectType!=0)
                {
                    bisSelectmode = true;
                    break;
                }
            }

        }
        return bisSelectmode;
    }

    private  void F_GridItem_Clicked(AdapterView<?> parent, View view, int position, long id)
    {

        MyNode node = nodes.get(position);
        boolean  bSelect=isSelectmode();
        if(bSelect)
        {
            if(node.nSelectType==1)
            {
                node.nSelectType = 2;
            }
            else
            {
                node.nSelectType = 1;
            }
            //DispDeleteIcon(CountSelected()>0);
            myAdapter.notifyDataSetChanged();
        }
        else
        {
            if(JH_App.bBrowPhoto)
            {
                JH_App.dispList.clear();
                for (MyNode nodea : nodes) {
                    JH_App.dispList.add(nodea.sPath);
                }

                JH_App.dispListInx = position;
                Intent mainIntent = new Intent(drone_grid_Activity.this, DispPhotoActivity.class);
                startActivity(mainIntent);
            }
            else
            {
                JH_App.sVideoPath = node.sPath;
                Intent mainIntent = new Intent(drone_grid_Activity.this, DispVideoActivity.class);
                startActivity(mainIntent);

            }
        }

    }

    private String getFileType(String filename){
        if(filename==null)
            return null;
       // filename.toLowerCase();
        int pos = filename.lastIndexOf(".");
        if(pos == -1){
            return null;
        }
        return filename.substring(pos+1);
    }

    private void F_GetAllPhotoLocal() {
        String sPath = JH_App.sLocalPhoto;
        local_PhotolistFiles.clear();
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();
            String sExt;
            for (int i = 0; i < files.length; i++) {
                File f = files[i];

                sExt = getFileType(f.getPath());
                if(sExt!=null) {
                    if (sExt.equalsIgnoreCase("jpg") ||
                            sExt.equalsIgnoreCase("png") ||
                            sExt.equalsIgnoreCase("bmp")) {
                        local_PhotolistFiles.add(f.getPath());
                    }
                }
            }
        }
        mList = local_PhotolistFiles;
        Collections.sort(mList, new MapComparator());

    }

    private void F_GetAllVideoLocal() {
        String sPath = JH_App.sLocalVideo;
        local_VideolistFiles.clear();
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();

            String sExt;
            for (File f : files) {
                sExt = getFileType(f.getPath());
                if(sExt!=null) {
                    if (sExt.equalsIgnoreCase("mp4"))
                    {
                        local_VideolistFiles.add(f.getPath());
                    }
                }
            }
        }
        mList = local_VideolistFiles;
        Collections.sort(mList, new MapComparator());
    }

    private  void F_Init()
    {

        //WaitView.setVisibility(View.VISIBLE);

        int nBrow = 0;
        if(!JH_App.bBrowPhoto)
            nBrow = 1;

        if (JH_App.bBrowPhoto)
        {
            F_GetAllPhotoLocal();
            if(nodes!=null)
            {
                nodes.clear();
            }
            nodes = new ArrayList<MyNode>();
            if ( mList!= null) {
                for (String sPath : mList) {
                    if(bExit)
                        break;;
                    Bitmap bitmap = GetSuonuitu(sPath);
                    MyNode node = new MyNode(nBrow);
                    node.bitmap = bitmap;
                    node.sPath = sPath;
                    nodes.add(node);
                }
            }
        }
        else
        {
            //imageType.setImageResource(R.mipmap.videofolder_nor_jh);
            F_GetAllVideoLocal();
            if(nodes!=null)
            {
                nodes.clear();
            }
            nodes = new ArrayList<MyNode>();
            if ( mList!= null) {
                for (String sPath : mList) {
                    if(bExit)
                        break;;
                    Bitmap bitmap = getVideoThumbnail(sPath);
                    MyNode node = new MyNode(0);
                    node.bitmap = bitmap;
                    node.sPath = sPath;
                    nodes.add(node);
                }
            }
        }

        /*
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(nodes.size() == 0) {
                        TitleText_View.setText(R.string.NoFile);
                    }
                    else
                    {
                        String sFormat=Grid_View.this.getString(R.string.Total_files);
                        String s  = String.format(sFormat,nodes.size());
                        TitleText_View.setText(s);
                    }
                }
            });
        }
        */
        if(bExit) {
            if(nodes!=null)
            {
                nodes.clear();
            }
            return;
        }

        EventBus.getDefault().post("abd","GetALLFiles_OK");

    }

    private String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }

    }

    private Bitmap GetSuonuitu(String sPath) {

        Bitmap bitmap = null;
        String strs = "";

        String sf = getFileName(sPath);
        strs = this.getCacheDir() + "/" + sf + ".thb.png";

        bitmap = BitmapFactory.decodeFile(strs);
        if (bitmap != null)
            return bitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        bitmap = BitmapFactory.decodeFile(sPath, options); //此时返回bm为空
        options.inJustDecodeBounds = false;
        //计算缩放比
        int be = (int) (options.outHeight / (float) 100);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(sPath, options);
        F_SaveBitmap(bitmap, strs);
        return bitmap;
    }
    private Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        String strs = "";
        String str = getFileName(filePath);
        strs = this.getCacheDir() + "/" + str + ".v_thb.png";
        bitmap = BitmapFactory.decodeFile(strs);
        if (bitmap != null)
            return bitmap;


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(1);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        F_SaveBitmap(bitmap, strs);
        return bitmap;
    }
    private void F_SaveBitmap(Bitmap bm, String sPath) {
        Log.e("WIFI", "保存图片");
        if (bm == null)
            return;
        File f = new File(sPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("WIFI", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    class MyAdapter extends BaseAdapter {
        private Context context;
        private int viewResourceId;
        private List<MyNode> mfilelist;
        private LayoutInflater mInflater;


        MyAdapter(Context context, int resourceid, List<MyNode> objects) {
            this.context = context;
            this.viewResourceId = resourceid;
            this.mfilelist = objects;
            this.mInflater = LayoutInflater.from(context);
            /*
            if(MyApp.bScreenRota)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else
            {


                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            */
        }

        public int getCount() {
            return mfilelist.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            MyAdapter.MyViewHolder holder = null;
            MyNode node = mfilelist.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(viewResourceId, null);
                holder = new MyAdapter.MyViewHolder();
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.Grid_progressBar1);
                holder.progressBar.setProgress(0);
                holder.progressBar.setMax(1000);
                holder.icon = (ImageView) convertView.findViewById(R.id.Grid_imageView1);
                holder.video_bg = (ImageView) convertView.findViewById(R.id.video_bg);
                holder.caption = (TextView) convertView.findViewById(R.id.Grid_textView1);
                holder.SelectIcon = (ImageView)convertView.findViewById(R.id.image_Select);
                holder.sUrl = "";
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.MyViewHolder) convertView.getTag();
            }
            //if(holder.sUrl!="")
            if (node != null) {
                {
                    if(node.nSelectType==1)
                    {
                        holder.SelectIcon.setVisibility(View.VISIBLE);
                        holder.SelectIcon.setBackgroundResource(R.mipmap.selectedflag_fly_0_jh);
                    }else if(node.nSelectType==2)
                    {
                        holder.SelectIcon.setVisibility(View.VISIBLE);
                        holder.SelectIcon.setBackgroundResource(R.mipmap.selectedflag_fly_jh);
                    }
                    else
                    {
                        holder.SelectIcon.setVisibility(View.INVISIBLE);
                    }
                    if (node.bitmap != null) {
                        holder.icon.setImageBitmap(node.bitmap);
                    }
                    else {
                        //  Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.background);
                        //holder.icon.setImageBitmap(bmp);
                    }
                }
                if (node.nType == MyNode.TYPE_Remote) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    if (node.nStatus == MyNode.Status_downloaded) {
                        holder.progressBar.setProgress(1000);
                        holder.caption.setVisibility(View.INVISIBLE);
                    } else if (node.nStatus == MyNode.Status_downloading) {
                        holder.progressBar.setProgress((int) node.nPre);
                        holder.caption.setTextColor(0xFF0000FF);
                        holder.caption.setVisibility(View.VISIBLE);
                        if (node.nPre < 10)
                            node.nPre = 10;
                        // String s1 = getString(R.string.downloading)+" "+node.nPre/10+"%";
                        String s1 = "Loadding"+" "+node.nPre/10+"%";
                        holder.caption.setText(s1);
                    } else {
                        holder.progressBar.setProgress(0);
                        holder.caption.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    holder.caption.setVisibility(View.INVISIBLE);
                }
            }
            /*
            if (JH_App.nBrowType == MyApp.Brow_Video){
                holder.video_bg.setVisibility(View.VISIBLE);
            }
            */
            return convertView;
        }

        class MyViewHolder {
            ProgressBar progressBar;
            TextView caption;
            ImageView icon;
            ImageView video_bg;
            String sUrl;
            ImageView    SelectIcon;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        JH_App.checkDeviceHasNavigationBar(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private boolean  bEdit=false;

    private  void F_DispSelectIcon()
    {
        if(bEdit)
        {
            btn_Select.setBackgroundResource(R.mipmap.jhd_selectpress);
        }
        else
        {
            btn_Select.setBackgroundResource(R.mipmap.jhd_selectoff);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btn_Back)
        {
            onBackPressed();
        }
        else if(v == btn_Select)
        {
            if(nodes!=null) {
                bEdit = !bEdit;
                F_DispSelectIcon();
                    for (MyNode node : nodes) {
                        if(bEdit) {
                            node.nSelectType = 1;
                        }
                        else
                        {
                            node.nSelectType = 0;
                        }
                    }


                myAdapter.notifyDataSetChanged();
            }
        }
        else if(v == btn_Delete)
        {
            if(nodes==null)
                return;
            int nSelected = 0;
            for (MyNode node : nodes) {
                if(node.nSelectType == 2)
                {
                    nSelected++;
                }
            }
            if(nSelected>0)
            {
                DeleteAert.setVisibility(View.VISIBLE);
            }

        }
        else if(v == alart_cancel)
        {
            DeleteAert.setVisibility(View.INVISIBLE);
            resetdeleteSelectFile();
        }
        else if(v == alart_ok)
        {
            deleteSelectFile();
            DeleteAert.setVisibility(View.INVISIBLE);
            resetdeleteSelectFile();

        }


    }

    private void deleteSelectFile()
    {

        if(nodes.size()==0)
            return;
        int size = nodes.size()-1;
        for(int ix=size;ix>=0;ix--)
        {
            MyNode node = nodes.get(ix);
            String sPath = node.sPath;
            if(node.nSelectType==2)
            {
                nodes.remove(ix);
                if (ix < mList.size())
                    mList.remove(ix);

                JH_App.DeleteImage(sPath);
                String strA = getFileName(sPath);
                String strs = getCacheDir() + strA + ".thb.png";
                if(!JH_App.bBrowPhoto)
                {
                    strs = getCacheDir() + "/" + strA + ".v_thb.png";
                }
                File ff = new File(strs);
                if (ff.exists()) {
                    ff.delete();
                }
                /*
                if(nodes.size() == 0) {
                    //TitleText_View.setText(R.string.NoFile);
                }
                else
                {
                    String sFormat=Grid_View.this.getString(R.string.Total_files);
                    String s  = String.format(sFormat,nodes.size());
                    TitleText_View.setText(s);
                }
                */
                myAdapter.notifyDataSetChanged();
            }


        }

        resetdeleteSelectFile();
    }

    private void resetdeleteSelectFile()
    {

        bEdit=false;
        F_DispSelectIcon();
        for(MyNode node :nodes)
        {
            node.nSelectType = 0;
        }
        myAdapter.notifyDataSetChanged();

    }


    @Subscriber(tag = "GetALLFiles_OK")
    private void GetALLFiles_OK(String str)
    {
        myAdapter = new MyAdapter(this, R.layout.my_grid_node, nodes);
        gridView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        resetdeleteSelectFile();


    }
}
