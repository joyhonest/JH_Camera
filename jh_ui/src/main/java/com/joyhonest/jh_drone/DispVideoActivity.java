package com.joyhonest.jh_drone;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.R;

public class DispVideoActivity extends AppCompatActivity {

    VideoView  Brow_videoView;
    MediaController mc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_video);
        Brow_videoView = (VideoView)findViewById(R.id.Brow_videoView);
        //Brow_videoView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams=
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        Brow_videoView.setLayoutParams(layoutParams);
        mc = new MediaController(this);
        Brow_videoView.setMediaController(mc);

        Brow_videoView.setVideoPath(JH_App.sVideoPath);
        Brow_videoView.start();
        Brow_videoView.requestFocus();
        mc.hide();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(runnable,100);


        findViewById(R.id.btn_ExitPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Brow_videoView.stopPlayback();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Brow_videoView.stopPlayback();
        handler.removeCallbacksAndMessages(null);

    }

    private  void HideBar()
    {
        JH_App.checkDeviceHasNavigationBar(this);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!mc.isShowing())
                HideBar();
            handler.postDelayed(this, 800);
        }
    };

}
