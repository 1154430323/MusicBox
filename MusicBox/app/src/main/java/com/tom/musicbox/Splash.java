package com.tom.musicbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by tom on 2016/4/23.
 */
public class Splash extends Activity{

    private final int SPLASH_DISPLAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent mainIntent = new Intent(Splash.this, LoginActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();

            }

        }, SPLASH_DISPLAY_TIME);




    }
}
