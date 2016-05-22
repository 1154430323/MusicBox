package com.tom.musicbox;

import android.graphics.Bitmap;

/**
 * Created by tom on 2016/2/27.
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String path;
    private Bitmap albumView;
    private String album;
    private String genre;

    public Bitmap getAlbumView() {
        return albumView;
    }

    public void setAlbumView(Bitmap albumView) {
        this.albumView = albumView;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
