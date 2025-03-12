package com.example.musicapplicationtemplate.model;

import java.io.Serializable;
import java.util.Date;

public class Song implements Serializable {
    private int song_id;
    private String title;
    private int type_id;
    private String artist;
    private String album;
    private int duration;
    private String file_path;
    private Date created_date;
    private String image;

    public Song(){

    }
    public Song(int song_id, String title, int type_id, String artist, String album, int duration, String file_path, Date created_date, String image) {
        this.song_id = song_id;
        this.title = title;
        this.type_id = type_id;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.file_path = file_path;
        this.created_date = created_date;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
}
