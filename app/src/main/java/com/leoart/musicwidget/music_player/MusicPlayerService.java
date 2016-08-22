package com.leoart.musicwidget.music_player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.leoart.musicwidget.MainActivity;
import com.leoart.musicwidget.R;
import com.leoart.musicwidget.model.MediaPlayerStateModel;
import com.leoart.musicwidget.model.Song;
import com.leoart.musicwidget.utils.Constants;

import java.util.List;

public class MusicPlayerService extends Service implements SongsProvider.SongProviderListener, MediaPlayerController.StateChangedListener {

    public static final String WIDGET_STATE = "WIDGET_STATE";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_NEXT_SONG = "ACTION_NEXT_SONG";
    public static final String ACTION_PREV_SONG = "ACTION_PREV_SONG";
    public static final String ACTION_SHUFFLE = "ACTION_SHUFFLE";
    public static final String ACTION_REPEAT = "ACTION_REPEAT";

    private static final String TAG = "MusicPlayerService";

    private MediaPlayerController player;

    private SongsProvider songsProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        player = new MediaPlayerController(this, getApplicationContext());
        songsProvider = new MP3SongProvider(getBaseContext());
        songsProvider.loadAllSongs(this);

        makeForeground();
    }

    private void makeForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Music Widget")
                .setTicker("Music Widget")
                .setContentText("My mp3 music")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null && intent.getAction() != null) {
            handleIntent(intent);
        }
        return START_STICKY;
    }

    protected void updateWidget(MediaPlayerStateModel widgetState, Context context) {
        if (widgetState.getSong() != null) {
            Log.d(TAG, "updateWidget: " + widgetState.getSong().getTitle());
        } else {
            Log.d(TAG, "updateWidget");
        }

        Intent intent = new Intent(context, MusicWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(WIDGET_STATE, widgetState);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, MusicWidgetProvider.class));
        if (ids != null && ids.length > 0) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);
        }
    }

    private void handleIntent(Intent intent) {
        if (player.songsLoaded()) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_PLAY:
                    player.togglePlay();
                    break;
                case ACTION_NEXT_SONG:
                    player.playNextSong();
                    break;
                case ACTION_PREV_SONG:
                    player.playPrevSong();
                    break;
                case ACTION_SHUFFLE:
                    player.toggleShuffle();
                    break;
                case ACTION_REPEAT:
                    player.toggleRepeat();
                    break;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        player.stop();
        player.release();
        player = null;
        stopForeground(true);
        resetWidget();
        super.onDestroy();
    }

    private void resetWidget() {
        MediaPlayerStateModel mediaPlayerStateModel = new MediaPlayerStateModel();
        mediaPlayerStateModel.setFormattedDuration(getString(R.string.empty_durarion));
        updateWidget(mediaPlayerStateModel, getBaseContext());
    }

    @Override
    public void onSongsLoaded(List<Song> songs) {
        this.player.setSongs(songs);
    }

    @Override
    public void onSongsLoadingError() {
        Toast.makeText(getBaseContext(), getString(R.string.songs_loading_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStateChanged(MediaPlayerStateModel mediaPlayerStateModel) {
        updateWidget(mediaPlayerStateModel, getBaseContext());
    }
}
