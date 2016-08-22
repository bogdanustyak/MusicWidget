package com.leoart.musicwidget.music_player;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import com.leoart.musicwidget.model.Song;

import java.util.ArrayList;
import java.util.List;

public abstract class SongsProvider {
    protected static final String IS_MUSIC_SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    protected static final String[] PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
    };

    private Context context;
    private SongProviderListener listener;

    public SongsProvider(Context context) {
        this.context = context;
    }

    public void loadAllSongs(SongProviderListener listener) {
        this.listener = listener;

        if (permissionsGranted()) {
            loadSongs();
        } else {
            listener.onSongsLoadingError();
        }
    }

    private void loadSongs() {
        List<Song> songs = null;
        try (Cursor cursor = getSongsCursor(context)) {
            songs = new ArrayList<>(cursor.getCount());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Song song = new Song(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5),
                        cursor.getString(6));
                songs.add(song);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onSongsLoadingError();
        }
        listener.onSongsLoaded(songs);
    }

    private boolean permissionsGranted() {
        boolean permissionsGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionsGranted = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            permissionsGranted = true;
        }
        return permissionsGranted;
    }

    protected abstract Cursor getSongsCursor(Context context);

    protected abstract String[] getSongsFormats();

    interface SongProviderListener {
        void onSongsLoaded(List<Song> songs);

        void onSongsLoadingError();
    }
}
