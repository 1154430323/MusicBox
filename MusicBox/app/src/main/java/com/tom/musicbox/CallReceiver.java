package com.tom.musicbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.style.TtsSpan;
import android.util.Log;

/**
 * Created by tom on 2016/4/30.
 */
public class CallReceiver extends BroadcastReceiver{
    TelephonyManager telephonyManager;
    Context mContext;//上下文

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        //实例化 TelephonyManager

        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

    }

    private final PhoneStateListener phoneStateListener = new PhoneStateListener(){

      public void onCallStateChanged (int state ,String incomingNumber){

          switch (state){

              case TelephonyManager.CALL_STATE_RINGING:{
                  Log.d("CallReceiver","Tom send pause request");
                  Intent pauseIntent = new Intent(MyMusicService.PAUSE_ACTION);
                  pauseIntent.putExtra("play",0);
                  mContext.sendBroadcast(pauseIntent);
                  break;
              }
              case TelephonyManager.CALL_STATE_IDLE: {
                  Log.d("CallReceiver","Tom send play request");
                  Intent playIntent = new Intent(MyMusicService.PLAY_ACTION);
                  playIntent.putExtra("play",1);
                  mContext.sendBroadcast(playIntent);
                  break;
              }
              default:
                  break;

          }

      }



    };


}
