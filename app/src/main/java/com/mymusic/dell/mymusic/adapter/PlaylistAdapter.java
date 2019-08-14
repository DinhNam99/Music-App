package com.mymusic.dell.mymusic.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.database.PlayListDB;
import com.mymusic.dell.mymusic.model.PlayList;
import com.mymusic.dell.mymusic.view.ActivityPlayList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.Holder> implements View.OnClickListener {

    Context context;
    ArrayList<PlayList> playLists;
    LayoutInflater inflater;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout edit, delete;
    ImageView songig;
    TextView tvNamePList, numberSong;
    int pos;
    PlayListDB playListDB;
    public PlaylistAdapter(Context context, ArrayList<PlayList> playLists){
        this.context = context;
        this.playLists = playLists;
        inflater = LayoutInflater.from(context);
        playListDB = new PlayListDB(context);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_playlist, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        final PlayList playList = playLists.get(i);
        if(playListDB.getAllPlaylistSongs(playList.getId()).size() == 0) {
            Glide.with(context).load(R.drawable.artsong).into(holder.imagePL);
        }else{
            Glide.with(context).load(playListDB.getAllPlaylistSongs(playList.getId()).get(0).getThumbnail()).into(holder.imagePL);
        }
        holder.tvNamePl.setText(playList.getNameList()+"");


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityPlayList.class);
                intent.putExtra("IDPL",playList.getId());
                intent.putExtra("NameList",playList.getNameList());
                intent.putExtra("numSongs",playList.getNumSongs());
                if(playListDB.getAllPlaylistSongs(playList.getId()).size() != 0) {
                    intent.putExtra("IMAGEPL",playListDB.getAllPlaylistSongs(playList.getId()).get(0).getThumbnail());
                }
                context.startActivity(intent);
            }
        });

        //diaglog upfile,photo...
        bottomSheetDialog = new BottomSheetDialog(context);
        View view = inflater.inflate(R.layout.diglog_option,null);
        bottomSheetDialog.setContentView(view);
        edit = view.findViewById(R.id.Edit);
        delete = view.findViewById(R.id.Delete);
        tvNamePList = view.findViewById(R.id.playlistname);
        numberSong = view.findViewById(R.id.numberSong);
        songig = view.findViewById(R.id.songig);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        holder.tvOpstions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = i;
                bottomSheetDialog.show();
                tvNamePList.setText(playList.getNameList()+"");
                numberSong.setText(playList.getNumSongs()+"");
                if(playListDB.getAllPlaylistSongs(playList.getId()).size() == 0) {
                    Glide.with(context).load(R.drawable.artsong).into(songig);
                }else{
                    Glide.with(context).load(playListDB.getAllPlaylistSongs(playList.getId()).get(0).getThumbnail()).into(songig);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        final PlayListDB playListDB = new PlayListDB(context);
        switch (id){
            case R.id.Edit:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                v = LayoutInflater.from(context).inflate(R.layout.dialog_add_playlist, null);
                alertDialog.setView(v);
                alertDialog.setTitle("Edit name");
                final AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(true);
                final EditText edName = v.findViewById(R.id.edName);
                Button btnOk = (Button) v.findViewById(R.id.btnOK);
                Button btnCancle = (Button) v.findViewById(R.id.btnCancle);
                edName.setText(playLists.get(pos).getNameList());
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayList list = new PlayList(edName.getText()+"","",0);
                        playListDB.update(pos+1,list);
                        playLists.set(pos,list);
                        notifyDataSetChanged();
                        alert.dismiss();
                        bottomSheetDialog.dismiss();
                    }
                });

                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
                alert.show();
                Toast.makeText(context, "Edit",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Delete:
                playListDB.deletePlist(playLists.get(pos));
                playLists.remove(pos);
                notifyDataSetChanged();
                bottomSheetDialog.dismiss();
                Toast.makeText(context, "Delete",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.imagePl)
        ImageView imagePL;
        @BindView(R.id.tvNamePL)
        TextView tvNamePl;
        @BindView(R.id.tvOptions)
        TextView tvOpstions;
        @BindView(R.id.layoutPLItem)
        RelativeLayout layout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
