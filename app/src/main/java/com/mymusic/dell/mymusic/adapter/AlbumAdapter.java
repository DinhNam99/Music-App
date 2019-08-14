package com.mymusic.dell.mymusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.model.Album;
import com.mymusic.dell.mymusic.view.ActivityAlbum;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {
    private ArrayList<Album> arrAlbum;
    private LayoutInflater inflater;
    private Context context;



    public AlbumAdapter(Context context,ArrayList<Album>arrAlbum) {
        this.arrAlbum = arrAlbum;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_album, viewGroup, false);
        return new AlbumHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder albumHolder, int i) {
        final Album album = arrAlbum.get(i);
        final String linkImg = arrAlbum.get(i).getImageAlbum();
        if (linkImg == null) {
            Glide.with(context).load(R.drawable.artsong).into(albumHolder.imageAB);
        } else
            Glide.with(context).load(linkImg).into(albumHolder.imageAB);

        albumHolder.tvNameAB.setText(album.getNameAlbum()+"");
        albumHolder.txvArtist.setText(album.getNameArtist()+"");
        albumHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityAlbum.class);
                intent.putExtra("linkIMG",linkImg);
                intent.putExtra("NAMEAB",album.getNameAlbum());
                intent.putExtra("NUMAB",album.getNumSong());
                intent.putExtra("ARTISTAB",album.getNameArtist());
                intent.putExtra("IDAB",album.getIdAlbum());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrAlbum.size();
    }


    public class AlbumHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageAB)
        ImageView imageAB;
        @BindView(R.id.tvNameAB)
        TextView tvNameAB;
        @BindView(R.id.txvArtist)
        TextView txvArtist;
        @BindView(R.id.layoutABItem)
        RelativeLayout layout;
        public AlbumHolder(View view){
            super(view);
            ButterKnife.bind(this,view);

        }
    }


}