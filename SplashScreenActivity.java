package com.playgame.ilana.easymap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.playgame.ilana.easymap.UserRegistrationAndLogin.LogInActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        },1000);

    }
}
