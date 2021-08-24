package com.joyhonest.jh_drone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.R;

import java.util.List;

public class DispPhotoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{


    private Button btn_back;
    private ViewPager photo_vp;
    private TextView index_view;

    private List<String> nodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JH_App.checkDeviceHasNavigationBar(this);
        setContentView(R.layout.activity_disp_photo);
        btn_back = findViewById(R.id.btn_back);
        photo_vp = findViewById(R.id.photo_vp);
        index_view =findViewById(R.id.index_view);
        index_view.setText((JH_App.dispListInx+1)+"/"+JH_App.dispList.size());
        nodes = JH_App.dispList;

        photo_vp.setAdapter(adapter);

        photo_vp.addOnPageChangeListener(this);
        photo_vp.setCurrentItem(JH_App.dispListInx);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //JH_App.PlayBtnVoice();
                onBackPressed();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        JH_App.checkDeviceHasNavigationBar(this);
    }



    @Override

    public void onPageScrollStateChanged(int arg0) {


    }


    @Override

    public void onPageScrolled(int arg0, float arg1, int arg2) {


    }


    @Override

    public void onPageSelected(int arg0) {
        //fileInx_label.setText("" + (arg0 + 1) + "/" + nodes.size());
        index_view.setText((arg0+1)+"/"+JH_App.dispList.size());
    }

    private PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
//                            return imagePathArray.size();
            return nodes.size();
            //return 4;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        /**
         * 销毁当前page的相隔2个及2个以上的item时调用
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object o) {
            container.removeView((View) o);
        }

        //设置ViewPager指定位置要显示的view
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final String strPath = nodes.get(position);

            MyImageView im = new MyImageView(DispPhotoActivity.this);
            im.bCanScal = false;
            im.setMaxZoom(3.5f);
            im.setScaleType(ImageView.ScaleType.FIT_XY);
            im.setImageDrawable(new BitmapDrawable(getResources(), LoadBitmap(strPath)));
            container.addView(im);
            return im;
        }


    };

    private Bitmap LoadBitmap(String sPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(sPath, options); //此时返回bm为空
        options.inJustDecodeBounds = false;
        //计算缩放比
        int be = (int) (options.outHeight / (float) 720);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(sPath, options);
        return bitmap;
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//
//        Context contextA;
//        if(MyApp.nLanguage == MyApp.JPNESE) {
//            contextA = MyContextWrapper.wrap(newBase, "ja");
//        }
//        else if(MyApp.nLanguage == MyApp.SPAIN)
//        {
//            contextA = MyContextWrapper.wrap(newBase, "es");
//        }
//        else if(MyApp.nLanguage == MyApp.FRANCE)
//        {
//            contextA = MyContextWrapper.wrap(newBase, "fr");
//        }
//        else if(MyApp.nLanguage == MyApp.DANISH)
//        {
//            contextA = MyContextWrapper.wrap(newBase, "da");
//        }
//        else
//        {
//            contextA = MyContextWrapper.wrap(newBase, "en");
//        }
//
//        super.attachBaseContext(contextA);
//    }

}
