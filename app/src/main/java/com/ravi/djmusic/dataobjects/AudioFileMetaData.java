package com.ravi.djmusic.dataobjects;

import android.graphics.Bitmap;

public class AudioFileMetaData {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String duration;
    private Bitmap albumArt;

    public AudioFileMetaData(String title, String artist, String album, String genre, String duration, Bitmap albumArt) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.albumArt = albumArt;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getDuration() {
        return duration;
    }

    public Bitmap getAlbumArt() {
        return this.albumArt;
    }
}
