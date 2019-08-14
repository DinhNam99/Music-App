package com.mymusic.dell.mymusic.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.adapter.PlaylistAdapter;
import com.mymusic.dell.mymusic.database.PlayListDB;
import com.mymusic.dell.mymusic.model.PlayList;
import com.mymusic.dell.mymusic.model.Song;
import com.mymusic.dell.mymusic.view.ActivityAddSong;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistFragment extends Fragment {

    @BindView(R.id.rcPl)
    RecyclerView rcPlaylist;

    @BindView(R.id.faAdd)
    FloatingActionButton faAdd;

    PlaylistAdapter adapter;
    ArrayList<PlayList> playLists;
    PlayListDB playListDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragplaylist, container, false);
        ButterKnife.bind(this, view);
        init();
        getData();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playListDB = new PlayListDB(getActivity());

    }

    private void init() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2,GridLayoutManager.VERTICAL,false);
        rcPlaylist.setLayoutManager(manager);

        faAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                v = getLayoutInflater().inflate(R.layout.dialog_add_playlist, null);
                alertDialog.setView(v);
                alertDialog.setTitle("Add Playlist");
                final AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(true);
                final EditText edName = v.findViewById(R.id.edName);
                Button btnOk = (Button) v.findViewById(R.id.btnOK);
                Button btnCancle = (Button) v.findViewById(R.id.btnCancle);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playListDB.createPlaylist(edName.getText()+"");
                        playLists.add(new PlayList(edName.getText()+""));
                        adapter.notifyDataSetChanged();
                        alert.dismiss();
                        Intent intent = new Intent(getActivity(), ActivityAddSong.class);
                        startActivity(intent);
                    }
                });

                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
                alert.show();
            }
        });

    }

    private void NotifyDataChanged(){
        adapter.notifyDataSetChanged();
    }
    private void getData() {
        playLists = new ArrayList<>();
        playLists = (ArrayList<PlayList>) playListDB.getAllPlaylist();

        adapter = new PlaylistAdapter(getContext(), playLists);
        rcPlaylist.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        playListDB = new PlayListDB(getActivity());
        playLists = (ArrayList<PlayList>) playListDB.getAllPlaylist();
        adapter = new PlaylistAdapter(getContext(), (ArrayList<PlayList>) playLists);
        rcPlaylist.setAdapter(adapter);
        super.onResume();
    }
}
