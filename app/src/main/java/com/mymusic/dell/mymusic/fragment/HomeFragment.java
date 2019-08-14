package com.mymusic.dell.mymusic.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.Utils.MyMediaStory;
import com.mymusic.dell.mymusic.adapter.AlbumAdapter;
import com.mymusic.dell.mymusic.adapter.PlaylistAdapter;
import com.mymusic.dell.mymusic.adapter.SongAdapter;
import com.mymusic.dell.mymusic.database.FavoriteDB;
import com.mymusic.dell.mymusic.database.PlayListDB;
import com.mymusic.dell.mymusic.model.Album;
import com.mymusic.dell.mymusic.model.PlayList;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment{

    @BindView(R.id.recyclerPL)
    RecyclerView rcPlaylist;

    static PlaylistAdapter adapter;
    ArrayList<PlayList> playLists;
    PlayListDB playListDB;

    FavoriteDB favoriteDB;
    MyMediaStory mediaStore;
    private ArrayList<Song> mSongList = new ArrayList<>();
    @BindView(R.id.recyclerS)
    RecyclerView mRecyclerViewSongs;
    private SongAdapter mAdapter;
    @BindView(R.id.recyclerAB)
    RecyclerView recyclerAB;
    ArrayList<Album> albumList = new ArrayList<>();
    AlbumAdapter albumAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fraghome,container,false);
        ButterKnife.bind(this,view);
        mediaStore = new MyMediaStory(getContext());
        init();
        getData();
        setUpAlbum();
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playListDB = new PlayListDB(getActivity());
        favoriteDB = new FavoriteDB(getActivity());
        getSongList();

    }
    private void init(){
        getSongList();
        //mSongList = getMultiDatas(this);


        mAdapter = new SongAdapter(getContext(), mSongList);
        mRecyclerViewSongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewSongs.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setSmoothScrollbarEnabled(true);
        rcPlaylist.setLayoutManager(manager);


    }

    @Override
    public void onResume() {
        Log.e("RESUME","resume");
        getSongList();
        mAdapter = new SongAdapter(getContext(), (ArrayList<Song>) mSongList);
        mRecyclerViewSongs.setAdapter(mAdapter);

        playListDB = new PlayListDB(getActivity());
        playLists = (ArrayList<PlayList>) playListDB.getAllPlaylist();
        adapter = new PlaylistAdapter(getContext(), (ArrayList<PlayList>) playLists);
        rcPlaylist.setAdapter(adapter);
        super.onResume();
    }
    private void setUpAlbum(){
        albumList = mediaStore.getAlbumData();
        albumAdapter = new AlbumAdapter(getContext(),albumList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setSmoothScrollbarEnabled(true);
        recyclerAB.setLayoutManager(manager);
        recyclerAB.setItemAnimator(new DefaultItemAnimator());
        recyclerAB.setAdapter(albumAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void getSongList() {
        mediaStore = new MyMediaStory(getContext());
        mSongList = mediaStore.getSong();
    }

    private void getData(){
        playLists = new ArrayList<>();
        playLists = (ArrayList<PlayList>) playListDB.getAllPlaylist();

        adapter = new PlaylistAdapter(getContext(),playLists);
        rcPlaylist.setAdapter(adapter);
    }

    public void NotifyChanged(ArrayList<PlayList> playLists){

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

    }
    public void updateList() {
        mAdapter.notifyDataSetChanged();
    }
    private static final String ARG_SECTION_NUMBER = "section_number";


    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

}
