package com.mymusic.dell.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.model.PlayList;

import java.util.ArrayList;

public class AdapterAddPL extends ArrayAdapter<PlayList> {
    LayoutInflater inflater;
    Context context;
    ArrayList<PlayList> playLists;
    public AdapterAddPL(Context context, int resource, ArrayList<PlayList> playLists) {
        super(context, resource,playLists);
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.playLists = playLists;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_pl_diaglog,null);
            holder.imagePL = convertView.findViewById(R.id.item_imagePL);
            holder.namePL = convertView.findViewById(R.id.namepl_dialog);
            holder.numSongPL = convertView.findViewById(R.id.numSong_dialog);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        PlayList playList = playLists.get(position);
        Glide.with(context).load(R.drawable.artsong).into(holder.imagePL);
        holder.namePL.setText(playList.getNameList()+"");
        holder.numSongPL.setText(playList.getNumSongs()+"");

        return convertView;
    }
    class Holder{
        ImageView imagePL;
        TextView namePL;
        TextView numSongPL;
    }
}
