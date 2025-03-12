package com.example.musicapplicationtemplate.model;

import java.util.Date;

public class Like {
    private int like_id;
    private User user;
    private Song song;
    private Date created_date;

    public Like() {
    }

    public Like(int like_id, User user, Song song, Date created_date) {
        this.like_id = like_id;
        this.user = user;
        this.song = song;
        this.created_date = created_date;
    }

    public int getLike_id() {
        return like_id;
    }

    public void setLike_id(int like_id) {
        this.like_id = like_id;
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

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
}
