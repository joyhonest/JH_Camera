package com.joyhonest.Ultradrone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.PermissionAsker;
import com.joyhonest.jh_ui.R;
import com.joyhonest.wifination.wifination;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;

public class ultradrone_acitvity extends AppCompatActivity  implements View.OnClickListener {



    private ImageView  DispImageView;

    private PermissionAsker mAsker;


    private Button  but_vr;
    private Button  but_snap;
    private Button  but_record;
    private Button  but_brow;
    private Button  but_wifi;
    private Button  but_return;

    private TextView RectimeView;

    private  boolean  bVR = false;

    private androidx.constraintlayout.widget.ConstraintLayout tool_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultradrone_acitvity);
        tool_bar = findViewById(R.id.tool_bar);


        but_vr = findViewById(R.id.but_vr);
        but_snap = findViewById(R.id.but_snap);
        but_record = findViewById(R.id.but_record);
        but_brow = findViewById(R.id.but_brow);
        but_wifi = findViewById(R.id.but_signe);
        but_return = findViewById(R.id.but_return);
        RectimeView = findViewById(R.id.RectimeView);

        but_vr.setOnClickListener(this);
        but_snap.setOnClickListener(this);
        but_record.setOnClickListener(this);
        but_brow.setOnClickListener(this);
        but_return.setOnClickListener(this);


        DispImageView = findViewById(R.id.DispImageView);
        DispImageView.setOnClickListener(this);
        JH_App.F_InitMusic();


        //if(!JH_App.isAndroidQ())
        if(Build.VERSION.SDK_INT>=33)
        {
            mAsker = new PermissionAsker(10, new Runnable() {
                @Override
                public void run() {
                    wifination.naSetRevBmp(true);
                    bVR = false;
                    JH_App.F_CreateLocalDir("Ultradrone","UltradroneV",null,null);
                    wifination.naSet3D(bVR);
                    JH_App.nSdStatus = 0;
                    JH_App.bIsRevBmp = false;
                    wifination.naInit("");

                }
            }, new Runnable() {
                @Override
                public void run() {
                    F_DispAlert();

                }
            }).askPermission(this, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES);
        }
        else
        {
            mAsker = new PermissionAsker(10, new Runnable() {
                @Override
                public void run() {
                    wifination.naSetRevBmp(true);
                    bVR = false;
                    JH_App.F_CreateLocalDir("Ultradrone","UltradroneV",null,null);
                    wifination.naSet3D(bVR);
                    JH_App.nSdStatus = 0;
                    JH_App.bIsRevBmp = false;
                        wifination.naInit("");

                }
            }, new Runnable() {
                @Override
                public void run() {
                    F_DispAlert();

                }
            }).askPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
//        else
//        {
//            JH_App.F_CreateLocalDir("UltradroneP","UltradroneV",null,null);
//            bVR = false;
//            JH_App.nSdStatus = 0;
//            JH_App.bIsRevBmp = false;
//            wifination.naSetRevBmp(true);
//            wifination.naSet3D(bVR);
//            wifination.naInit("");
//
//        }

        RssiHander.post(RssiRunable);


        EventBus.getDefault().register(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAsker.onRequestPermissionsResult(grantResults);
    }

    private Handler DispRecTimeHander = new Handler();
    private Runnable DispRecTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if((JH_App.nSdStatus & JH_App.LocalRecording) !=0)
            {
                int rec = wifination.naGetRecordTime()/1000;
                int nMin = rec/60;
                int nSec = rec %60;
                String str = String.format("%02d:%02d",nMin,nSec);
                RectimeView.setText(str);
                DispRecTimeHander.postDelayed(this,250);
            }
        }
    };


    private void F_DispAlert() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Waring")
                .setMessage("The necessary permission denied, the application exit")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RssiHander.removeCallbacksAndMessages(null);
        DispRecTimeHander.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag ="ReviceBMP")
    private void ReviceBMP(Bitmap bmp)
    {
        JH_App.bIsRevBmp = true;
        DispImageView.setImageBitmap(bmp);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.but_vr) {
            JH_App.F_PlayButton();
            if(JH_App.bIsRevBmp)
            {
                bVR = !bVR;
                wifination.naSet3D(bVR);
                if(bVR)
                {
                    tool_bar.setVisibility(View.INVISIBLE);
                }
            }

        } else if (id == R.id.but_snap) {
            JH_App.F_PlayPhoto();
            final String str = JH_App.F_GetSaveName(true);
            wifination.naSnapPhoto(str, wifination.TYPE_ONLY_PHONE);
        }
        else if (id == R.id.but_record) {
            JH_App.F_PlayButton();
            StartOrStopRecord();

        }
        else if (id == R.id.but_brow) {
            JH_App.F_PlayButton();
            Intent mainIntent = new Intent(this, browActivity.class);
            startActivity(mainIntent);
            overridePendingTransition(0, 0);

        }
        else if (id == R.id.but_return) {
            JH_App.F_PlayButton();
            onBackPressed();
        }
        else if(id == R.id.DispImageView)
        {
            if(tool_bar.getVisibility()!=View.VISIBLE)
            {
                tool_bar.setVisibility(View.VISIBLE);
            }
            else
            {
                tool_bar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private  void StartOrStopRecord()
    {
        if(JH_App.bIsRevBmp)
        {
            if((JH_App.nSdStatus & JH_App.LocalRecording) ==0)
            {
                String str = JH_App.F_GetSaveName(false);
                wifination.naStartRecord(str, wifination.TYPE_ONLY_PHONE);
            }
            else
            {
                wifination.naStopRecord_All();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        wifination.naStopRecord_All();
        wifination.naStop();
    }


    @Subscriber(tag = "SDStatus_Changed")
    private void _SDStatus_Changed(Integer nStatus) {
        int backStatus = JH_App.nSdStatus;
        if ((nStatus & 0x01) != 0) {
            JH_App.nSdStatus |= JH_App.Status_Connected;
        } else {
            JH_App.nSdStatus &= ((JH_App.Status_Connected ^ 0xFFFF) & 0xFFFF);
        }

        if ((nStatus & JH_App.LocalRecording) != 0)
        {

            if((JH_App.nSdStatus & JH_App.LocalRecording) == 0)
            {
                JH_App.nSdStatus |= JH_App.LocalRecording;   //Recording . . .
                DispRecTimeView(true);
            }
        } else {
            JH_App.nSdStatus &= ((JH_App.LocalRecording ^ 0xFFFF) & 0xFFFF);   //StopRecording ....
            DispRecTimeView(false);
        }

    }

    private void DispRecTimeView(boolean b)
    {
        if(b)
        {
            RectimeView.setText("00:00");
            RectimeView.setVisibility(View.VISIBLE);
            DispRecTimeHander.removeCallbacksAndMessages(null);
            DispRecTimeHander.post(DispRecTimeRunnable);

        }
        else
        {
            DispRecTimeHander.removeCallbacksAndMessages(null);
            RectimeView.setVisibility(View.INVISIBLE);
            RectimeView.setText("00:00");
        }
    }

    Handler RssiHander = new Handler();
    Runnable RssiRunable = new Runnable() {
        @Override
        public void run() {
            int nrssi = JH_App.F_GetWifiRssi();
            if(nrssi<0)
                nrssi = 0;
            if(nrssi>4)
                nrssi = 4;

            if(nrssi==0)
            {
                but_wifi.setBackgroundResource(R.mipmap.wifi00_icon_jh01);
            }
            else if(nrssi==1)
            {
                but_wifi.setBackgroundResource(R.mipmap.wifi02_icon_jh01);
            }
            else if(nrssi==2)
            {
                but_wifi.setBackgroundResource(R.mipmap.wifi02_icon_jh01);
            }
            else if(nrssi==3)
            {
                but_wifi.setBackgroundResource(R.mipmap.wifi03_icon_jh01);
            }
            else
            {
                but_wifi.setBackgroundResource(R.mipmap.wifi04_icon_jh01);
            }
            RssiHander.postDelayed(this, 1000);

        }
    };


    @Subscriber(tag = "SavePhotoOK")
    private void SavePhotoOK(String Sn) {
        if (Sn.length() < 5) {
            return;
        }
        String sType = Sn.substring(0, 2);
        String sName = Sn.substring(2, Sn.length());
        int nPhoto = Integer.parseInt(sType);
        if (nPhoto == 0) {
            JH_App.F_Save2ToGallery(sName, true);
        }
        else {
            JH_App.F_Save2ToGallery(sName, false);
        }
    }
}