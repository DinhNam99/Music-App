package com.mymusic.dell.mymusic.adapter;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mymusic.dell.mymusic.R;
import com.mymusic.dell.mymusic.database.FavoriteDB;
import com.mymusic.dell.mymusic.database.PlayListDB;
import com.mymusic.dell.mymusic.fragment.HomeFragment;
import com.mymusic.dell.mymusic.model.PlayList;
import com.mymusic.dell.mymusic.model.Song;
import com.mymusic.dell.mymusic.service.MusicService;
import com.mymusic.dell.mymusic.view.PlayActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> implements View.OnClickListener {
    private ArrayList<Song> mSongList;
    private Context mContext;

    LayoutInflater inflater;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout addPL, addFV;
    ImageView igSong;
    TextView tvNameSong,txtFV, artistOptions;
    int pos;
    PlayListDB playListDB;
    FavoriteDB favoriteDB;
    private Intent playIntent;
    private boolean musicBound = false;
    MusicService musicService;


    public SongAdapter(Context context, ArrayList<Song> songList) {
        this.mContext = context;
        this.mSongList = songList;
        inflater = LayoutInflater.from(context);
        favoriteDB = new FavoriteDB(context);
        playListDB = new PlayListDB(context);
        if (playIntent == null) {
            playIntent = new Intent(context, MusicService.class);
            context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            context.startService(playIntent);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Song song = mSongList.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        Glide.with(mContext).load(song.getThumbnail()).placeholder(R.drawable
                .artsong).error(R.drawable.artsong)
                .into(holder.thumbnail);


        //diaglog upfile,photo...
        bottomSheetDialog = new BottomSheetDialog(mContext);
        View view = inflater.inflate(R.layout.option_songs,null);
        bottomSheetDialog.setContentView(view);
        addPL = view.findViewById(R.id.AddToPL);
        addFV = view.findViewById(R.id.FVOptions);
        txtFV = view.findViewById(R.id.txtFV);
        if (favoriteDB.checkFavorite((int) song.getId())) {
            txtFV.setText("Remove from favorite");

        } else {
            txtFV.setText("Add to favorite");
        }
        tvNameSong = view.findViewById(R.id.nameSongOption);
        artistOptions = view.findViewById(R.id.artistOptions);
        igSong = view.findViewById(R.id.imagesongOptions);
        addPL.setOnClickListener(this);
        addFV.setOnClickListener(this);
        holder.tvOptionSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
                tvNameSong.setText(song.getTitle()+"");
                artistOptions.setText(song.getArtist()+"");
                Glide.with(mContext).load(song.getThumbnail()).placeholder(R.drawable
                        .artsong).error(R.drawable.artsong)
                        .into(igSong);
                pos = position;
            }
        });

        holder.view_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayActivity.class);
                intent.putExtra("POS",position);
                intent.putExtra("SONGS",mSongList);
                //favorite
                boolean isFV = false;
                if (favoriteDB.checkFavorite((int) mSongList.get(position).getId())) {
                    isFV = true;
                } else {
                    isFV = false;
                }
                intent.putExtra("FV",isFV);
                mContext.startActivity(intent);
                //MusicService musicService = ((MainActivity)getActivity()).getMusicService();
                musicService.setSong(position);
                musicService.togglePlay();
                Log.e("ID",song.getId()+"");
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.AddToPL:
                showDialogAddPL();
                Toast.makeText(mContext, "Add to playlist",Toast.LENGTH_SHORT).show();
                break;
            case R.id.FVOptions:
                Song song = mSongList.get(pos);
                if (favoriteDB.checkFavorite((int) song.getId())) {
                    txtFV.setText("Remove from favorite");
                    favoriteDB.deleteBook(song);
                    mSongList.remove(pos);
                    notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                    Toast.makeText(mContext, "The song has been removed from the favorites list.",Toast.LENGTH_SHORT).show();
                } else {
                    favoriteDB.addSong(song);
                    bottomSheetDialog.dismiss();
                    txtFV.setText("Add to favorite");
                    Toast.makeText(mContext, "The song has been added to the favorites list.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.song_title_item)
        public TextView title;
        @BindView(R.id.song_artist_item)
        TextView artist;
        @BindView(R.id.tvOptionSong)
        TextView tvOptionSong;
        @BindView(R.id.thumbnail)
        public ImageView thumbnail;
        @BindView(R.id.view_song)
        RelativeLayout view_song;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }


    // Connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            // Get service
            musicService = binder.getService();
            // Pass song list
            musicService.setSongs(mSongList);

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


    public void showDialogAddPL(){
        final ArrayList<PlayList> playListDialog = (ArrayList<PlayList>) playListDB.getAllPlaylist();
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final LayoutInflater inflaterDiaplog = LayoutInflater.from(mContext);

        int posPl=0;

        View convertView = (View) inflaterDiaplog.inflate(R.layout.dialog_playlist, null);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }

                });

        // add custom view in dialog

        builder.setView(convertView);

        ListView lv = (ListView) convertView.findViewById(R.id.listPLdialog);

        final AlertDialog alert = builder.create();

        final AdapterAddPL myadapter = new AdapterAddPL(mContext, R.layout.item_pl_diaglog, playListDialog);

        lv.setAdapter(myadapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                // TODO Auto-generated method stub
                playListDB.addSong((int)mSongList.get(pos).getId(),playListDialog.get(position).getId());
                Toast.makeText(mContext, "Added to playlist",Toast.LENGTH_SHORT).show();
                alert.cancel();
            }

        });

        RelativeLayout layoutCreat = convertView.findViewById(R.id.layout_add_pl);
        layoutCreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

                v = inflater.inflate(R.layout.dialog_add_playlist, null);
                alertDialog.setView(v);
                alertDialog.setTitle("Add Playlist");
                final AlertDialog alertCreat = alertDialog.create();
                alert.setCanceledOnTouchOutside(true);
                final EditText edName = v.findViewById(R.id.edName);
                Button btnOk = (Button) v.findViewById(R.id.btnOK);
                Button btnCancle = (Button) v.findViewById(R.id.btnCancle);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playListDB.createPlaylist(edName.getText()+"");

                        ArrayList<PlayList> newplayListDialog = (ArrayList<PlayList>) playListDB.getAllPlaylist();
                        playListDB.addSong((int)mSongList.get(pos).getId(),newplayListDialog.get(newplayListDialog.size()-1).getId());
                        Toast.makeText(mContext, "Added to playlist",Toast.LENGTH_SHORT).show();
                        alertCreat.dismiss();
                        alert.dismiss();
                        bottomSheetDialog.dismiss();
                    }
                });

                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertCreat.dismiss();
                    }
                });
                alertCreat.show();
            }
        });
        // show dialog

        alert.show();

    }
}
