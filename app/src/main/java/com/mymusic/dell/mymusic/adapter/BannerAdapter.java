package com.mymusic.dell.mymusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {

    Context context;
    ArrayList<Song> listSong;

    ArrayList<Song> songs;


    public BannerAdapter(Context context, ArrayList<Song> listSong){
        this.context = context;
        this.listSong = listSong;
        this.songs = listSong;
    }
    @Override
    public int getCount() {
        return listSong.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_banner,null);
        RelativeLayout layout = view.findViewById(R.id.banner);
        ImageView imageBanner = view.findViewById(R.id.imageBanner);
        ImageView imageSong = view.findViewById(R.id.image_song);
        TextView tvTitle = view.findViewById(R.id.titleBanner);
        TextView tvArtist = view.findViewById(R.id.artistBanner);

        final Song song = listSong.get(position);

        Glide.with(context).load(song.getThumbnail()).placeholder(R.drawable
                .artsong).error(R.drawable.artsong).into(imageBanner);
        Glide.with(context).load(song.getThumbnail()).placeholder(R.drawable
                .artsong).error(R.drawable.artsong)
                .into(imageSong);

        tvTitle.setText(song.getTitle()+"");
        tvArtist.setText(song.getArtist()+"");

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
