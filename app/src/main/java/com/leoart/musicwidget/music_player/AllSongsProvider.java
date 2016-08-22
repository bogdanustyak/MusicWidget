package com.leoart.musicwidget.music_player;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class AllSongsProvider extends SongsProvider {

    @Override
    protected Cursor getSongsCursor(Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                IS_MUSIC_SELECTION,
                getSongsFormats(),
                null);
    }

    @Override
    protected String[] getSongsFormats() {
        return null;
    }

    public AllSongsProvider(Context context) {
        super(context);
    }

}
