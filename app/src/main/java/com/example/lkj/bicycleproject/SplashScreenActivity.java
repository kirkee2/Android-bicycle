package com.example.lkj.bicycleproject;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView img;
    private Animation animation;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        img = (ImageView) findViewById(R.id.imageView);


        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation2);
        img.setAnimation(animation);




        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        /*
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };

        handler.sendEmptyMessageDelayed(0, 3000);
        */
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
