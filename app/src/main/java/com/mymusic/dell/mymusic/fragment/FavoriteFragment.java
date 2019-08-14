package com.mymusic.dell.mymusic.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mymusic.dell.mymusic.MainActivity;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.RecyclerItemClickListener;
import com.mymusic.dell.mymusic.Utils.MyMediaStory;
import com.mymusic.dell.mymusic.adapter.SongAdapter;
import com.mymusic.dell.mymusic.database.FavoriteDB;
import com.mymusic.dell.mymusic.model.Song;
import com.mymusic.dell.mymusic.service.MusicService;
import com.mymusic.dell.mymusic.view.ActivityAlbum;
import com.mymusic.dell.mymusic.view.PlayActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteFragment extends Fragment {

    @BindView(R.id.recyclerFavorite)
    RecyclerView recyclerView;
    private ArrayList<Song> mSongList = new ArrayList<>();
    private SongAdapter mAdapter;
    FavoriteDB favoriteDB;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorite, container, false);
        ButterKnife.bind(this, view);

        favoriteDB = new FavoriteDB(getActivity());
        mSongList = favoriteDB.getListSong();

        Log.e("LIST", mSongList.get(0).getTitle() + "");
        favoriteDB.close();
        mAdapter = new SongAdapter(getContext(), mSongList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteDB = new FavoriteDB(getActivity());
        mSongList = favoriteDB.getListSong();
        favoriteDB.close();



    }
    @Override
    public void onResume() {
        Log.e("RESUME","resume");
        favoriteDB = new FavoriteDB(getActivity());
        mSongList = (ArrayList<Song>) favoriteDB.getListSong();
        mAdapter = new SongAdapter(getContext(), (ArrayList<Song>) mSongList);
        recyclerView.setAdapter(mAdapter);
        super.onResume();
    }
    public void updateList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static FavoriteFragment newInstance(int sectionNumber) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FavoriteFragment() {
    }

}


