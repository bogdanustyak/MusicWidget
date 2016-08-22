package com.leoart.musicwidget.music_player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.leoart.musicwidget.R;
import com.leoart.musicwidget.model.MediaPlayerStateModel;
import com.leoart.musicwidget.model.Song;
import com.leoart.musicwidget.utils.MediaFormatter;

import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MediaPlayerController extends MediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public enum PlayMode {DEFAULT, SHUFFLE}

    private static final String TAG = "MediaPlayerController";

    private int currentSongPosition = 0;
    private PlayMode playMode = PlayMode.SHUFFLE;
    private Boolean repeatEnabled = false;
    private Stack<Integer> playedSongsPositions = new Stack<>();
    private List<Song> songs;
    private Context context;
    private StateChangedListener stateChangedListener;
    private Handler mHandler = new Handler();
    private Runnable updateMediaProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && stateChangedListener != null) {
                stateChangedListener.onStateChanged(getState(false));
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    public MediaPlayerController(StateChangedListener stateChangedListener, Context context) {
        super();
        this.context = context;
        this.stateChangedListener = stateChangedListener;

        setWakeMode(context,
                PowerManager.PARTIAL_WAKE_LOCK);
        setAudioStreamType(AudioManager.STREAM_MUSIC);
        setOnPreparedListener(this);
        setOnCompletionListener(this);
        setOnErrorListener(this);
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        startUpdatingProgress();
    }

    private void startUpdatingProgress() {
        Log.d(TAG, "startUpdatingProgress");
        mHandler.removeCallbacks(updateMediaProgressRunnable);
        mHandler.post(updateMediaProgressRunnable);
    }

    @Override
    public void stop() throws IllegalStateException {
        stopUpdatingProgress();
        super.stop();
    }

    private void stopUpdatingProgress() {
        Log.d(TAG, "stopUpdatingProgress");
        mHandler.removeCallbacks(updateMediaProgressRunnable);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onCompletion");
        if (mediaPlayer.getCurrentPosition() > 0) {
            mediaPlayer.reset();
        }
        playNextSong();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Log.d(TAG, "onPrepared");
        if (songs != null && songs.size() > 0) {
            stateChangedListener.onStateChanged(getState(true));
        }
    }

    public void playNextSong() {
        Log.d(TAG, "playNextSong");
        if (songs.size() > 0) {
            playSong(getNextSongPosition());
        } else {
            Toast.makeText(context, context.getString(R.string.no_songs), Toast.LENGTH_LONG).show();
        }
    }

    private int getNextSongPosition() {
        playedSongsPositions.push(currentSongPosition);
        switch (playMode) {
            case DEFAULT:
                return getNextSongDefaultMode();
            case SHUFFLE:
                return getNextSongShuffleMode();
        }
        return 0;
    }

    private int getNextSongShuffleMode() {
        int currentPosition = currentSongPosition;
        int nextPosition = currentSongPosition;

        while (currentPosition == nextPosition) {
            nextPosition = new Random().nextInt(songs.size());
        }
        return nextPosition;
    }

    private int getNextSongDefaultMode() {
        if (currentSongPosition + 1 < songs.size() - 1) {
            return currentSongPosition + 1;
        } else {
            return 0; // TODO handle repeat enabled/disabled
        }
    }

    public void playPrevSong() {
        Log.d(TAG, "playPrevSong");
        if (songs.size() > 0) {
            playSong(getPrevSongPosition());
        } else {
            Toast.makeText(context, context.getString(R.string.no_songs), Toast.LENGTH_LONG).show();
        }
    }

    private int getPrevSongPosition() {
        switch (playMode) {
            case DEFAULT:
                return getPrevSongDefaultMode();
            case SHUFFLE:
                return getPrevSongShuffleMode();
        }
        return 0;
    }

    private int getPrevSongShuffleMode() {
        return playedSongsPositions.size() > 0 ? playedSongsPositions.pop() : 0;
    }

    private int getPrevSongDefaultMode() {
        if (currentSongPosition - 1 <= 0) {
            return songs.size() - 1;
        } else {
            return currentSongPosition - 1; // TODO handle repeat enabled/disabled
        }
    }

    private void playSong(int songPosition) {
        Log.d(TAG, "playSong: " + songPosition);

        this.currentSongPosition = songPosition;
        this.reset();

        Song song = songs.get(songPosition);
        Uri trackUri = Uri.parse(song.getData());
        try {
            setDataSource(context, trackUri);
        } catch (Exception e) {
            Log.e(TAG, "Error setting data source", e);
        }

        prepareAsync();
    }

    public void toggleRepeat() {
        this.repeatEnabled = !repeatEnabled;
        stateChangedListener.onStateChanged(getState(true));
        Toast.makeText(context, context.getString(R.string.tbd), Toast.LENGTH_LONG).show();
    }

    public void toggleShuffle() {
        if (playMode == PlayMode.DEFAULT) {
            playMode = PlayMode.SHUFFLE;
        } else {
            playMode = PlayMode.DEFAULT;
        }

        stateChangedListener.onStateChanged(getState(true));
        playNextSong();
    }

    public void togglePlay() {
        if (this.isPlaying()) {
            this.pause();
        } else {
            this.start();
        }
        stateChangedListener.onStateChanged(getState(true));
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.v(TAG, "MediaPlayer Error");
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void release() {
        stateChangedListener = null;
        super.release();
    }

    public boolean songsLoaded() {
        return songs != null && songs.size() > 0;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    private MediaPlayerStateModel getState(boolean songChanged) {
        Song song = songs.size() > 0 ? songs.get(currentSongPosition) : null;
        MediaPlayerStateModel stateModel = new MediaPlayerStateModel();
        stateModel.setSong(song);
        stateModel.setPlaying(this.isPlaying());
        stateModel.setPlayMode(this.playMode);
        stateModel.setRepeatEnabled(this.repeatEnabled);
        stateModel.setSongHasChanged(songChanged);
        long duration = 0;
        if (song != null) {
            duration = song.getDuration();
        }
        stateModel.setFormattedDuration(getFormattedDuration(duration));
        return stateModel;
    }

    private String getFormattedDuration(long duration) {
        return new MediaFormatter().formatMediaFileDuration(context, duration - getCurrentPosition());
    }

    public interface StateChangedListener {
        void onStateChanged(MediaPlayerStateModel mediaPlayerStateModel);
    }
}
