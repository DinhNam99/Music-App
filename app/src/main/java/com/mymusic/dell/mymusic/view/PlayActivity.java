package com.mymusic.dell.mymusic.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.database.FavoriteDB;
import com.mymusic.dell.mymusic.model.Song;
import com.mymusic.dell.mymusic.service.MusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.card_music)
    CardView cardView;
    @BindView(R.id.musicImagePlay)
    ImageView imageSongGB;
    @BindView(R.id.imageMusic)
    ImageView songPlay;
    @BindView(R.id.playSong)
    ImageView play;
    @BindView(R.id.Previous)
    ImageView previous;
    @BindView(R.id.Next)
    ImageView next;
    @BindView(R.id.repeatSong)
    ImageView repeatSong;
    @BindView(R.id.shuffleSong)
    ImageView shuffle;
    @BindView(R.id.seekbar)
    SeekBar seekBar;
    @BindView(R.id.titleMusic)
    TextView title;
    @BindView(R.id.artistMusic)
    TextView artist;
    @BindView(R.id.timeSong)
    TextView time;
    @BindView(R.id.btnFavorite)
    ImageView btnFavorite;
    @BindView(R.id.timeCurrent)
    TextView timecurrent;
    int pos = 0;
    FavoriteDB favoriteDB;
    ArrayList<Song> songList;
    private static MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    boolean isFV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_now);
        ButterKnife.bind(this);
        favoriteDB = new FavoriteDB(this);

        init();
        setUpListeners();


    }

    private void init(){
        Intent intent = getIntent();
        pos = intent.getIntExtra("POS",0);
        songList = (ArrayList) intent.getParcelableArrayListExtra("SONGS");
        isFV = intent.getBooleanExtra("FV",false);
        Song  song = songList.get(pos);
        Glide.with(this).load(song.getThumbnail()).placeholder(R.drawable
                .artsong).error(R.drawable.artsong)
                .into(imageSongGB);
        Glide.with(this).load(song.getThumbnail()).placeholder(R.drawable
                .artsong).error(R.drawable.artsong)
                .into(songPlay);
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        cardView.setAnimation(anim);

        //setFavorite
        if(isFV == true){
            btnFavorite.setImageResource(R.drawable.favorite);
        }else{
            btnFavorite.setImageResource(R.drawable.no_favorite);
        }
//        musicService.setSong(pos);
//        musicService.togglePlay();
    }
    private void setUpListeners() {
        play.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        btnFavorite.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playSong:
                playMusic();
                break;
            case R.id.Previous:
                playPreviousSong();
                break;
            case R.id.Next:
                playNextSong();
                break;
            case R.id.btnFavorite:
                favoriteClick();
                break;
            default:
                break;
        }
    }

    private void favoriteClick(){
        if (favoriteDB.checkFavorite((int) songList.get(musicService.getCurrentIndex()).getId())) {
            btnFavorite.setImageResource(R.drawable.no_favorite);
            favoriteDB.deleteBook(songList.get(musicService.getCurrentIndex()));
        } else {
            btnFavorite.setImageResource(R.drawable.favorite);
            favoriteDB.addSong(songList.get(musicService.getCurrentIndex()));
        }
    }
    private void playMusic() {
        musicService.togglePlay();
        //Log.e("CurrentPos",musicService.getCurrentIndex()+"");
    }

    private void playNextSong() {
        int current = musicService.getCurrentIndex();
        int next = current + 1;
        // If current was the last song, then play the first song in the list
        if (next == songList.size())
            next = 0;
        musicService.setSong(next);
        musicService.togglePlay();
        Log.e("Position",next+"");
        //favorite
        if (favoriteDB.checkFavorite((int) songList.get(next).getId())) {
            btnFavorite.setImageResource(R.drawable.favorite);
        } else {
            btnFavorite.setImageResource(R.drawable.no_favorite);
        }
    }

    private void playPreviousSong() {
        int current = musicService.getCurrentIndex();
        int previous = current - 1;
        // If current was 0, then play the last song in the list
        if (previous < 0)
            previous = songList.size() - 1;
        musicService.setSong(previous);
        musicService.togglePlay();
        if (favoriteDB.checkFavorite((int) songList.get(previous).getId())) {
            btnFavorite.setImageResource(R.drawable.favorite);
        } else {
            btnFavorite.setImageResource(R.drawable.no_favorite);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        // Start service when we start the activity
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopService(playIntent);
    }
    private int mInterval = 1000;
    private Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (seekBar != null) {
                seekBar.setProgress(musicService.getPlayer().getCurrentPosition());
                if(musicService.getPlayer().isPlaying()) {
                    seekBar.postDelayed(mProgressRunner, mInterval);
                }
            }
        }
    };
    // Connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            // Get service
            musicService = binder.getService();
            // Pass song list
            musicService.setSongs(songList);
            musicService.setUIControls(seekBar,timecurrent, time);
            musicBound = true;
            musicService.setPlayerState(MusicService.PLAYING);

            musicService.getPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    // Async thread to update progress bar every second
                    int duration = mp.getDuration();
                    seekBar.setMax(duration);
                    seekBar.postDelayed(mProgressRunner, mInterval);

                    // Set our duration text view to display total duration in format 0:00
                    time.setText(String.format("%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    ));
                }
            });
            seekBar.setMax(musicService.getPlayer().getDuration());
            seekBar.postDelayed(mProgressRunner, mInterval);
            time.setText(String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(musicService.getPlayer().getDuration()),
                    TimeUnit.MILLISECONDS.toSeconds(musicService.getPlayer().getDuration()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(musicService.getPlayer().getDuration()))));
            // Initialize interfaces
            musicService.setOnSongChangedListener(new MusicService.OnSongChangedListener() {
                @Override
                public void onSongChanged(Song song) {
                    artist.setText(song.getArtist());
                    title.setText(song.getTitle());

                    Glide.with(getApplicationContext()).load(song.getThumbnail()).placeholder(R.drawable
                            .artsong).error(R.drawable.artsong)
                            .into(imageSongGB);
                    Glide.with(getApplicationContext()).load(song.getThumbnail()).placeholder(R.drawable
                            .artsong).error(R.drawable.artsong)
                            .into(songPlay);
                    if (favoriteDB.checkFavorite((int) song.getId())) {
                        btnFavorite.setImageResource(R.drawable.favorite);
                    } else {
                        btnFavorite.setImageResource(R.drawable.no_favorite);
                    }
                }

                @Override
                public void onPlayerStatusChanged(int status) {
                    switch (status) {
                        case MusicService.PLAYING:
                            play.setImageResource(R.drawable.pause);
                            break;
                        case MusicService.PAUSED:
                            play.setImageResource(R.drawable.play);
                            break;
                    }
                }
            });

            //musicService.setSong(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public static MusicService getMusicService() {
        return musicService;
    }

    public static void setMusicService(MusicService musicService) {
        PlayActivity.musicService = musicService;
    }
    public void updatFav(ArrayList<Song> songsList, int pos){
        //fa
        final List<Song> songs = favoriteDB.getListSong();
        final Song song = songsList.get(pos);
//        btnFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (favoriteDB.checkFavorite((int) song.getId())) {
//                    btnFavorite.setImageResource(R.drawable.no_favorite);
//                    favoriteDB.deleteBook(song);
//                } else {
//                    btnFavorite.setImageResource(R.drawable.favorite);
//                    favoriteDB.addSong(song);
//                }
//            }
//        });

        for (int i = 0; i < songs.size(); i++) {
            Log.e("ID",songs.get(i).getId()+"-");
            if (song.getId() == (songs.get(i).getId())) {
                btnFavorite.setImageResource(R.drawable.favorite);
            }else if(song.getId() != songs.get(i).getId()){
                btnFavorite.setImageResource(R.drawable.no_favorite);
            }
        }

    }
}
