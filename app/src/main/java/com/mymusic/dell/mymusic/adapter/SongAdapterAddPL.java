package com.mymusic.dell.mymusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongAdapterAddPL extends RecyclerView.Adapter<SongAdapterAddPL.Holder> {

    private ArrayList<Song> mSongList;
    private Context mContext;
    public ArrayList<Song> songChecked = new ArrayList<>();

    LayoutInflater inflater;
    public SongAdapterAddPL(Context context, ArrayList<Song> songList) {
        this.mContext = context;
        this.mSongList = songList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_song_addpl, viewGroup, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final Song song = mSongList.get(i);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        Glide.with(mContext).load(song.getThumbnail()).placeholder(R.drawable
                .artsong).error(R.drawable.artsong)
                .into(holder.thumbnail);

        holder.setItemClick(new ItemClickCheckbox() {
            @Override
            public void itemClickCheck(View view, int position) {
                CheckBox checkBox = (CheckBox) view;
                if(checkBox.isChecked()){
                    songChecked.add(song);
                }else if(!checkBox.isChecked()){
                    songChecked.remove(song);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.thumbnail_addpl)
        ImageView thumbnail;
        @BindView(R.id.song_title_item_addpl)
        TextView title;
        @BindView(R.id.song_artist_item_addpl)
        TextView artist;
        @BindView(R.id.checkbox_add)
        CheckBox checkBox;
        ItemClickCheckbox itemClickCheckbox;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            checkBox.setOnClickListener(this);
        }

        public void setItemClick(ItemClickCheckbox itemClick){
            this.itemClickCheckbox = itemClick;

        }

        @Override
        public void onClick(View v) {
            this.itemClickCheckbox.itemClickCheck(v,getLayoutPosition());
        }
    }

    interface ItemClickCheckbox{
        void itemClickCheck(View view, int position);
    }
}

