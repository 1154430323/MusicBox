package com.tom.musicbox;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by tom on 2016/2/27.
 */
public class MyMusicService extends Service implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener

{
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String audioLink;
//    4 actiom
//    play, pause, next,previous
    public static final String PLAY_ACTION="com.Tom.musicbox.MUSIC_PAYL";
    public static final String PAUSE_ACTION="com.Tom.musicbox.MUSIC_PAUSE";
    public static final String NEXT_ACTION="com.Tom.musicbox.MUSIC_NEXT";
    public static final String PREVIOUS_ACTION="com.Tom.musicbox.MUISC_PREVIOUS";
    boolean isBroadcastRegistered;
    Intent playBackIntent;
//    check if this receiver has been registered;
    protected BroadcastReceiver musicPlayBackReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            get Bundle from Intent;
            Bundle bundle = intent.getExtras();
//            o is pause; 1 is paly;
            int code = bundle.getInt("play");
//            create a uri for get music path;
            switch (code){
                case 0:{
                    ;
//                    pause ,music;
                    pause();
                    break;
                }
                case 1:{
//                    play music
                    playMedia();
                    break;
                }
                default:
                    break;

            }
        }
    };

//    Handler is used for catching coming message from sub Thread;
    private android.os.Handler mHandler = new android.os.Handler() {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
//        get current position of mediaPlayer
        int position = mediaPlayer.getCurrentPosition();
        playBackIntent = new Intent(MainActivity.SEEKBAR_UPDATE);
        playBackIntent.putExtra("pos",position);
        sendBroadcast(playBackIntent);


    }
};

    private SeekBarTHread seekBarTHread;

    private class SeekBarTHread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();

                }
                mHandler.sendEmptyMessage(0);
            }

        }
        public void cancel(){
            interrupt();
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer.setOnCompletionListener(this);
        Log.d("Tom", "ServiceOnCreat");
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.reset();//always do that to avoid media player to point to some other song;
//        Refister this broadcastReceiver
//        Step1: Create a IntentFilter which is a class for filter action;
        IntentFilter intentFilter = new IntentFilter();
//        Step2: adding actions;
        intentFilter.addAction(PLAY_ACTION);
        intentFilter.addAction(PAUSE_ACTION);
        intentFilter.addAction(NEXT_ACTION);
        intentFilter.addAction(PREVIOUS_ACTION);
//        Step3: Register this recriver;
        isBroadcastRegistered = false;
        if (!isBroadcastRegistered){

            registerReceiver(musicPlayBackReceiver,intentFilter);
            isBroadcastRegistered = true;
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //called when serivice is started;
        if (intent != null) {
            audioLink = intent.getStringExtra("audioLink");
            mediaPlayer.reset();
            if (!mediaPlayer.isPlaying()) {

                try {
                    mediaPlayer.setDataSource(audioLink);
                    mediaPlayer.prepareAsync();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }
            return START_STICKY;

    }
    protected void playMedia(){
        if (!mediaPlayer.isPlaying())
        mediaPlayer.start();

        playBackIntent = new Intent(MainActivity.MUSIC_INTENT);
        playBackIntent.putExtra("isPlaying",true);
        playBackIntent.putExtra("total",mediaPlayer.getDuration());
        playBackIntent.putExtra("pos",mediaPlayer.getCurrentPosition());
        sendBroadcast(playBackIntent);

    }

    protected void pause(){
        if (mediaPlayer!= null && mediaPlayer.isPlaying())
            mediaPlayer.pause();


        playBackIntent = new Intent(MainActivity.MUSIC_INTENT);
        playBackIntent.putExtra("isPlaying",false);
        playBackIntent.putExtra("total",mediaPlayer.getDuration());
        playBackIntent.putExtra("pos",mediaPlayer.getCurrentPosition());
        sendBroadcast(playBackIntent);

    }

    protected void stopMedia(){
        if (mediaPlayer!= null){
            mediaPlayer.stop();
            //在调用stop后如果需要再次通过start方法，则需要在这里调用prepare函数；
            try {
                mediaPlayer.prepare();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!= null){
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
//    Unrefister recetver
        if(isBroadcastRegistered && musicPlayBackReceiver != null){
            unregisterReceiver(musicPlayBackReceiver);
            isBroadcastRegistered=false;
            musicPlayBackReceiver=null;

        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
        seekBarTHread.cancel();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        seekBarTHread = new SeekBarTHread();
        seekBarTHread.start();
        playMedia();








    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}
