package com.joyhonest.jh_ui;


import android.Manifest;
import android.animation.ObjectAnimator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.joyhonest.wifination.MyThumb;
import com.joyhonest.wifination.jh_dowload_callback;
import com.joyhonest.wifination.wifination;


import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PlayActivity extends AppCompatActivity implements View.OnClickListener {


    //static Demo sInstance = null;


    //class Demo
    //{
    //    void doSomething()
    //    {
    //        System.out.print("dosth.");
    //    }
    //}

    /*


    int org = this.getWindowManager().getDefaultDisplay().getRotation();
                        if(MyApp.bGay)
                        {
                            if(Surface.ROTATION_270 == org)
                            {
                                MyApp.bRot = true;
                            }
                            else
                            {
                                MyApp.bRot = false;
                            }
                            if (MyApp.bRot)
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                            else
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
     */

    Select_Video_Photo_Fragment Select_Video_Photo_Fragment;
    main_fragment main_fragment;
    MyGrid_Fragment Grid_fragment;
    DispPhoto_Fragment dispPhoto_Fragment;
    //Start_Fragment                  start_fragment;
    DispVideo_Fragment dispVideo_fragment;
    Path_Fragment path_fragment;
    public boolean bDeleteing = false;

    private boolean bPhone_SNAP = false;

    VideoView videoView;
    MediaController mc;

    // private ImageView   dispImageView;


    public JH_App app;

    private TextView infp_TestView;

    private boolean bCancelDownLoadVideo = false;
    private boolean bCancelDownLoad = false;
    private boolean bCancelDownLoad_B = false;
    private Fragment mActiveFragment = null;
    FragmentManager mFragmentMan;// = getFragmentManager();


    public int nGetFileType = 0;
    private Bitmap bmpThmb = null;
    private String bmpThmb_fileName = null;


    // private SurfaceHolder surfaceHolder;
    //private SurfaceView surfaceView = null;


    private boolean bFirsetOpen = false;
    private HandlerThread thread1;
    private Handler openHandler;
    private Runnable openRunnable = new Runnable() {
        @Override
        public void run() {
            {
                //JH_App.F_OpenStream();
                bFirsetOpen = true;
                JH_App.F_OpenCamera(true);
                if (main_fragment != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            main_fragment.F_GetSYMA_X5UW();

                        }
                    });
                }

            }
        }
    };

    /*
        private   Handler sendCmdHandle = new Handler();
        private   Runnable  sendCmdRunnable = new Runnable() {
            @Override
            public void run() {
                //F_SentCmd();
                sendCmdHandle.postDelayed(this,20);
            }
        };
        */
    private RelativeLayout Fragment_Layout;

    //private JH_GLSurfaceView glSurfaceView;

    private ImageView DispImageView;


    private Handler RssiHander;
    private Runnable RssiRunable;
    private byte[] mData = null;
    private boolean bGoFly = false;
    private PermissionAsker mAsker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JH_App.nAppType = 1;
        JH_App.F_InitMusic();
        wifination.naSetRevBmp(true);

/*
        if(JH_App.F_GetWifiType()==wifination.IC_GPH264A)
        {
            bGoFly = true;
            Intent mainIntent = new Intent(PlayActivity.this, Fly_PlayActivity.class);
            startActivity(mainIntent);
            finish();
            return;
        }
*/

        bGoFly = false;
        wifination.appContext = getApplicationContext();
        wifination.naSetVrBackground(false);


        mAsker = new PermissionAsker(10, new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_play_jh);
                JH_App.F_CreatSyMaGoDir();
                F_Init();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayActivity.this, "The necessary permission denied, the application exit",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        if(Build.VERSION.SDK_INT>=33)
        {
            mAsker.askPermission(this, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES);
        }
        else
        {
            mAsker.askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAsker.onRequestPermissionsResult(grantResults);
    }

    private void F_Init() {
        MyControl.bFlyType = false;
        wifination.F_AdjBackGround(this, R.mipmap.loginbackground_jh);
        JH_App.checkDeviceHasNavigationBar(this);
        JH_App.F_Clear_not_videoFiles();
        //glSurfaceView = (JH_GLSurfaceView) findViewById(R.id.surfaceView_gl);
        DispImageView = findViewById(R.id.DispImageView);
        mFragmentMan = getSupportFragmentManager();//  getFragmentManager();// getSupportFragmentManager();
        Fragment_Layout = (RelativeLayout) findViewById(R.id.Fragment_Layout);
        RssiHander = new Handler();
        RssiRunable = new Runnable() {
            @Override
            public void run() {
                int nrssi = JH_App.F_GetWifiRssi();
                {
                    if (main_fragment != null) {
                        F_DispRssi(main_fragment.imageViewRssi, nrssi);
                    }
                    if (path_fragment != null) {
                        F_DispRssi(path_fragment.imageViewRssi, nrssi);
                    }
                }
                RssiHander.postDelayed(this, 1000);
            }
        };


        // surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        //surfaceHolder = surfaceView.getHolder();
        //surfaceHolder.addCallback(this);

        thread1 = new HandlerThread("MyHandlerThread");
        thread1.start(); //创建一个HandlerThread并启动它
        openHandler = new Handler(thread1.getLooper());

        F_InitFragment();

        //openHandler.postDelayed(initmusic,100);

        EventBus.getDefault().register(this);
        RssiHander.postDelayed(RssiRunable, 100);
    }


    private void F_DispRssi(ImageView imageView, int nRssi) {
        if (imageView == null)
            return;
        if (nRssi >= 4) {
            imageView.setBackgroundResource(R.mipmap.wifistrength_4_jh);
        } else if (nRssi == 3) {
            imageView.setBackgroundResource(R.mipmap.wifistrength_3_jh);
        } else if (nRssi == 2) {
            imageView.setBackgroundResource(R.mipmap.wifistrength_2_jh);
        } else if (nRssi == 1) {
            imageView.setBackgroundResource(R.mipmap.wifistrength_1_jh);
        } else {
            imageView.setBackgroundResource(R.mipmap.wifistrength_0_jh);
        }

    }

    @Subscriber(tag = "OnGetGP_Status")
    private void OnGetGP_Status(int n) {

        Log.e("GetData", "Key = " + n);
    }

    @Subscriber(tag ="ReviceBMP")
    private void ReviceBMP(Bitmap bmp)
    {
        //imageView4.setImageBitmap(bmp);
        if(bFirsetOpen)
        {
            if(bmp.getHeight()<=360)
            {
                wifination.naSetRecordWH(640 , 480);
            }
            bFirsetOpen = false;
        }
        else {
            DispImageView.setImageBitmap(bmp);
        }

    }

    boolean bFirst = false;

    private void F_InitFragment() {
        Select_Video_Photo_Fragment = new Select_Video_Photo_Fragment();
        main_fragment = new main_fragment();
        Grid_fragment = new MyGrid_Fragment();
        dispPhoto_Fragment = new DispPhoto_Fragment();
        dispVideo_fragment = new DispVideo_Fragment();
        path_fragment = new Path_Fragment();


        FragmentTransaction transaction = mFragmentMan.beginTransaction();
        transaction.add(R.id.Fragment_Layout, Select_Video_Photo_Fragment);
        transaction.add(R.id.Fragment_Layout, Grid_fragment);
        transaction.add(R.id.Fragment_Layout, dispPhoto_Fragment);
        transaction.add(R.id.Fragment_Layout, dispVideo_fragment);
        transaction.add(R.id.Fragment_Layout, path_fragment);
        transaction.add(R.id.Fragment_Layout, main_fragment);
        transaction.commit();
        mFragmentMan.executePendingTransactions();


        F_OpenCamera(true);

        bFirst = true;


        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transactionA = mFragmentMan.beginTransaction();
                hideFragments(transactionA);
                transactionA.commit();
                mFragmentMan.executePendingTransactions();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dispVideo_fragment.F_SetBackImg(R.mipmap.return_nor_1_jh);
                        F_SetView(main_fragment);
                    }
                }, 20);
            }
        }, 50);
        */

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bGoFly)
            return;
        JH_App.checkDeviceHasNavigationBar(this);
        if (bFirst) {
            bFirst = false;
            FragmentTransaction transactionA = mFragmentMan.beginTransaction();
            hideFragments(transactionA);
            transactionA.commit();
            dispVideo_fragment.F_SetBackImg(R.mipmap.return_nor_1_jh);
            F_SetView(main_fragment);
        }
    }

    private void F_SetView(final Fragment fragment) {

        JH_App.checkDeviceHasNavigationBar(this);

        if (fragment == null) {
            mActiveFragment = null;
            Fragment_Layout.setVisibility(View.INVISIBLE);
            FragmentTransaction transaction = mFragmentMan.beginTransaction();
            hideFragments(transaction);
            transaction.commit();
            return;
        }
        if (mActiveFragment == fragment)
            return;


        Fragment_Layout.setVisibility(View.VISIBLE);
        if (Grid_fragment != null && fragment == Grid_fragment) {
            if (JH_App.bBrowPhoto) {
                Grid_fragment.F_SetIcon(true);
            } else {
                Grid_fragment.F_SetIcon(false);
            }
        }

        if (mActiveFragment != main_fragment && fragment == main_fragment) {
//            if (glSurfaceView != null)
//                glSurfaceView.setVisibility(View.VISIBLE);

            if (DispImageView != null)
                DispImageView.setVisibility(View.VISIBLE);


        }

        if (fragment == dispVideo_fragment) {
//            if (glSurfaceView != null)
//                glSurfaceView.setVisibility(View.INVISIBLE);

            if (DispImageView != null)
                DispImageView.setVisibility(View.INVISIBLE);

        }


        if (fragment == main_fragment) {
            main_fragment.F_StartAdjRecord(true);
        } else {
            main_fragment.F_StartAdjRecord(false);
        }

        FragmentTransaction transaction = mFragmentMan.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
        mFragmentMan.executePendingTransactions();
        F_DispFramgent(fragment);


    }

    private void F_DispFramgent(Fragment fragment) {
        final int normal = 0;
        final int dn2up = 1;
        final int up2dn = 2;
        final int left2right = 3;
        final int right2left = 4;

        final int nDelay = 300;

        int type = normal;
        if (mActiveFragment == main_fragment) {
            if (fragment == Select_Video_Photo_Fragment)
                type = dn2up;
            if (fragment == path_fragment)
                type = right2left;
        }
        if (mActiveFragment == Select_Video_Photo_Fragment) {
            if (fragment == main_fragment) {
                type = up2dn;
            } else {
                type = dn2up;
            }
        }
        if (mActiveFragment == Grid_fragment) {
            if (fragment == Select_Video_Photo_Fragment) {
                type = up2dn;
            } else {
                type = dn2up;
            }
        }

        if (mActiveFragment == dispVideo_fragment) {
            if (fragment == Grid_fragment) {
                type = up2dn;
            } else {
                type = dn2up;
            }
        }

        if (mActiveFragment == dispPhoto_Fragment) {
            if (fragment == Grid_fragment) {
                type = up2dn;
            } else {
                type = dn2up;
            }
        }
        if (mActiveFragment == path_fragment) {
            type = left2right;
        }


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        int width = dm.widthPixels + 200;
        int height = dm.heightPixels + 200;


        if (type == dn2up) {
            if (mActiveFragment != null) {
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "Y", 0, 0 - height).setDuration(nDelay).start();
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "X", 0, 0).setDuration(1).start();
            }
            ObjectAnimator.ofFloat(fragment.getView(), "Y", height, 0).setDuration(nDelay).start();
            ObjectAnimator.ofFloat(fragment.getView(), "X", 0, 0).setDuration(1).start();
        } else if (type == up2dn) {
            if (mActiveFragment != null) {
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "Y", 0, height + 100).setDuration(nDelay).start();
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "X", 0, 0).setDuration(1).start();
            }
            ObjectAnimator.ofFloat(fragment.getView(), "Y", 0 - height, 0).setDuration(nDelay).start();
            ObjectAnimator.ofFloat(fragment.getView(), "X", 0, 0).setDuration(1).start();
        } else if (type == left2right) {
            if (mActiveFragment != null) {
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "X", 0, width).setDuration(nDelay).start();
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "Y", 0, 0).setDuration(1).start();
            }
            ObjectAnimator.ofFloat(fragment.getView(), "X", 0 - width - 100, 0).setDuration(nDelay).start();
            ObjectAnimator.ofFloat(fragment.getView(), "Y", 0, 0).setDuration(1).start();
        } else if (type == right2left) {
            if (mActiveFragment != null) {
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "X", 0, 0 - width).setDuration(nDelay).start();
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "Y", 0, 0).setDuration(1).start();
            }
            ObjectAnimator.ofFloat(fragment.getView(), "X", width, 0).setDuration(nDelay).start();
            ObjectAnimator.ofFloat(fragment.getView(), "Y", 0, 0).setDuration(1).start();
        } else {
            if (mActiveFragment != null) {
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "X", 0, 0).setDuration(1).start();
                ObjectAnimator.ofFloat(mActiveFragment.getView(), "Y", 0, 0).setDuration(1).start();
            }
            ObjectAnimator.ofFloat(fragment.getView(), "X", 0, 0).setDuration(1).start();
            ObjectAnimator.ofFloat(fragment.getView(), "Y", 0, 0).setDuration(1).start();

        }


        mActiveFragment = fragment;

        JH_App.bisPathMode = mActiveFragment == path_fragment;

    }

    private void hideFragments(FragmentTransaction ft) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        int width = dm.widthPixels + 100;
        //int height = dm.heightPixels+100;
        if (Select_Video_Photo_Fragment != null) {
            ObjectAnimator.ofFloat(Select_Video_Photo_Fragment.getView(), "Y", 0, 0).setDuration(1).start();
            ObjectAnimator.ofFloat(Select_Video_Photo_Fragment.getView(), "X", 0, -width).setDuration(1).start();
            ft.hide(Select_Video_Photo_Fragment);
        }

        if (main_fragment != null) {
            ObjectAnimator.ofFloat(main_fragment.getView(), "Y", 0, 0).setDuration(1).start();
            ObjectAnimator.ofFloat(main_fragment.getView(), "X", 0, 0).setDuration(1).start();
            ft.hide(main_fragment);
        }


        if (Grid_fragment != null) {
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "Y", 0, 0).setDuration(1).start();
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "X", 0, -width).setDuration(1).start();
            ft.hide(Grid_fragment);
        }
        if (dispPhoto_Fragment != null) {
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "Y", 0, 0).setDuration(1).start();
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "X", 0, -width).setDuration(1).start();
            ft.hide(dispPhoto_Fragment);
        }
        if (dispVideo_fragment != null) {
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "Y", 0, 0).setDuration(1).start();
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "X", 0, -width).setDuration(1).start();
            ft.hide(dispVideo_fragment);
        }
        if (path_fragment != null) {
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "Y", 0, 0).setDuration(1).start();
            ObjectAnimator.ofFloat(Grid_fragment.getView(), "X", 0, -width).setDuration(1).start();
            ft.hide(path_fragment);
        }
    }


    private void F_OpenCamera(boolean b) {
        if (b) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openHandler.removeCallbacksAndMessages(null);
                    openHandler.postDelayed(openRunnable, 25);
                }
            }, 30);
        } else {
            wifination.naStopRecord(wifination.TYPE_ONLY_PHONE);
            //wifination.naSetVideoSurface(null);
            wifination.naCancelDownload();
            wifination.naCancelGetThumb();
            wifination.naStop();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (bGoFly)
            return;
        if (main_fragment != null) {
            main_fragment.myControl.F_ReasetAll();
        }
        /*
        // sendCmdHandle.removeCallbacksAndMessages(null);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int status = tm.getCallState();
        if(status== TelephonyManager.CALL_STATE_IDLE) {
            Exit2Spalsh("");
        }
        */


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bGoFly)
            return;
        if (openHandler != null) {
            JH_App.F_OpenCamera(false);
//            wifination.naStop();
            wifination.release();
            EventBus.getDefault().unregister(this);

            openHandler.removeCallbacksAndMessages(null);
            //  sendCmdHandle.removeCallbacksAndMessages(null);
            RssiHander.removeCallbacksAndMessages(null);
            thread1.quit();
        }
        /*
        FragmentTransaction transaction = mFragmentMan.beginTransaction();
        transaction.remove(Select_Video_Photo_Fragment);
        transaction.remove(main_fragment);
        transaction.remove(Grid_fragment);
        transaction.remove(dispPhoto_Fragment);
        transaction.remove(dispVideo_fragment);
        transaction.remove(path_fragment);
        transaction.commit();
        */

    }

    /*
        private  void F_OpenStream()
        {

            int ictype = JH_App.F_GetWifiType();

            JH_App.nICType = ictype;
            //JH_App.nICType = wifination.IC_GPH264A;
            //JH_App.nICType = wifination.IC_GKA;
            //JH_App.nICType = wifination.IC_GPH264;
            //JH_App.nICType = wifination.IC_GPRTSP;
            //JH_App.nICType = wifination.IC_SN;
            //JH_App.nICType = wifination.IC_GPRTP;
            wifination.naSetIcType(JH_App.nICType);
            if(JH_App.bIsSyMa)
                wifination.naSetCustomer("sima");
            else
                wifination.naSetCustomer(" ");
            wifination.naSetVideoSurface(surfaceHolder.getSurface());
            String str = "";
            int re=-1;
            if(JH_App.nICType == wifination.IC_GK)
            {
                str = "rtsp://192.168.234.1/12";
                re = wifination.naInit(str);
            }
            if(JH_App.nICType == wifination.IC_GKA)
            {
                if(JH_App.b720P)
                {
                    str="1";
                }
                else
                {
                    str="2";
                }
                re = wifination.naInit(str);
            }
            if(JH_App.nICType == wifination.IC_SN)
            {
                wifination.naInit(str);
            }

            if(JH_App.nICType == wifination.IC_GP)
            {
                str = "http://192.168.25.1:8080/?action=stream";
                wifination.naInit(str);
            }

            if(JH_App.nICType == wifination.IC_GPRTSP)
            {
                str = "rtsp://192.168.26.1:8080/?action=stream";
                re = wifination.naInit(str);
                if(re==0)
                    wifination.naPlay();
            }

            if(JH_App.nICType == wifination.IC_GPH264)
            {
                str = "rtsp://192.168.27.1:8080/?action=stream";
                wifination.naInit(str);
            }

            if(JH_App.nICType == wifination.IC_GPRTP)
            {
                str = "192.168.28.1:19200";
                wifination.naInit(str);
            }
            if( JH_App.nICType == wifination.IC_GPRTPB)
            {
                str = "192.168.29.1:19200";
                wifination.naInit(str);
            }
            if(JH_App.nICType == wifination.IC_GPH264A)
            {
                str = "192.168.30.1:8080";
                wifination.naSetGPFps(20);
                wifination.naInit(str);
            }
            //sendCmdHandle.removeCallbacksAndMessages(null);
            //sendCmdHandle.post(sendCmdRunnable);
        }
    */
    @Override
    public void onClick(View view) {

    }


    @Subscriber(tag = "StartGame")
    private void F_Start(String s) {
        //spirit.Start(300);
    }

    ////////////// 回调函数


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
            main_fragment.F_DispMessage("snapshot");
        } else {
            JH_App.F_Save2ToGallery(sName, false);
        }
    }

    @Subscriber(tag = "GetWifiSendData")
    private void GetWifiSendData(byte[] data) {
        String ss = "";
        String str;
        for (byte da : data) {
            str = String.format("%02X", da);
            ss = ss + str + " ";
        }
        Log.e("data", ss);
    }

    private Bitmap bmp = null;// Bitmap.createBitmap(640,360,Bitmap.Config.ARGB_8888);

    @Subscriber(tag = "ReviceBMP")
    private void ReviceBMP(int i) {
        /*
        int w = (i&0xFFFF);
        int h = ((i&0xFFFF0000)>>16);
        if(bmp==null)
        {
            bmp=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        }
        final ByteBuffer buf = wifination.mDirectBuffer;
        buf.rewind();
        bmp.copyPixelsFromBuffer(buf);
        dispImageView.setImageBitmap(bmp);
        */
        //wifination.naSetContinue();

    }

    @Subscriber(tag = "key_Press")
    private void key_Press(Integer n) {
        main_fragment.F_DispInfo("Error:" + n);
    }

    @Subscriber(tag = "GetThumb")
    private void GetThumb(MyThumb thmb) {
        if (thmb.thumb != null) {
            bmpThmb = thmb.thumb;
            bmpThmb_fileName = thmb.sFilename;
        }
    }

    @Subscriber(tag = "DownloadFile")
    private void DownloadFile(jh_dowload_callback dowload) {
        //Log.v("abc",dowload.sFileName+"   "+dowload.nPercentage+"%");
        if (JH_App.bBrowSD && !JH_App.bBrowPhoto) {
            for (MyItemData data : JH_App.mGridList) {
                if (data.sSDPath.compareToIgnoreCase(dowload.sFileName) == 0) {
                    data.fPrecent = dowload.nPercentage;
                    Grid_fragment.F_UpdateLisetViewData();
                }
            }
        }

    }

    @Subscriber(tag = "GetFiles")
    private void GetFiles(String sfileName) {
        Log.e("GetFiles--", "GetFiles == " + sfileName);
        String[] temp = null;
        temp = sfileName.split("--");
        if (sfileName.startsWith("---")) {
            if (sfileName.startsWith("---End")) {
                if (nGetFileType == 0) {
                    nGetFileType = 1;
                } else {
                    if (nGetFileType == 1) {
                        Select_Video_Photo_Fragment.F_Update_number(JH_App.mSD_PhotosList.size(), JH_App.mSD_VideosList.size());
                    }
                }
            }
            return;
        }
        if (temp.length == 2) {
            MyFilesItem sd_PhotoFile = new MyFilesItem();
            sd_PhotoFile.sSDPath = temp[0];
            Integer it = new Integer(temp[1]);
            sd_PhotoFile.nSize = it.intValue();
            if (nGetFileType == 0) {

                JH_App.mSD_PhotosList.add(sd_PhotoFile);
            } else {
                JH_App.mSD_VideosList.add(sd_PhotoFile);
            }
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
        if ((nStatus & JH_App.SD_Recording) != 0) {
            //main_fragment.F_DispRcordIcon(true);
            JH_App.nSdStatus |= JH_App.SD_Ready;
            JH_App.nSdStatus |= JH_App.SD_Recording;
        } else {
            //main_fragment.F_DispRcordIcon(false);
            JH_App.nSdStatus &= ((JH_App.SD_Recording ^ 0xFFFF) & 0xFFFF);
        }

        if ((nStatus & JH_App.SD_SNAP) != 0) {
            JH_App.nSdStatus |= JH_App.SD_Ready;

            if ((JH_App.nSdStatus & JH_App.SD_SNAP) == 0) {
                if (!JH_App.bPhone_SNAP) {
                    //  [self menu_Photo_Click:nil];
                }
            }
            JH_App.nSdStatus |= JH_App.SD_SNAP;

        } else {
            JH_App.nSdStatus &= ((JH_App.SD_SNAP ^ 0xFFFF) & 0xFFFF);
        }

        if ((nStatus & JH_App.SD_Ready) != 0) {
            JH_App.nSdStatus |= JH_App.SD_Ready;
        } else {
            JH_App.nSdStatus &= ((JH_App.SD_Ready ^ 0xFFFF) & 0xFFFF);
            JH_App.nSdStatus &= ((JH_App.SD_SNAP ^ 0xFFFF) & 0xFFFF);
            JH_App.nSdStatus &= ((JH_App.SD_Recording ^ 0xFFFF) & 0xFFFF);
        }

        if ((JH_App.nSdStatus & JH_App.SD_Ready) == 0) {
            JH_App.nSdStatus &= ((JH_App.SD_SNAP ^ 0xFFFF) & 0xFFFF);
            JH_App.nSdStatus &= ((JH_App.SD_Recording ^ 0xFFFF) & 0xFFFF);
        }


        if ((nStatus & JH_App.LocalRecording) != 0) {
            JH_App.nSdStatus |= JH_App.LocalRecording;
        } else {
            JH_App.nSdStatus &= ((JH_App.LocalRecording ^ 0xFFFF) & 0xFFFF);
        }

/*
        if((JH_App.nSdStatus & JH_App.SD_Recording) !=0  && (backStatus & JH_App.SD_Recording) ==0) //SD已经开始录像
        {
           // NSLog(@"SD Record");
            if((JH_App.nSdStatus & JH_App.LocalRecording) !=0)    //本地已经开始录像
            {
                JH_App.bPhone_Video = false;
                return;
            }
            else
            {
                if(!JH_App.bPhone_Video)                    // 如果不是因为iphone 发出的命令来同步录像，就启动本地录像
                {
                    JH_App.bNeedStartsasyRecord = true;

                }
                JH_App.bPhone_Video = false;
            }
        }

        if((JH_App.nSdStatus & JH_App.SD_Recording) == 0  && (backStatus & JH_App.SD_Recording)!=0)    //SD 卡录像停止， 那么，也需要停止本地录像。
        {
            wifination.naStopRecord_All();
            main_fragment.F_DispRcordIcon(false);
        }
*/
        if ((JH_App.nSdStatus & JH_App.LocalRecording) != 0) {
            main_fragment.F_DispRcordIcon(true);
        } else {

            main_fragment.F_DispRcordIcon(false);
        }
        if ((JH_App.nSdStatus & JH_App.SD_Recording) != 0) {
            //[_Button_Video_SD setImage:self.Img_Recing forState:UIControlStateNormal];
        } else {
            //[_Button_Video_SD setImage:self.Img_No_Recing forState:UIControlStateNormal];
        }
        if ((JH_App.nSdStatus & JH_App.SD_Ready) != 0) {
            // [_Button_SD setImage:[UIImage imageNamed:@"sd"] forState:UIControlStateNormal];
        } else {
            //  [_Button_SD setImage:[UIImage imageNamed:@"no_sd"] forState:UIControlStateNormal];
        }

    }


    /////////// 获取文件列表

    private class SortComparator implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            String str1 = lhs;
            String str2 = rhs;
            str1 = JH_App.getFileName(str1);
            str2 = JH_App.getFileName(str2);
            return str2.compareTo(str1);
        }
    }

    public long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception e) {
                ;
            }

        }
        return size;
    }


    private void F_GetFilesNumber() {
        //wifination.naGetFiles()
        JH_App.mSD_PhotosList.clear();
        JH_App.mSD_VideosList.clear();

        JH_App.mLocal_PhotosList.clear();
        JH_App.mLocal_VideosList.clear();


        if (JH_App.bBrowSD) {
            MyThread_GetFileNumber thread_getFileNumber = new MyThread_GetFileNumber(this);
            thread_getFileNumber.start();
        } else {
            File f = new File(JH_App.sLocalPhoto);
            File[] files = f.listFiles();// 列出所有文件
            String fileName;
            for (File file : files) {
                if (file.exists() && !file.isDirectory()) {
                    fileName = file.getAbsolutePath();
                    String fileName1 = fileName.toLowerCase();

                    fileName = file.getAbsolutePath();
                    String sVedor = file.getParent();
                    sVedor = sVedor.substring(sVedor.lastIndexOf("/") + 1);
                    String slocal = "";

                    if (fileName1.endsWith(".jpg") || fileName1.endsWith(".png")) {
                        sVedor ="SYMA-PHOTO";
                        if(JH_App.isAndroidQ()) {
                            slocal = Environment.DIRECTORY_PICTURES + File.separator + sVedor;
                            String sfile = fileName.substring(fileName.lastIndexOf("/") + 1);
                            if (JH_App.F_CheckIsExit(slocal, sfile, true)) {
                                JH_App.mLocal_PhotosList.add(fileName);
                            } else {
                                file.delete();
                            }
                        }
                        else {
                            JH_App.mLocal_PhotosList.add(fileName);
                        }
                    }
                }
            }


            Collections.sort(JH_App.mLocal_PhotosList, new SortComparator());

            f = new File(JH_App.sLocalVideo);
            files = f.listFiles();// 列出所有文件
            //Bitmap bitmap = null;
            for (File file : files) {
                if (file.exists() && !file.isDirectory()) {
                    fileName = file.getAbsolutePath();
                    String fileName1 = fileName.toLowerCase();

                    String sVedor = file.getParent();
                    sVedor = sVedor.substring(sVedor.lastIndexOf("/") + 1);

                    if (fileName1.endsWith(".mp4")) {

                        long nsize = getFileSize(file);
                        //bitmap = JH_App.getVideoThumbnail(fileName);
                        //if(bitmap!=null)
                        if (nsize > 1200) {

                            if(JH_App.isAndroidQ()) {
                                sVedor ="SYMA-VIDEO";
                                String slocal = Environment.DIRECTORY_MOVIES + File.separator + sVedor;
                                String sfile = fileName.substring(fileName.lastIndexOf("/") + 1);
                                if (JH_App.F_CheckIsExit(slocal, sfile, false)) {
                                    JH_App.mLocal_VideosList.add(fileName);
                                } else {
                                    file.delete();
                                }
                            }
                            else {
                                JH_App.mLocal_VideosList.add(fileName);
                            }
                        } else {
                            file.delete();
                        }




                    } else {
                        if ((JH_App.nSdStatus & JH_App.LocalRecording) == 0)
                            file.delete();
                    }
                }
            }
            Collections.sort(JH_App.mLocal_VideosList, new SortComparator());
            Select_Video_Photo_Fragment.F_Update_number(JH_App.mLocal_PhotosList.size(), JH_App.mLocal_VideosList.size());
        }
    }

    ///////

    ///  Fragment  处理
/*
    @Subscriber(tag = "go_Main")
    private  void go_Main(String str)
    {
            F_OpenCamera(true);
            F_SetView(main_fragment);
    }
*/
/*
    @Subscriber(tag = "MyContronl_setSize")
    private  void MyContronl_setSize(MyControl.MyRockeViewA ctrol)
    {
         if(path_fragment!=null)
         {
             if(ctrol==main_fragment.myControl.RockeLeft)
                path_fragment.myControl.F_SetSize(ctrol);
         }
    }
*/

    @Subscriber(tag = "GotoPath")
    private void GotoPath(String str) {
        F_SetView(path_fragment);
        path_fragment.F_SttartPath();
    }


    @Subscriber(tag = "GotoSelect_Video_Photo")
    private void GotoSelect_Video_Photo(String str) {
        if (JH_App.bBrowSD) {
            Select_Video_Photo_Fragment.F_Update_number(-1, -1);
        } else {
            Select_Video_Photo_Fragment.F_Update_number(0, 0);
        }

        F_GetFilesNumber();

        F_SetView(Select_Video_Photo_Fragment);
    }

    @Subscriber(tag = "PlayBtnMusic")
    private void _OnPlayBtnMusic(Integer nn) {
        int n = nn & 0x0F;
        if (n != 0) {
            JH_App.F_PlayCenter();
        } else {
            JH_App.F_PlayAdj();
        }
        if (main_fragment != null) {
            JH_App.nAdjRota = main_fragment.myControl.F_GetRotateAdj();
            JH_App.nAdjForwardBack = main_fragment.myControl.F_GetForwardBackAdj();
            JH_App.nAdjLeftRight = main_fragment.myControl.F_GetLeftRightAdj();
            JH_App.F_ReadSaveSetting(true);
        }

        n = nn & 0xFF00;
        if (n == 0) {                   //副翼等调整
            n = nn & 0xF0;
            if (n == 0x70)   //y--       //前后-
            {
            }
            if (n == 0x80)   //y++       //前后+
            {
            }
            if (n == 0x90)   //x--       //左右-
            {
            }
            if (n == 0xA0)   //x++       //左右+
            {
            }
        } else if (n == 0x1000) {
            n = nn & 0xF0;
            if (n == 0x90)   //x--           //旋转-
            {
            }
            if (n == 0xA0)   //x++           //旋转+
            {
            }
        }
//        AdjHandler.postDelayed(AdjRunable,200);   //连续发送 200ms
    }


    private void F_ReadAllSdPhotos() {

        bCancelDownLoad = false;
        MyThread_DownLoad downLoad = new MyThread_DownLoad(this);
        downLoad.start();
    }

    private void F_ReadAllSdVideos() {

        bCancelDownLoad = false;
        MyThread_DownLoadThmb downLoad = new MyThread_DownLoadThmb(this);
        downLoad.start();
    }


    @Subscriber(tag = "Grid_Delete")
    private void Grid_Delete(String str) {

        MyThread_Delete delete = new MyThread_Delete(this);
        delete.start();


    }

    @Subscriber(tag = "GoTo_DispGrid")
    private void GoTo_DispGrid(String str) {

        Grid_fragment.F_Init();

        for (MyItemData itemData : JH_App.mGridList) {
            itemData.image = null;
            itemData.sSDPath = null;
            itemData.sPhonePath = null;
            itemData = null;
        }
        JH_App.mGridList.clear();
        Grid_fragment.F_UpdateLisetView();
        F_SetView(Grid_fragment);
        final PlayActivity activity = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (JH_App.bBrowSD) {
                    if (JH_App.bBrowPhoto) {
                        F_ReadAllSdPhotos();
                    } else   //SD Video
                    {
                        F_DownLoadVideoThread(true);
                        F_ReadAllSdVideos();
                    }
                } else {
                    bCancelDownLoad_B = false;
                    MyThread_GetAllLocalFile locall = new MyThread_GetAllLocalFile(activity);
                    locall.start();
                }
            }
        }, 500);


    }

    private void F_CancelDownLoad() {
        bCancelDownLoad = true;
        bCancelDownLoad_B = true;
        F_DownLoadVideoThread(false);
        wifination.naCancelDownload();
        wifination.naCancelGetThumb();
    }


    @Subscriber(tag = "GridItem_Click")
    private void GridItem_Click(Integer ixx) {
        final int ix = ixx;
        int inx = 0;
        int inxA = 0;
        int nSelect = 0;
        if (JH_App.bBrowSD) {
            if (JH_App.bBrowPhoto) {
                JH_App.mDispList.clear();
                //MyFilesItem item = JH_App.mSD_PhotosList.get(ix);
                inx = 0;
                inxA = 0;
                nSelect = 0;

                for (MyItemData data : JH_App.mGridList) {
                    if (data.nDownloadStatus == JH_App.DownLoaded) {
                        JH_App.mDispList.add(data.sPhonePath);
                        if (ix == inx) {
                            nSelect = inxA;
                        }
                        inxA++;
                    }
                    inx++;
                }
                dispPhoto_Fragment.UpdateData(nSelect);
                F_SetView(dispPhoto_Fragment);
            } else {
                MyItemData data = JH_App.mGridList.get(ix);
                if (data.nDownloadStatus == JH_App.DownLoaded) {
                    dispVideo_fragment.F_Play(data.sPhonePath);
                    F_SetView(dispVideo_fragment);

                } else if (data.nDownloadStatus == JH_App.DownLoading) {
                    ;
                } else if (data.nDownloadStatus == JH_App.DownLoaded_NO ||
                        data.nDownloadStatus == JH_App.DownLoadNormal)

                {
                    //data.nDownloadStatus = JH_App.DownLoading;
                    Integer x = ix;
                    data.nDownloadStatus = JH_App.DownLoadNeed;
                    JH_App.mDownloadList.add(x);
                    Grid_fragment.F_UpdateLisetViewData();
                        /*
                        MyThread_DownLoadVideo  thread = new MyThread_DownLoadVideo(this,ix);
                        thread.start();
                        */

                }

            }
        } else {
            if (JH_App.bBrowPhoto) {
                JH_App.mDispList.clear();
                //String  item = JH_App.mLocal_PhotosList.get(ix);
                inx = 0;
                inxA = 0;
                nSelect = 0;

                for (MyItemData data : JH_App.mGridList) {
                    //if(data.nDownloadStatus == JH_App.DownLoaded)
                    {
                        JH_App.mDispList.add(data.sPhonePath);
                        if (ix == inx) {
                            nSelect = inxA;
                        }
                        inxA++;
                    }
                    inx++;
                }
                dispPhoto_Fragment.UpdateData(nSelect);
                F_SetView(dispPhoto_Fragment);
            } else {


                MyItemData data = JH_App.mGridList.get(ix);
                dispVideo_fragment.F_Play(data.sPhonePath);
                F_SetView(dispVideo_fragment);


            }
        }

    }


    @Subscriber(tag = "Return_Back")
    private void Return_Back(Integer i) {

        if (mActiveFragment == Grid_fragment) {
            bCancelDownLoad = true;
            F_CancelDownLoad();


        }
        if (mActiveFragment == dispVideo_fragment) {
            dispVideo_fragment.F_Stop();
        }
        if (i == JH_App.select_Fragment_Value) {
            bCancelDownLoad = true;
            F_CancelDownLoad();
            F_SetView(Select_Video_Photo_Fragment);
        }
        if (i == JH_App.mainFragmeng_Value) {
            F_SetView(main_fragment);
        }
        if (i == JH_App.Grid_Fragment_Value) {
            F_SetView(Grid_fragment);
        }

    }

    @Subscriber(tag = "GoTo_Main")
    private void GoTo_Main(String str) {
        F_SetView(main_fragment);
    }


    @Subscriber(tag = "Exit2Spalsh")
    private void Exit2Spalsh(String str) {
        F_OpenCamera(false);
        if (main_fragment != null)
            main_fragment.F_Power(false);
        if (path_fragment != null)
            path_fragment.F_StopPaht();
        F_CancelDownLoad();
        finish();
    }


    private static class MyThread_DownLoadThmb extends Thread {

        WeakReference<PlayActivity> mThreadActivityRef;

        public MyThread_DownLoadThmb(PlayActivity activity) {
            mThreadActivityRef = new WeakReference<PlayActivity>(
                    activity);
        }

        @Override
        public void run() {
            super.run();
            String sFileName = "";
            String sPhonePath = "";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            Bitmap bitmap = null;
            JH_App.bDownLoading = true;
            for (MyFilesItem filesItem : JH_App.mSD_VideosList) {
                if (mThreadActivityRef.get().bCancelDownLoad)
                    break;

                Log.v("message", "Thmb ---" + filesItem.sSDPath);
                bitmap = null;
                MyItemData data = new MyItemData();
                data.sSDPath = filesItem.sSDPath;
                sFileName = JH_App.getFileName(filesItem.sSDPath);
                sPhonePath = JH_App.sRemoteVideo + "/" + sFileName;
                if (JH_App.F_CheckFileIsExist(sPhonePath, filesItem.nSize)) {
                    bitmap = JH_App.getVideoThumbnail(sPhonePath);
                    {
                        data.nDownloadStatus = JH_App.DownLoaded;
                        data.fPrecent = 100;
                        data.sPhonePath = sPhonePath;
                        data.nDuration = JH_App.F_GetVideoCountTime(sPhonePath);
                        if (bitmap != null)
                            data.image = bitmap;
                    }
                } else {
                    mThreadActivityRef.get().bmpThmb = null;
                    mThreadActivityRef.get().bmpThmb_fileName = null;
                    wifination.naGetThumb(sFileName);
                    try {
                        Thread.currentThread();
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mThreadActivityRef.get().bmpThmb == null) {
                        try {
                            Thread.currentThread();
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (mThreadActivityRef.get().bmpThmb != null)
                            data.image = mThreadActivityRef.get().bmpThmb;
                    } else {
                        data.image = mThreadActivityRef.get().bmpThmb;
                    }
                    if (mThreadActivityRef.get().bmpThmb == null) {
                        mThreadActivityRef.get().bmpThmb_fileName = null;
                        wifination.naGetThumb(sFileName);
                        try {
                            Thread.currentThread();
                            Thread.sleep(80);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (mThreadActivityRef.get().bmpThmb == null) {
                            try {
                                Thread.currentThread();
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (mThreadActivityRef.get().bmpThmb != null)
                                data.image = mThreadActivityRef.get().bmpThmb;
                        } else {
                            data.image = mThreadActivityRef.get().bmpThmb;
                        }
                    }
                }
                JH_App.mGridList.add(data);
                mThreadActivityRef.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mThreadActivityRef.get().Grid_fragment.F_UpdateLisetView();
                    }
                });
                if (mThreadActivityRef.get().bCancelDownLoad)
                    break;
            }
            JH_App.bDownLoading = false;
        }
    }


/*
    private static  Bitmap  F_AdjBitmp(Bitmap bitmap)
    {
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        int newWidth = 720/8;
        int newHeight = 1280/8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true);
        return newbm;
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            bitmap = retriever.getFrameAtTime(50);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if(bitmap!=null)
        {
            return F_AdjBitmp(bitmap);
        }
        return bitmap;
    }
*/


    private static class MyThread_Delete extends Thread {


        WeakReference<PlayActivity> mThreadActivityRef;

        public MyThread_Delete(PlayActivity activity) {
            mThreadActivityRef = new WeakReference<PlayActivity>(
                    activity);
        }

        @Override
        public void run() {
            super.run();
            int inx = 0;
            if (mThreadActivityRef.get().bDeleteing) {
                return;
            }
            mThreadActivityRef.get().bDeleteing = true;

            for (MyItemData data : JH_App.mGridList) {
                if (data.bSelected) {
                    if (JH_App.bBrowSD) {
                        wifination.naDeleteSDFile(data.sSDPath);
                        File file = new File(data.sPhonePath);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {

                        }


                    } else {
                        JH_App.DeleteImage(data.sPhonePath);
                    }
                    data.bNeedDelete = true;
                }
                inx++;
            }

            String sdFileName;
            String sLocalFileName;
            String sFileName;

            while (true) {
                inx = 0;
                for (MyItemData data : JH_App.mGridList) {
                    if (data.bNeedDelete) {
                        JH_App.mGridList.remove(inx);
                        if (JH_App.bBrowSD) {
                            if (JH_App.bBrowPhoto) {

                                if (inx < JH_App.mSD_PhotosList.size()) {
                                    JH_App.mSD_PhotosList.remove(inx);
                                }

                            } else {


                                if (inx < JH_App.mSD_VideosList.size()) {
                                    JH_App.mSD_VideosList.remove(inx);
                                }
                            }
                        } else {
                            if (JH_App.bBrowPhoto) {

                                if (inx < JH_App.mLocal_PhotosList.size()) {
                                    JH_App.mLocal_PhotosList.remove(inx);
                                }

                            } else {


                                if (inx < JH_App.mLocal_VideosList.size()) {
                                    JH_App.mLocal_VideosList.remove(inx);
                                }
                            }
                        }
                        mThreadActivityRef.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mThreadActivityRef.get().Grid_fragment.F_UpdateLisetViewData();
                                if (JH_App.bBrowSD)
                                    mThreadActivityRef.get().Select_Video_Photo_Fragment.F_Update_number(JH_App.mSD_PhotosList.size(), JH_App.mSD_VideosList.size());
                                else
                                    mThreadActivityRef.get().Select_Video_Photo_Fragment.F_Update_number(JH_App.mLocal_PhotosList.size(), JH_App.mLocal_VideosList.size());
                            }
                        });
                        break;
                    }
                    inx++;
                }
                try {
                    Thread.currentThread();
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (inx >= JH_App.mGridList.size())
                    break;
            }
            mThreadActivityRef.get().bDeleteing = false;
            mThreadActivityRef.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mThreadActivityRef.get().Grid_fragment.F_ResetSelect();
                    mThreadActivityRef.get().Grid_fragment.F_UpdateLisetViewData();

                }
            });
        }
    }


    private static class MyThread_GetAllLocalFile extends Thread {
        WeakReference<PlayActivity> mThreadActivityRef;

        public MyThread_GetAllLocalFile(PlayActivity activity) {
            mThreadActivityRef = new WeakReference<PlayActivity>(
                    activity);
        }

        @Override
        public void run() {
            super.run();

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (JH_App.bBrowPhoto) {
                for (String filename : JH_App.mLocal_PhotosList) {
                    MyItemData data = new MyItemData();
                    Bitmap bitmap = null;
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(filename);
                        options.inSampleSize = 5;
                        bitmap = BitmapFactory.decodeStream(fis, null, options);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null)
                        bitmap = JH_App.F_AdjBitmp(bitmap);
                    if (bitmap != null) {
                        data.image = bitmap;
                        data.sPhonePath = filename;
                        JH_App.mGridList.add(data);
                        mThreadActivityRef.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mThreadActivityRef.get().Grid_fragment.F_UpdateLisetView();
                            }
                        });
                    }

                    if (mThreadActivityRef.get().bCancelDownLoad_B)
                        break;
                }
            } else {
                for (String filename : JH_App.mLocal_VideosList) {
                    MyItemData data = new MyItemData();
                    Bitmap bitmap = null;
                    bitmap = JH_App.getVideoThumbnail(filename);
                    if (bitmap != null)
                        bitmap = JH_App.F_AdjBitmp(bitmap);
                    if (bitmap != null) {
                        data.image = bitmap;
                        data.sPhonePath = filename;
                        JH_App.mGridList.add(data);
                        mThreadActivityRef.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mThreadActivityRef.get().Grid_fragment.F_UpdateLisetView();
                            }
                        });
                    }
                    if (mThreadActivityRef.get().bCancelDownLoad_B)
                        break;
                }
            }
        }
    }

    private void F_DownLoadVideoThread(boolean bStart) {
        if (bStart) {
            bCancelDownLoadVideo = true;
            wifination.naCancelDownload();
            try {
                Thread.currentThread();
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JH_App.mDownloadList.clear();
            bCancelDownLoadVideo = false;
            MyThread_DownLoadVideo downLoadVideo = new MyThread_DownLoadVideo(this);
            downLoadVideo.start();
        } else {
            bCancelDownLoadVideo = true;
            wifination.naCancelDownload();
        }

    }


/*
    private  static int   F_GetVideoCountTime(String path)
    {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
        int du = -1;
        try {
            du = Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if(du!=-1)
            du/=1000;
        return du;
    }
    */

    private static class MyThread_DownLoadVideo extends Thread {
        WeakReference<PlayActivity> mThreadActivityRef;

        int inx;

        public MyThread_DownLoadVideo(PlayActivity activity) {
            mThreadActivityRef = new WeakReference<PlayActivity>(
                    activity);

        }

        @Override
        public void run() {
            super.run();
            while (!mThreadActivityRef.get().bCancelDownLoadVideo) {
                if (JH_App.mDownloadList.size() > 0) {
                    Integer ix = JH_App.mDownloadList.get(0);
                    inx = ix.intValue();

                    MyItemData data = null;
                    if (inx >= JH_App.mGridList.size()) {
                        JH_App.mDownloadList.remove(0);
                        continue;
                    }

                    data = JH_App.mGridList.get(inx);
                    data.nDownloadStatus = JH_App.DownLoading;
                    Log.v("message", "Download ---" + data.sSDPath);
                    String sFileName = JH_App.getFileName(data.sSDPath);
                    String sPhonePath = JH_App.sRemoteVideo + "/" + sFileName;


                    MyFilesItem filesItem = null;
                    if (inx >= JH_App.mSD_VideosList.size()) {
                        JH_App.mDownloadList.remove(0);
                        continue;
                    }
                    filesItem = JH_App.mSD_VideosList.get(inx);

                    Bitmap bitmap = null;
                    if (JH_App.F_CheckFileIsExist(sPhonePath, filesItem.nSize)) {
                        bitmap = JH_App.getVideoThumbnail(sPhonePath);
                        data.nDownloadStatus = JH_App.DownLoaded;
                        data.fPrecent = 100;
                        data.sPhonePath = sPhonePath;
                        int du = JH_App.F_GetVideoCountTime(sPhonePath);
                        data.nDuration = du;
                        if (bitmap != null)
                            data.image = bitmap;
                        continue;
                    }

                    wifination.naDownloadFile(data.sSDPath, sPhonePath);

                    bitmap = JH_App.getVideoThumbnail(sPhonePath);
                    //if (bitmap != null)
                    {
                        data.nDownloadStatus = JH_App.DownLoaded;
                        data.fPrecent = 100;

                        data.sPhonePath = sPhonePath;
                        int du = JH_App.F_GetVideoCountTime(sPhonePath);
                        data.nDuration = du;
                        if (bitmap != null)
                            data.image = bitmap;
                    }


                    mThreadActivityRef.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mThreadActivityRef.get().Grid_fragment.F_UpdateLisetView();
                        }
                    });
                    JH_App.mDownloadList.remove(0);
                }
                try {
                    Thread.currentThread();
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private static class MyThread_DownLoad extends Thread {

        WeakReference<PlayActivity> mThreadActivityRef;

        public MyThread_DownLoad(PlayActivity activity) {
            mThreadActivityRef = new WeakReference<PlayActivity>(
                    activity);
        }

        @Override
        public void run() {
            super.run();
            if (mThreadActivityRef == null)
                return;
            if (mThreadActivityRef.get() != null) {

                String sFileName = "";
                String sPhonePath = "";
                BitmapFactory.Options options = new BitmapFactory.Options();
                List<MyFilesItem> list = JH_App.mSD_PhotosList;
                String sLocalDir = JH_App.sRemotePhoto;
                if (!JH_App.bBrowPhoto) {
                    list = JH_App.mSD_VideosList;
                    sLocalDir = JH_App.sRemoteVideo;
                }

                for (MyFilesItem filesItem : list) {
                    if (mThreadActivityRef.get().bCancelDownLoad)
                        break;
                    Log.v("message", "Download ---" + filesItem.sSDPath);
                    MyItemData data = new MyItemData();
                    data.sSDPath = filesItem.sSDPath;
                    sFileName = JH_App.getFileName(filesItem.sSDPath);
                    sPhonePath = sLocalDir + "/" + sFileName;
                    if (JH_App.F_CheckFileIsExist(sPhonePath, filesItem.nSize)) {
                        Bitmap bitmap = null;
                        try {
                            if (JH_App.bBrowPhoto) {
                                FileInputStream fis = new FileInputStream(sPhonePath);
                                options.inSampleSize = 5;
                                bitmap = BitmapFactory.decodeStream(fis, null, options);
                                if (bitmap != null)
                                    bitmap = JH_App.F_AdjBitmp(bitmap);
                            } else {
                                bitmap = JH_App.getVideoThumbnail(sPhonePath);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                            data.nDownloadStatus = JH_App.DownLoaded;
                            data.fPrecent = 100;
                            data.image = bitmap;
                            data.sPhonePath = sPhonePath;
                        }
                    } else {
                        wifination.naDownloadFile(filesItem.sSDPath, sPhonePath);
                        if (JH_App.F_CheckFileIsExist(sPhonePath, filesItem.nSize)) {
                            Bitmap bitmap = null;
                            try {
                                if (JH_App.bBrowPhoto) {
                                    FileInputStream fis = new FileInputStream(sPhonePath);
                                    options.inSampleSize = 5;
                                    bitmap = BitmapFactory.decodeStream(fis, null, options);
                                    fis = null;
                                } else {
                                    bitmap = JH_App.getVideoThumbnail(sPhonePath);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                data.nDownloadStatus = JH_App.DownLoaded;
                                data.fPrecent = 100;
                                data.image = bitmap;
                                data.sPhonePath = sPhonePath;
                            }
                        } else {
                            File file = new File(sPhonePath);
                            if (file.exists() && !file.isDirectory()) {
                                file.delete();
                            }
                        }
                    }
                    JH_App.mGridList.add(data);
                    mThreadActivityRef.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mThreadActivityRef.get().Grid_fragment.F_UpdateLisetView();
                        }
                    });
                    if (mThreadActivityRef.get().bCancelDownLoad)
                        break;
                }

            }
        }
    }


    private static class MyThread_GetFileNumber extends Thread {

        WeakReference<PlayActivity> mThreadActivityRef;

        public MyThread_GetFileNumber(PlayActivity activity) {
            mThreadActivityRef = new WeakReference<PlayActivity>(
                    activity);
        }

        @Override
        public void run() {
            super.run();
            if (mThreadActivityRef == null)
                return;
            if (mThreadActivityRef.get() != null) {
                mThreadActivityRef.get().nGetFileType = 0;
                wifination.naGetFiles(0);
                try {
                    Thread.currentThread();
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wifination.naGetFiles(1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (mActiveFragment == path_fragment) {
            path_fragment.F_StopPaht();
            F_SetView(main_fragment);
            return;
        }
        if (mActiveFragment == dispPhoto_Fragment) {
            F_SetView(Grid_fragment);
            return;

        }
        if (mActiveFragment == dispVideo_fragment) {
            dispVideo_fragment.F_Stop();
            F_SetView(Grid_fragment);
            return;
        }

        if (mActiveFragment == Grid_fragment) {

            bCancelDownLoad = true;
            F_CancelDownLoad();

            F_SetView(Select_Video_Photo_Fragment);
            return;

        }

        if (mActiveFragment == Select_Video_Photo_Fragment) {
            F_SetView(main_fragment);
            return;

        }

        if (mActiveFragment == main_fragment) {

            main_fragment.F_Power(false);
            Exit2Spalsh("");
            return;

        }
        //  if(mActiveFragment == start_fragment)
        //  {
        //      finish();
        //  }
    }


}
