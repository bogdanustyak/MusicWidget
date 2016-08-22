package com.leoart.musicwidget.music_player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

public class MusicFileAlbumCover {

    private String filePath;

    public MusicFileAlbumCover(String filePath) {
        this.filePath = filePath;
    }

    public Bitmap getAlbumCover() {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        mmr.extractMetadata(MediaMetadataRetriever.OPTION_CLOSEST);

        byte[] data = mmr.getEmbeddedPicture();

        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
