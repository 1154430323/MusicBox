package com.tom.musicbox;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
        ImageButton playPauseBtn, nextBtn, previousBtn;
        SeekBar seekBar;
        TextView startTimerTextView, endTimerTextView;
        ListView songList;
        String[]items;

    ArrayList<Song> musicFile;
    boolean isPlaying;
//    use for switch play /pause button;
    int selectedIndex;
//    use for select music, default is -1
//    broadcastReceiver
    public final static String MUSIC_INTENT = "com.Tom.musicbox.MUSIC_PLAYBACK";
    public final static String SEEKBAR_UPDATE="com.Tom.muisicbox.SEEKBAR_UPDATE";
    boolean isBroadcastRegistered;
    int currentPosition, totalDuration;
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null){
//                get is Playing from service;
                currentPosition = bundle.getInt("pos");
                totalDuration = bundle.getInt("total");
                Log.d("Tom check","Tom check isPlaying "+isPlaying+" currentPosition "+currentPosition+" total "+totalDuration);
                if (intent.getAction() == MUSIC_INTENT){

                    startTimerTextView.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))
                    ));

                    endTimerTextView.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(totalDuration),
                            TimeUnit.MILLISECONDS.toSeconds(totalDuration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration))
                    ));
                    seekBar.setMax(totalDuration);
                    seekBar.setProgress(currentPosition);
                    isPlaying = bundle.getBoolean("isPlaying");

                    if (!isPlaying){
                    playPauseBtn.setBackground(getDrawable(android.R.drawable.ic_media_play));
                    }
                    else {
//                    Display play Btn
                    playPauseBtn.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));

                   }
                }
                else {
                    Log.d("Tom check", "SEEKBAR_UPDATE" + currentPosition);
                    seekBar.setProgress(currentPosition);
                    startTimerTextView.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))
                    ));
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songList = (ListView) findViewById(R.id.song_listview);
        playPauseBtn = (ImageButton)findViewById(R.id.playPause_btn);
        previousBtn = (ImageButton)findViewById(R.id.previous_btn);
        nextBtn = (ImageButton)findViewById(R.id.next_btn);
        startTimerTextView = (TextView)findViewById(R.id.start_timer);
        endTimerTextView = (TextView)findViewById(R.id.end_timer);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        playPauseBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);

        isBroadcastRegistered = false;
        selectedIndex = -1;


        String path  ="/storage/emulated/0/qqmusic";
                //Environment.getExternalStorageDirectory().getPath().toString();
        Log.d("Tom Check","pass "+path);
        File searchFile = new File(path);
        musicFile =findSongs(searchFile);
        items =new String[musicFile.size()];

        for (int i = 0; i <musicFile.size();i++){
            items[i] = musicFile.get(i).getTitle();
            //Log.d("Tom check",i+""+items[i]);
            Log.d("Tom check", musicFile.get(i).getPath().toString() + ""  + items[i]);


        }
       // ArrayAdapter<String>adp = new
         //       ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,
           //     items);
        MusicListAdapter adp = new MusicListAdapter(this,-1,musicFile);

        songList.setAdapter(adp);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Tom","onItemClick");

                Intent musicIntent=new Intent(MainActivity.this,MyMusicService.class);
                musicIntent.putExtra("audioLink",musicFile.get(position).getPath());
                startService(musicIntent);
                selectedIndex = position;
                sendNotification(view);

                
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("WEI check", "onResume called " + isPlaying);
        if (!isBroadcastRegistered){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MUSIC_INTENT);
            intentFilter.addAction(SEEKBAR_UPDATE);
            registerReceiver(broadcastReceiver, intentFilter);
            isBroadcastRegistered = true;}


        }

    void setupBtns() {
        if (!isPlaying){
            playPauseBtn.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
        else {
//         Display play Btn
            playPauseBtn.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));

        }

    }


    MediaMetadataRetriever metaRetriver;

    public ArrayList<Song> findSongs(File root) {
            ArrayList<Song> musicFile = new ArrayList<Song>();
            //searching music file;

            File [] files = root.listFiles();
        if(files == null) {
            Log.d("Tom check", "file is null");
            return musicFile;
        }

            for (File singleFile : files ){
        // if file is music file, add it into musicfile list;

            if(singleFile.isDirectory()&& !singleFile.isHidden()){
                //recursive method
                musicFile.addAll(findSongs(singleFile));



            }
              else {
                if(singleFile.getName().endsWith(".mp3")||
                        singleFile.getName().endsWith(".wav")){
                    if (metaRetriver == null)
                    metaRetriver=new MediaMetadataRetriever();
                    metaRetriver.setDataSource(singleFile.getPath());

                    Song mSong = new Song();
                    mSong.setId(musicFile.size() + 1);

                    try {
                        byte[] art = metaRetriver.getEmbeddedPicture();
                        Bitmap songimage = BitmapFactory.decodeByteArray(art, 0, art.length);
                        mSong.setAlbumView(songimage);}
                    catch (Exception e) {
                        mSong.setAlbumView(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                    }

                    try {
                        mSong.setArtist(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    }
                    catch (Exception e)
                    {
                        mSong.setAlbum("Unknown Album");
                    }

                    try{
                        mSong.setAlbum(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                    }
                    catch (Exception e)
                    {
                        mSong.setArtist("Unknown Artist");
                    }

                    try{
                        mSong.setGenre(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
                    }
                    catch (Exception e)
                    { mSong.setGenre("Unknown Genre");

                    }

                    try{ mSong.setTitle(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

                    }
                    catch (Exception e)
                    {
                        mSong.setTitle(singleFile.getName());
                    }

                    mSong.setPath(singleFile.getAbsolutePath());

                    //this file is muisc file
                    musicFile.add(mSong);
                }
            }

        }

            return musicFile;
        }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.playPause_btn:{
                if (selectedIndex != -1) {
                    if (isPlaying) {
//                        send pause request
                        Intent pauseIntent = new Intent(MyMusicService.PLAY_ACTION);
                        pauseIntent.putExtra("play",0);
                        sendBroadcast(pauseIntent);
                    }  else{
//                    send play request
                        Intent playIntent = new Intent(MyMusicService.PLAY_ACTION);
                        playIntent.putExtra("play",1);
                        sendBroadcast(playIntent);

                    }
                }
                break;
            }



                case R.id.next_btn:{
                    if (selectedIndex != -1){
                        if (selectedIndex == musicFile.size()-1){
                            Toast.makeText(MainActivity.this,"Last one!",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            selectedIndex = selectedIndex + 1;
                            Intent musicIntent = new Intent(MainActivity.this, MyMusicService.class);
                            musicIntent.putExtra("audioLink", musicFile.get(selectedIndex).getPath());
                            startService(musicIntent);
                        }


                    }

                break;
            }



            case R.id.previous_btn:{
                if (selectedIndex != -1){
                    if (selectedIndex == 0){
                        Toast.makeText(MainActivity.this,"First!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        selectedIndex = selectedIndex -1;
                        Intent musicIntent = new Intent(MainActivity.this, MyMusicService.class);
                        musicIntent.putExtra("audioLink", musicFile.get(selectedIndex).getPath());
                        startService(musicIntent);
                    }


                }


                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Register the receiver if not registered;
        if (!isBroadcastRegistered){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MUSIC_INTENT);
            intentFilter.addAction(SEEKBAR_UPDATE);
            registerReceiver(broadcastReceiver, intentFilter);
            isBroadcastRegistered = true;

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBroadcastRegistered){
            unregisterReceiver(broadcastReceiver);
            isBroadcastRegistered=false;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh_settings:
                Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Send simple notification using the NotificationCompat API.
     */
    public void sendNotification(View view) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //create RemoteViews
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(),R.layout.notification);

        // 1.Set Icon
        mRemoteViews.setImageViewBitmap(R.id.notification_icon,musicFile.get(selectedIndex).getAlbumView());
        mRemoteViews.setTextViewText(R.id.notification_music_subTitle,
                musicFile.get(selectedIndex).getArtist()+""+
                        musicFile.get(selectedIndex).getAlbum()+""+
                        musicFile.get(selectedIndex).getGenre());
        if (isPlaying){
            mRemoteViews.setImageViewResource(R.id.notification_playPause_btn,android.R.drawable.ic_media_pause);
            mRemoteViews.setImageViewResource(R.id.notification_playPause_btn,android.R.drawable.ic_media_play);



        }

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_launcher);

        Intent playPauseIntent;
        if (isPlaying) {
//          send pause request
            playPauseIntent = new Intent(MyMusicService.PAUSE_ACTION);
            playPauseIntent.putExtra("play",0);
//            sendBroadcast(playPauseIntent);
        }  else{
//          send play request
            playPauseIntent = new Intent(MyMusicService.PLAY_ACTION);
            playPauseIntent.putExtra("play", 1);

        }

        Intent  nextIntent = new Intent(MainActivity.this, MyMusicService.class);
        if (selectedIndex != musicFile.size()-1){
            Log.d("Tom check","current index"+selectedIndex);
            selectedIndex = selectedIndex + 1;
            Log.d("Tom check","current index 2 "+selectedIndex);
            Log.d("Tom check","audio:"+musicFile.get(selectedIndex).getPath());
            nextIntent.putExtra("audioLink", musicFile.get(selectedIndex).getPath());
        }else{
            Log.d("Tom check","Can not go next");
            nextIntent.putExtra("audioLink", musicFile.get(selectedIndex).getPath());
        }

        PendingIntent pendingPlayPausseIntent =
                PendingIntent.getBroadcast(getBaseContext(),0,
                        playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_playPause_btn,pendingPlayPausseIntent);

        PendingIntent pendingNextIntent =
                PendingIntent.getService(getBaseContext(),0,
                        nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_next_btn,pendingNextIntent);
        builder.setContent(mRemoteViews);


        // This intent is fired when notification is clicked
        Intent intent = new Intent(MainActivity.this, NotificationView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());
    }



}

//pass /storage/emulated/0
