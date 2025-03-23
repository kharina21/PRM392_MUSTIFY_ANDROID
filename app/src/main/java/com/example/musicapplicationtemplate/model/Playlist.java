package com.example.musicapplicationtemplate.model;

import java.io.Serializable;
import java.util.Date;

public class Playlist implements Serializable {
    private int playlist_id;
    private String playlist_name;
    private int user_id;
    private int song_count;
    private Date created_date;

    public Playlist() {
    }

    public Playlist(int playlist_id, String playlist_name, int user_id,int song_count, Date created_date) {
        this.playlist_id = playlist_id;
        this.playlist_name = playlist_name;
        this.user_id = user_id;
        this.song_count = song_count;
        this.created_date = created_date;
    }

    public int getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(int playlist_id) {
        this.playlist_id = playlist_id;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public int getUser() {
        return user_id;
    }

    public void setUser(User user) {
        this.user_id = user_id;
    }

    public int getSong_count() {
        return song_count;
    }

    public void setSong_count(int song_count) {
        this.song_count = song_count;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
}
