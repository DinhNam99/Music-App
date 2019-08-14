package com.mymusic.dell.mymusic.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

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

public class ActivityAllSong extends AppCompatActivity {

    @BindView(R.id.songforsearch)
    RecyclerView recyclerView;
    @BindView(R.id.search_song)
    EditText edSearch;
    MyMediaStory mediaStore;
    private ArrayList<Song> mSongList = new ArrayList<>();

    private SongAdapter mAdapter;

    MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    FavoriteDB favoriteDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_all_song);
        ButterKnife.bind(this);
        mediaStore = new MyMediaStory(this);
        favoriteDB = new FavoriteDB(this);
        mSongList = mediaStore.getSong();
        searchSong();
    }

    public void searchSong(){
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final ArrayList<Song> filteredList = new ArrayList<>();
                s = s.toString().toLowerCase();

                for (int i = 0; i < mSongList.size(); i++) {

                    final String text = mSongList.get(i).getTitle().toLowerCase();
                    if (text.contains(s)) {

                        filteredList.add(mSongList.get(i));
                    }
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(ActivityAllSong.this));
                mAdapter = new SongAdapter(ActivityAllSong.this,filteredList);
                recyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();
           }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
