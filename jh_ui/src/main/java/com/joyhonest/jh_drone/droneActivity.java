package com.joyhonest.jh_drone;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.PermissionAsker;
import com.joyhonest.jh_ui.R;
import com.joyhonest.wifination.wifination;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class droneActivity extends AppCompatActivity implements View.OnClickListener {

    private PermissionAsker mAsker;

    private Button  btn_photo;
    private Button  btn_video;
    private Button  btn_brow;
    private Button  btn_wifi;
    private Button  btn_back;
    private TextView RectimeView;
    private ImageView DispImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JH_App.bIsSyMa=false;
        setContentView(R.layout.activity_drone);
        wifination.naSetRevBmp(true);
        wifination.appContext = getApplicationContext();
        wifination.naSetVrBackground(false);
        JH_App.F_InitMusic();

        thread1 = new HandlerThread("MyHandlerThread_FPV");
        thread1.start(); //创建一个HandlerThread并启动它
        openHandler = new Handler(thread1.getLooper());

        btn_wifi = findViewById(R.id.btn_Wifi);

        btn_photo = (Button)findViewById(R.id.btn_Photo);
        btn_video = (Button)findViewById(R.id.btn_Video);
        btn_brow = (Button)findViewById(R.id.btn_Brow);
        btn_wifi = (Button)findViewById(R.id.btn_Wifi);
        btn_back = (Button)findViewById(R.id.btn_Back);
        RectimeView = findViewById(R.id.RectimeView);

        btn_photo.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_brow.setOnClickListener(this);
        btn_wifi.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        DispImageView = findViewById(R.id.DispImageView);

        mAsker = new PermissionAsker(10, new Runnable() {
            @Override
            public void run() {
                F_Init();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(droneActivity.this, "The necessary permission denied, the application exit",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }).askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        RssiHander.post(RssiRunable);

        EventBus.getDefault().register(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAsker.onRequestPermissionsResult(grantResults);
    }

    private HandlerThread thread1;
    private Handler openHandler;
    private Runnable openRunnable = new Runnable() {
        @Override
        public void run() {
            {

                JH_App.bIsRevBmp = false;
                //JH_App.F_OpenStream();
                JH_App.F_OpenCamera(true);
            }
        }
    };


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
                btn_wifi.setBackgroundResource(R.mipmap.jhd_wifi0);
            }
            else if(nrssi==1)
            {
                btn_wifi.setBackgroundResource(R.mipmap.jhd_wifi1);
            }
            else if(nrssi==2)
            {
                btn_wifi.setBackgroundResource(R.mipmap.jhd_wifi2);
            }
            else if(nrssi==3)
            {
                btn_wifi.setBackgroundResource(R.mipmap.jhd_wifi3);
            }
            else
            {
                btn_wifi.setBackgroundResource(R.mipmap.jhd_wifi4);
            }
            RssiHander.postDelayed(this, 1000);

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        JH_App.checkDeviceHasNavigationBar(this);
    }


    private void F_Init() {
        JH_App.F_CreateLocalDroneDefalutDir();
        JH_App.F_Clear_not_videoFiles();
        openHandler.postDelayed(openRunnable,100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        wifination.naStopRecord_All();
//        wifination.naStop();
        JH_App.F_OpenCamera(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        wifination.naStopRecord_All();
//        wifination.naStop();
        JH_App.F_OpenCamera(false);
        RssiHander.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        thread1.quit();
        openHandler.removeCallbacksAndMessages(null);


    }

    private  void TackPhoto()
    {
        if(!JH_App.bIsRevBmp)
            return;

        final String str = JH_App.F_GetSaveName(true);
//        flashView.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                flashView.setVisibility(View.GONE);
//            }
//        }, 120);
        wifination.naSnapPhoto(str, wifination.TYPE_ONLY_PHONE);
        JH_App.F_PlayPhoto();

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
    public void onClick(View v) {
        if(v == btn_back)
        {
            onBackPressed();
        }
        else if(v == btn_brow)
        {
            Intent mainIntent = new Intent(droneActivity.this, drone_Select_Activity.class);
            startActivity(mainIntent);
        }
        else if(v == btn_photo)
        {
                TackPhoto();
        }
        else if(v == btn_video)
        {
            StartOrStopRecord();
        }


    }


    private Handler  DispRecTimeHander = new Handler();
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
        } else {
            JH_App.F_Save2ToGallery(sName, false);
        }
    }

    @Subscriber(tag = "ReviceBMP")
    private  void ReviceBMP(Bitmap bmp)
    {
        JH_App.bIsRevBmp = true;
        DispImageView.setImageBitmap(bmp);
    }


}
