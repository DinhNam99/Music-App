package com.mymusic.dell.mymusic.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.Utils.MyMediaStory;
import com.mymusic.dell.mymusic.adapter.BannerAdapter;
import com.mymusic.dell.mymusic.database.FavoriteDB;
import com.mymusic.dell.mymusic.model.Song;
import com.mymusic.dell.mymusic.service.MusicService;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import me.relex.circleindicator.CircleIndicator;

public class FragmentBanner extends Fragment {
    View view;
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    BannerAdapter adapter;
    Handler handler;
    Runnable runnable;
    int currentBanner;
    ArrayList<Song> randomSong = new ArrayList<>();
    private Intent playIntent;
    private boolean musicBound = false;
    MusicService musicService;
    FavoriteDB favoriteDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragbanner,container,false);
        favoriteDB = new FavoriteDB(getContext());

        init();
        getData();
        setUpBanner();
        return view;
    }


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            // Get service
            musicService = binder.getService();
            // Pass song list
            musicService.setSongs(randomSong);

            musicBound = true;

            // Initialize interfaces
            musicService.setOnSongChangedListener(new MusicService.OnSongChangedListener() {
                @Override
                public void onSongChanged(Song song) {
                }

                @Override
                public void onPlayerStatusChanged(int status) {

                }
            });

            //musicService.setSong(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    private void init(){
        viewPager = view.findViewById(R.id.viewflipper);
        circleIndicator = view.findViewById(R.id.indicator);

    }

    private void getData(){
        MyMediaStory myMediaStory = new MyMediaStory(getContext());
        ArrayList<Song> listSong = new ArrayList<>();
        listSong = myMediaStory.getSong();
        Random random = new Random();
        Vector v = new Vector();
        int iNew = 0;
        for (int i = 0; i < 5;  ) {
            iNew = random.nextInt(listSong.size());
            if (!v.contains(iNew)){
                i++;
                v.add(iNew);
                randomSong.add(listSong.get(iNew));
            }
        }
    }
    private void setUpBanner(){
        adapter = new BannerAdapter(getActivity(),randomSong);
        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                currentBanner = viewPager.getCurrentItem();
                currentBanner++;
                if(currentBanner >=viewPager.getAdapter().getCount()){
                    currentBanner = 0;
                }
                viewPager.setCurrentItem(currentBanner,true);
                handler.postDelayed(runnable,4000);
            }
        };
        handler.postDelayed(runnable,4000);
    }


    @Override
    public void onResume() {
        Log.e("RESUME","resume");
        adapter = new BannerAdapter(getContext(),randomSong);
        viewPager.setAdapter(adapter);
        super.onResume();
    }
}

