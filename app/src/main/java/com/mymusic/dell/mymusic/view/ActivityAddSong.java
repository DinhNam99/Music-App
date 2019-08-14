package com.mymusic.dell.mymusic.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.RecyclerItemClickListener;
import com.mymusic.dell.mymusic.Utils.MyMediaStory;
import com.mymusic.dell.mymusic.adapter.SongAdapter;
import com.mymusic.dell.mymusic.adapter.SongAdapterAddPL;
import com.mymusic.dell.mymusic.database.PlayListDB;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityAddSong extends AppCompatActivity {

    @BindView(R.id.rcAddpl)
    RecyclerView recyclerView;
    @BindView(R.id.search_song_addpl)
    EditText edSearch;
    @BindView(R.id.done)
    FloatingActionButton done;
    private ArrayList<Song> mSongList = new ArrayList<>();

    private SongAdapterAddPL mAdapter;
    MyMediaStory mediaStore;
    PlayListDB playListDB;

    ArrayList<Integer> songsID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_add_song);
        ButterKnife.bind(this);
        mediaStore = new MyMediaStory(this);
        mSongList = mediaStore.getSong();
        playListDB = new PlayListDB(this);
        searchSong();
        addSong();
    }

    public void searchSong(){
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                s = s.toString().toLowerCase();
                final ArrayList<Song> filteredList = new ArrayList<>();


                for (int i = 0; i < mSongList.size(); i++) {

                    final String text = mSongList.get(i).getTitle().toLowerCase();
                    if (text.contains(s)) {

                        filteredList.add(mSongList.get(i));
                    }
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(ActivityAddSong.this));
                mAdapter = new SongAdapterAddPL(ActivityAddSong.this,filteredList);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void addSong(){
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Song song : mAdapter.songChecked){
                    songsID.add((int) song.getId());
                }
                if(mAdapter.songChecked.size()>0){
                    playListDB.addSong(songsID,playListDB.getAllPlaylist().get(playListDB.getAllPlaylist().size()-1).getId());
                    Toast.makeText(ActivityAddSong.this,"Added to playlist",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ActivityAddSong.this,"Not added to playlist",Toast.LENGTH_SHORT).show();
                }
                finish();

            }
        });

    }
}
