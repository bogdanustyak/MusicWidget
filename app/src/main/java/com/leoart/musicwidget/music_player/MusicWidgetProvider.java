package com.leoart.musicwidget.music_player;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.leoart.musicwidget.R;
import com.leoart.musicwidget.model.MediaPlayerStateModel;
import com.leoart.musicwidget.model.Song;

public class MusicWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "MusicWidgetProvider";

    private MediaPlayerStateModel mediaPlayerStateModel;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);
            views.setOnClickPendingIntent(R.id.btn_play_pause, getActionIntent(MusicPlayerService.ACTION_PLAY, context));
            views.setOnClickPendingIntent(R.id.next, getActionIntent(MusicPlayerService.ACTION_NEXT_SONG, context));
            views.setOnClickPendingIntent(R.id.prev, getActionIntent(MusicPlayerService.ACTION_PREV_SONG, context));
            views.setOnClickPendingIntent(R.id.shuffle, getActionIntent(MusicPlayerService.ACTION_SHUFFLE, context));
            views.setOnClickPendingIntent(R.id.repeat, getActionIntent(MusicPlayerService.ACTION_REPEAT, context));

            updateUI(context, mediaPlayerStateModel, views);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void updateUI(Context context, MediaPlayerStateModel mediaPlayerStateModel, RemoteViews views) {
        if (mediaPlayerStateModel != null) {
            Log.d(TAG, "update UI: " + mediaPlayerStateModel.getFormattedDuration());
            if (mediaPlayerStateModel.getFormattedDuration() != null) {
                views.setTextViewText(R.id.tv_duration, mediaPlayerStateModel.getFormattedDuration());
            }
            Song song = mediaPlayerStateModel.getSong();
            if (song != null) {
                if (mediaPlayerStateModel.isSongHasChanged()) {
                    updateNewSong(context, mediaPlayerStateModel, views, song);
                }
            }
        }
    }

    private void updateNewSong(Context context, MediaPlayerStateModel mediaPlayerStateModel,
                               RemoteViews views, Song song) {
        updateTitle(views, song, song.getTitle());
        updateArtist(views, song, song.getArtist());
        updateCover(context, views, song);

        updatePlayButton(context, mediaPlayerStateModel, views);

        updateRepeatButton(context, views, mediaPlayerStateModel.isRepeatEnabled());

        updateShuffleButton(context, mediaPlayerStateModel, views);
    }

    private void updateShuffleButton(Context context, MediaPlayerStateModel mediaPlayerStateModel,
                                     RemoteViews views) {
        if (mediaPlayerStateModel.getPlayMode() == MediaPlayerController.PlayMode.DEFAULT) {
            views.setImageViewBitmap(R.id.shuffle, BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.shuffle_disabled));
        } else {
            views.setImageViewBitmap(R.id.shuffle, BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.shuffle_enabled));
        }
    }

    private void updateRepeatButton(Context context, RemoteViews views, boolean repeatEnabled) {
        if (repeatEnabled) {
            views.setImageViewBitmap(R.id.repeat,
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.repeat_enabled));
        } else {
            views.setImageViewBitmap(R.id.repeat,
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.repeat_disabled));
        }
    }

    private void updatePlayButton(Context context, MediaPlayerStateModel mediaPlayerStateModel,
                                  RemoteViews views) {
        if (isPlaying(mediaPlayerStateModel)) {
            views.setImageViewBitmap(R.id.btn_play_pause,
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.pause_icon));
        } else {
            views.setImageViewBitmap(R.id.btn_play_pause,
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.play_icon));
        }
    }

    private boolean isPlaying(MediaPlayerStateModel mediaPlayerStateModel) {
        return mediaPlayerStateModel.isPlaying();
    }

    private void updateCover(Context context, RemoteViews views, Song song) {
        Bitmap cover = new MusicFileAlbumCover(song.getData()).getAlbumCover();
        if (cover != null) {
            views.setImageViewBitmap(R.id.iv_cover, cover);
        } else {
            views.setImageViewBitmap(R.id.iv_cover,
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.album_cover_default));
        }
    }

    private void updateArtist(RemoteViews views, Song song, String artist) {
        if (artist != null) {
            views.setTextViewText(R.id.tv_artist, song.getArtist());
        }
    }

    private void updateTitle(RemoteViews views, Song song, String title) {
        if (title != null) {
            views.setTextViewText(R.id.tv_title, song.getTitle());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (intent != null && intent.getExtras() != null) {
            mediaPlayerStateModel = intent.getExtras().getParcelable(MusicPlayerService.WIDGET_STATE);
        }
        super.onReceive(context, intent);
    }

    @NonNull
    private PendingIntent getActionIntent(String action, Context context) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(action);
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
