package com.mymusic.dell.mymusic.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.MainActivity;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.RecyclerItemClickListener;
import com.mymusic.dell.mymusic.Utils.MyMediaStory;
import com.mymusic.dell.mymusic.adapter.SongAdapter;
import com.mymusic.dell.mymusic.database.FavoriteDB;
import com.mymusic.dell.mymusic.model.Song;
import com.mymusic.dell.mymusic.service.MusicService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityAlbum extends AppCompatActivity {
    @BindView(R.id.collapsingtoolbarAB)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbarAlbum)
    Toolbar toolbar;

    @BindView(R.id.albumItem)
    TextView tvName;
    @BindView(R.id.numSongsAlbum)
    TextView tvNumSongs;
    @BindView(R.id.aritistAB)
    TextView artist;
    @BindView(R.id.imageAlbum)
    ImageView imageAB;
    @BindView(R.id.imageABbg)
    ImageView imageABbg;
    @BindView(R.id.recyclerAlbumSong)
    RecyclerView recyclerALbumSong;

    FavoriteDB favoriteDB;
    MyMediaStory mediaStore;
    private ArrayList<Song> mSongList = new ArrayList<>();
    private SongAdapter mAdapter;
    int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);

        mediaStore = new MyMediaStory(this);
        favoriteDB = new FavoriteDB(this);
        init();
        setUpAdapter();
    }

    private void init(){
        Intent intent = getIntent();
        String nameList = intent.getStringExtra("NAMEAB");
        String numSongs = intent.getStringExtra("NUMAB");
        id = intent.getIntExtra("IDAB",0);
        String artistAB = intent.getStringExtra("ARTISTAB");
        String linkImg = intent.getStringExtra("linkIMG");
        if (linkImg == null) {
            Glide.with(this).load(R.drawable.artsong).into(imageAB);
        } else
            Glide.with(this).load(linkImg).into(imageAB);
        if (linkImg == null) {
            Glide.with(this).load(R.drawable.artsong).into(imageABbg);
        } else
            Glide.with(this).load(linkImg).into(imageABbg);

        collapsingToolbarLayout.setTitle(nameList);
        tvName.setText(nameList);
        tvNumSongs.setText(numSongs+" songs");
        artist.setText(artistAB);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
    }
    private void setUpAdapter() {

        mSongList = mediaStore.getSongByAlbum(id);
        Collections.sort(mSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        mAdapter = new SongAdapter(this, mSongList);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(this.getApplicationContext());
        recyclerALbumSong.setLayoutManager(mLayoutManager);
        recyclerALbumSong.setItemAnimator(new DefaultItemAnimator());
        recyclerALbumSong.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }


}
