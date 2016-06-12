package com.tom.musicbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by tom on 2016/4/23.
 */
public class Splash extends Activity{

    private final int SPLASH_DISPLAY_TIME = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(isLoggedin()){
                    Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }
                else {
                Intent mainIntent = new Intent(Splash.this, LoginActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();}

            }

        }, SPLASH_DISPLAY_TIME);

    }

    protected boolean isLoggedin(){
        SharedPreferences sharedPreferences =getSharedPreferences("MusicBox", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        Log.d("WEI check", "username " + username + "password " + password);
        editor.commit();
        if (username == null || password == null || username.length() == 0 || password.length() == 0)
            return false;

        return true;
    }

}
