package com.leoart.musicwidget.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String id;
    private String title;
    private String artist;
    private String album;
    private String displayName;
    private long duration;
    private String data;

    public Song(String id, String title, String artist, String album, String displayName, long duration, String data) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.displayName = displayName;
        this.duration = duration;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getDuration() {
        return duration;
    }

    public String getAlbum() {
        return album;
    }

    public String getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeString(this.displayName);
        dest.writeLong(this.duration);
        dest.writeString(this.data);
    }

    protected Song(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.displayName = in.readString();
        this.duration = in.readLong();
        this.data = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
