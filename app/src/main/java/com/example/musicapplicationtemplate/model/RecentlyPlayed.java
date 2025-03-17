package com.example.musicapplicationtemplate.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class RecentlyPlayed implements Serializable {
    private int id;

    @SerializedName("user")
    private User user;
    @SerializedName("song")
    private Song song;

    private Date played_at;

    public RecentlyPlayed() {
    }

    public RecentlyPlayed(int id, User user, Song song, Date played_at) {
        this.id = id;
        this.user = user;
        this.song = song;
        this.played_at = played_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Date getPlayed_at() {
        return played_at;
    }

    public void setPlayed_at(Date played_at) {
        this.played_at = played_at;
    }
}
