package com.example.musicapplicationtemplate.model;

import java.io.Serializable;
import java.util.Date;

public class PlayList implements Serializable {
    private int playlist_id;
    private String playlist_name;
    private User user;

    private Date created_date;

    public PlayList() {
    }

    public PlayList(int playlist_id, String playlist_name, User user, Date created_date) {
        this.playlist_id = playlist_id;
        this.playlist_name = playlist_name;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
}
