package com.mymusic.dell.mymusic.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for music playback. This is the main controller that handles all user actions
 * regarding song playback
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    // Media player
    private MediaPlayer player;
    // Song list
    private ArrayList<Song> songs;
    // Current position
    private int songPos;
    // Our binder
    private final IBinder musicBind = new MusicBinder();
    private OnSongChangedListener onSongChangedListener;

    public static final int STOPPED = 0;
    public static final int PAUSED = 1;
    public static final int PLAYING = 2;
    private int playerState = STOPPED;
    private SeekBar mSeekBar;
    private TextView mCurrentPosition;
    private TextView mTotalDuration;
    private int mInterval = 1000;
    private static final String CHANNEL_2_ID = "CHANNEL_2";

    private NotificationManagerCompat notificationManager;
    private MediaSessionCompat mediaSession;

    private enum ACTIONS {
        ACION_PAUSE,
        ACION_NEXT,
        ACION_PREV,
    }

    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }

    int duration = 0;
    // Async thread to update progress bar every second
    private Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar != null) {
                mSeekBar.setProgress(player.getCurrentPosition());
                if(player.isPlaying()) {
                    mSeekBar.postDelayed(mProgressRunner, mInterval);
                }
            }
        }
    };

    public MediaPlayer getPlayer() {
        return player;
    }

    public void onCreate(){
        // Create the service
        super.onCreate();
        // Initialize position
        songPos = 0;
        // Create player
        player = new MediaPlayer();
        initMusicPlayer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTIONS.ACION_PREV.name());
        filter.addAction(ACTIONS.ACION_PAUSE.name());
        filter.addAction(ACTIONS.ACION_NEXT.name());
        registerReceiver(receiver, filter);
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);
        mediaSession = new MediaSessionCompat(this, "tag");
    }

    public void initMusicPlayer(){
        // Set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // Set player event listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongs(ArrayList<Song> songs){
        this.songs = songs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Stop media player
        player.stop();
        player.reset();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int newPos = songPos + 1;
        if (newPos == songs.size())
            newPos = 0;
        setSong(newPos);
        togglePlay();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (player != null) {
            player.release();
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        // Start playback
        mp.start();
        duration = mp.getDuration();
        Log.e("DUR",duration+"");
    }


    /**
     * Sets a new song to buffer
     * @param songIndex - position of the song in the array
     */
    public void setSong(int songIndex){
        if (songs.size() <= songIndex || songIndex < 0) // if the list is empty... just return
            return;
        songPos = songIndex;
        playerState = STOPPED;
        onSongChangedListener.onSongChanged(songs.get(songPos));
    }

    /**
     * Toggles on/off song playback
     */
    public void togglePlay() {
        switch(playerState) {
            case STOPPED:
                playSong();
                sendNotification(songs.get(songPos), R.drawable.ic_pause);
                break;
            case PAUSED:
                player.start();
                sendNotification(songs.get(songPos), R.drawable.ic_pause);
                onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
                mProgressRunner.run();
                break;
            case PLAYING:
                player.pause();
                sendNotification(songs.get(songPos), R.drawable.ic_play);
                onSongChangedListener.onPlayerStatusChanged(playerState = PAUSED);
                mSeekBar.removeCallbacks(mProgressRunner);
                break;
        }
    }

    private void playSong() {
        if (songs.size() <= songPos)
            return;
        player.reset();
        Song playSong = songs.get(songPos);
        long currSongID = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSongID);

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
        mProgressRunner.run();
        onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);

    }

    public interface OnSongChangedListener {
        void onSongChanged(Song song);
        void onPlayerStatusChanged(int status);
    }


    public void setOnSongChangedListener(OnSongChangedListener listener) {
        onSongChangedListener = listener;
    }


    public void setUIControls(SeekBar seekBar, TextView currentPosition, TextView totalDuration) {

        mSeekBar = seekBar;
        mCurrentPosition = currentPosition;
        mTotalDuration = totalDuration;
        mSeekBar.setMax(duration);
        mSeekBar.postDelayed(mProgressRunner, mInterval);

        // Set our duration text view to display total duration in format 0:00
        mTotalDuration.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    player.seekTo(progress);
                }

                // Update our textView to display the correct number of second in format 0:00
                mCurrentPosition.setText(String.format("%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(progress),
                        TimeUnit.MILLISECONDS.toSeconds(progress) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress))
                ));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public int getCurrentIndex() {
        return songPos;
    }
    public void sendNotification(Song song,@DrawableRes int idRes) {

        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.drawable.artsong);

        @SuppressLint("ResourceAsColor")
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.music)
                .setContentTitle(song.getTitle())
                .setColor(android.R.color.white)
                .setColor(android.R.color.white)
                .setLargeIcon(artwork)
                .addAction(R.drawable.ic_previous, "Previous", getPendingAction(ACTIONS.ACION_PREV))
                .addAction(idRes, "Pause", getPendingAction(ACTIONS.ACION_PAUSE))
                .addAction(R.drawable.ic_next, "Next", getPendingAction(ACTIONS.ACION_NEXT))
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setSubText("Sub Text")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AAA";
            String description ="HELLO";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_2_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private PendingIntent getPendingAction(ACTIONS action) {
        Intent intent = new Intent(action.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(ACTIONS.ACION_NEXT.name())) {

                int current = getCurrentIndex();
                int next = current + 1;
                // If current was the last song, then play the first song in the list
                if (next == songs.size())
                    next = 0;
                setSong(next);
                togglePlay();
            } else if (action.equalsIgnoreCase(ACTIONS.ACION_PREV.name())) {
                int current = getCurrentIndex();
                int previous = current - 1;
                // If current was 0, then play the last song in the list
                if (previous < 0)
                    previous = songs.size() - 1;
                setSong(previous);
                togglePlay();
            } else if (action.equalsIgnoreCase(ACTIONS.ACION_PAUSE.name())) {
                togglePlay();
            }
        }
    };
    public boolean isPause() {
        if (player != null) {
            return !player.isPlaying();
        }
        return false;
    }
}
