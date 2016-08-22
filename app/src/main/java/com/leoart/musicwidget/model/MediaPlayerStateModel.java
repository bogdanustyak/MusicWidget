package com.leoart.musicwidget.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.leoart.musicwidget.music_player.MediaPlayerController;
import com.leoart.musicwidget.music_player.MusicPlayerService;

public class MediaPlayerStateModel implements Parcelable{

    private Song song;
    private boolean isPlaying;
    private MediaPlayerController.PlayMode playMode = MediaPlayerController.PlayMode.DEFAULT;
    private boolean repeatEnabled;
    private boolean songHasChanged = true;
    private String formattedDuration;

    public MediaPlayerStateModel() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.song, flags);
        dest.writeByte(this.isPlaying ? (byte) 1 : (byte) 0);
        dest.writeString(playMode.name());
        dest.writeByte(this.repeatEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.songHasChanged ? (byte) 1 : (byte) 0);
        dest.writeString(this.formattedDuration);
    }

    protected MediaPlayerStateModel(Parcel in) {
        this.song = in.readParcelable(Song.class.getClassLoader());
        this.isPlaying = in.readByte() != 0;
        this.playMode = MediaPlayerController.PlayMode.valueOf(in.readString());
        this.repeatEnabled = in.readByte() != 0;
        this.songHasChanged = in.readByte() != 0;
        this.formattedDuration = in.readString();
    }

    public static final Creator<MediaPlayerStateModel> CREATOR = new Creator<MediaPlayerStateModel>() {
        @Override
        public MediaPlayerStateModel createFromParcel(Parcel source) {
            return new MediaPlayerStateModel(source);
        }

        @Override
        public MediaPlayerStateModel[] newArray(int size) {
            return new MediaPlayerStateModel[size];
        }
    };

    public Song getSong() {
        return song;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public MediaPlayerController.PlayMode getPlayMode() {
        return playMode;
    }

    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setPlayMode(MediaPlayerController.PlayMode playMode) {
        this.playMode = playMode;
    }

    public void setRepeatEnabled(boolean repeatEnabled) {
        this.repeatEnabled = repeatEnabled;
    }

    public boolean isSongHasChanged() {
        return songHasChanged;
    }

    public void setSongHasChanged(boolean songHasChanged) {
        this.songHasChanged = songHasChanged;
    }

    public String getFormattedDuration() {
        return formattedDuration;
    }

    public void setFormattedDuration(String formattedDuration) {
        this.formattedDuration = formattedDuration;
    }

    public static Creator<MediaPlayerStateModel> getCREATOR() {
        return CREATOR;
    }
}
