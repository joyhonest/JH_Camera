package com.joyhonest.Ultradrone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.R;

public class browActivity extends AppCompatActivity  implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brow2);
        findViewById(R.id.but_return).setOnClickListener(this);
        findViewById(R.id.but_photo).setOnClickListener(this);
        findViewById(R.id.but_video).setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.but_photo).setClickable(true);
        findViewById(R.id.but_video).setClickable(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.but_return)
        {
            onBackPressed();
        }
        else if(id == R.id.but_photo)
        {

            findViewById(R.id.but_photo).setClickable(false);
            JH_App.bBrowPhoto = true;
            Intent mainIntent = new Intent(this, ulgridActivity.class);
            startActivity(mainIntent);
            //overridePendingTransition(0, 0);
        }
        else if(id == R.id.but_video)
        {
            JH_App.bBrowPhoto = false;
            findViewById(R.id.but_video).setClickable(false);
            Intent mainIntent = new Intent(this, ulgridActivity.class);
            startActivity(mainIntent);
            //overridePendingTransition(0, 0);

        }

    }
}