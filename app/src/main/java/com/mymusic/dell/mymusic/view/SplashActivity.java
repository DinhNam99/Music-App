package com.mymusic.dell.mymusic.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.mymusic.dell.mymusic.MainActivity;
import com.mymusic.dell.mymusic.R;

public class SplashActivity extends AppCompatActivity {

    Thread mThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash);
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }

            }
        };
        mThread.start();
    }
}
