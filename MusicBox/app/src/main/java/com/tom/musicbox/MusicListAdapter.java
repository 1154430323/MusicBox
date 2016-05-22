package com.tom.musicbox;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tom on 2016/5/7.
 */
public class MusicListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<Song>musicFile;
    int currentlndex;

    public MusicListAdapter(Context mContext, int currentlndex, ArrayList<Song> musicFile) {
        this.mContext = mContext;
        this.musicFile = musicFile;
        this.currentlndex = currentlndex;
    }

    static class MusicItem{
        ImageView albumView;
        TextView songTitleView;
        TextView songSubTitleView;
        ImageView isPlayingView;

    }

    @Override
    public int getCount() {

        return musicFile.size();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        MusicItem musicItem;
        if (row == null){
            LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
            row = layoutInflater.inflate(R.layout.layout_item,parent,false);
            musicItem = new MusicItem();

            musicItem.albumView = (ImageView)row.findViewById(R.id.album_imageview);
            musicItem.songTitleView = (TextView)row.findViewById(R.id.music_title_view);
            musicItem.songSubTitleView = (TextView)row.findViewById(R.id.music_subtitle_view);
            musicItem.isPlayingView = (ImageView)row.findViewById(R.id.isPlaying_imageview);
            row.setTag(musicItem);

        }
        else {
            musicItem = (MusicItem) row.getTag();

        }

        Song currentSong = musicFile.get(position);
        musicItem.albumView.setImageBitmap(currentSong.getAlbumView());
        musicItem.songTitleView.setText(currentSong.getTitle());
        musicItem.songSubTitleView.setText(currentSong.getArtist() + " . " + currentSong.getAlbum());
        if (currentlndex != position)
            musicItem.isPlayingView.setVisibility(View.INVISIBLE);
        else
        musicItem.isPlayingView.setVisibility(View.VISIBLE);

        return row;

    }

    @Override
    public Object getItem(int position) {

        return musicFile.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;


    }




}
