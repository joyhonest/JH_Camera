package com.joyhonest.jh_drone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.R;

import java.io.File;

public class drone_Select_Activity extends AppCompatActivity implements View.OnClickListener {

    Button  but_Back;
    Button  but_Filep;
    Button  but_Filev;
    TextView  Filep_Count;
    TextView  Filev_Count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_select);
        but_Back = findViewById(R.id.but_Back);
        but_Filep = findViewById(R.id.but_Filep);
        but_Filev = findViewById(R.id.but_Filev);
        Filep_Count = findViewById(R.id.Filep_Count);
        Filev_Count = findViewById(R.id.Filev_Count);

        but_Back.setOnClickListener(this);
        but_Filep.setOnClickListener(this);
        but_Filev.setOnClickListener(this);
    }


    private class _Init_Theard extends Thread {
        @Override
        public void run() {
            F_Init();
        }
    }

    int nTotalPhotos = 0;
    int nTotalVideos = 0;

    private  void F_Init()
    {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                WaitView.setVisibility(View.VISIBLE);
//            }
//        });

        nTotalPhotos = F_GetAllPhotoLocalCount();
        nTotalVideos = F_GetAllVideoLocalCount();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //WaitView.setVisibility(View.INVISIBLE);
                Filep_Count.setText(""+nTotalPhotos);
                Filev_Count.setText(""+nTotalVideos);
            }
        });
    }

    private int F_GetAllPhotoLocalCount() {
        String sPath = JH_App.sLocalPhoto;
        int nCount = 0;
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();
            String sExt;
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                sExt = getFileType(f.getPath());
                if(sExt.equalsIgnoreCase("jpg") ||
                        sExt.equalsIgnoreCase("png") ||
                        sExt.equalsIgnoreCase("bmp"))
                {
                    nCount++;
                }

            }
        }
        return nCount;

    }

    private String getFileType(String filename){
        if(filename==null)
            return null;
        //filename.toLowerCase();
        int pos = filename.lastIndexOf(".");
        if(pos == -1){
            return null;
        }
        return filename.substring(pos+1);
    }

    private int F_GetAllVideoLocalCount() {
        String sPath = JH_App.sLocalVideo;
        int nCount = 0;
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();
            String sExt;
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                sExt = getFileType(f.getPath());
                if(sExt.equalsIgnoreCase("mp4"))
                {
                    nCount++;
                }

            }
        }
        return nCount;
    }


    @Override
    protected void onResume() {
        super.onResume();

        _Init_Theard init = new _Init_Theard();
        init.start();

        JH_App.checkDeviceHasNavigationBar(this);
    }

    @Override
    public void onClick(View v) {
        if(v == but_Back)
        {
            onBackPressed();
        }
        else if(v == but_Filev)
        {
            if(nTotalVideos>0) {
                JH_App.bBrowPhoto = false;
                Intent mainIntent = new Intent(drone_Select_Activity.this, drone_grid_Activity.class);
                startActivity(mainIntent);
            }
        }
        else if(v == but_Filep)
        {
            if(nTotalPhotos>0) {
                JH_App.bBrowPhoto = true;
                Intent mainIntent = new Intent(drone_Select_Activity.this, drone_grid_Activity.class);
                startActivity(mainIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
