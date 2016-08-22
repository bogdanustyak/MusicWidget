package com.leoart.musicwidget.music_player;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MP3SongProvider extends SongsProvider {
    private static final String LIKE_SELECTION = MediaStore.Audio.Media.DATA + " like ?";
    private static final String MP3_FORMAT = "mp3";
    private static final String AND = " AND ";

    public MP3SongProvider(Context context) {
        super(context);
    }

    @Override
    protected Cursor getSongsCursor(Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                IS_MUSIC_SELECTION + AND + LIKE_SELECTION,
                getSongsFormats(),
                null);
    }

    @Override
    protected String[] getSongsFormats() {
        return new String[]{"%" + MP3_FORMAT + "%"};
    }
}
