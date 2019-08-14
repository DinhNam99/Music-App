package com.mymusic.dell.mymusic.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
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

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.Utils.MyMediaStory;
import com.mymusic.dell.mymusic.adapter.SongAdapter;
import com.mymusic.dell.mymusic.database.FavoriteDB;
import com.mymusic.dell.mymusic.database.PlayListDB;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityPlayList extends AppCompatActivity {

    @BindView(R.id.collapsingtoolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbarpl)
    Toolbar toolbar;
    @BindView(R.id.imagePLbg)
    ImageView imagePLbg;
    @BindView(R.id.imageplist)
    ImageView imagepList;

    @BindView(R.id.playlistItem)
    TextView tvName;
    @BindView(R.id.numSongsItem)
    TextView tvNumSongs;
    @BindView(R.id.recyclerPlisSong)
    RecyclerView recyclerPlisSong;

    FavoriteDB favoriteDB;
    private ArrayList<Song> mSongList = new ArrayList<>();
    private SongAdapter mAdapter;
    int id;

    PlayListDB playListDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_play_list);
        ButterKnife.bind(this);
        playListDB = new PlayListDB(this);

        init();
        setUpAdapter();
    }

    private void init(){
        Intent intent = getIntent();
        String nameList = intent.getStringExtra("NameList");
        id = intent.getIntExtra("IDPL",0);
        int numSongs = intent.getIntExtra("numSongs",0);
        String image = intent.getStringExtra("IMAGEPL");
        if(image == null){
            Glide.with(this).load(R.drawable.artsong).into(imagePLbg);
            Glide.with(this).load(R.drawable.artsong).into(imagepList);
        }else{
            Glide.with(this).load(image).into(imagePLbg);
            Glide.with(this).load(image).into(imagepList);
        }
        Log.e("NUM",numSongs+"");

        collapsingToolbarLayout.setTitle(nameList);
        tvName.setText(nameList);
        tvNumSongs.setText(numSongs+" songs");
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
        mSongList = playListDB.getAllPlaylistSongs(id);

        mAdapter = new SongAdapter(this, mSongList);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(this.getApplicationContext());
        recyclerPlisSong.setLayoutManager(mLayoutManager);
        recyclerPlisSong.setItemAnimator(new DefaultItemAnimator());
        recyclerPlisSong.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }
}
