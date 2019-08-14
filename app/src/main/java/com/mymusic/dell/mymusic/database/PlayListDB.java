package com.mymusic.dell.mymusic.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ListView;

import com.mymusic.dell.mymusic.model.PlayList;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayListDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "PLAYLIST";
    private static final String TABLE_PL = "playlist";
    private static final String NAMEPL ="NamePL";
    private static final String IMAGEPL = "imagePL";
    private static final String NUMSONGS = "numberSongs";
    private static final String CREATE_TABLEPL = "create table "+TABLE_PL+" ( ID INTEGER PRIMARY KEY  AUTOINCREMENT, "+
                                NAMEPL + " varchar(20), "+
                                IMAGEPL + " varchar(100))";

    private static final String TABLE_PLAYLIST_SONGS = "playlistSongs";
    private static final String SONG_KEY_ID = "song_id";
    private static final String SONG_KEY_REAL_ID = "song_real_id";
    private static final String SONG_KEY_PLAYLIST_ID = "song_playlist_id";
    String CREATE_PLAYLIST_SONG_TABLE = "CREATE TABLE playlistSongs (" +
            "song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "song_real_id INTEGER," +
            "song_playlist_id INTEGER)";

    private static Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private Context context;
    public PlayListDB( Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_PLAYLIST_SONG_TABLE);
        db.execSQL(CREATE_TABLEPL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS playlistSongs");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PL);
        onCreate(db);
    }

    public void createPlaylist(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull("ID");
        values.put(NAMEPL, name);

        db.insert(TABLE_PL, null, values);
        db.close();
    }

    public ArrayList<PlayList> getAllPlaylist() {
        String query = "SELECT  * FROM " + TABLE_PL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<PlayList> playlist = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                playlist.add(getPlaylistFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        db.close();
        return playlist;
    }


    private PlayList getPlaylistFromCursor(Cursor cursor) {
        int playlistId = Integer.parseInt(cursor.getString(0));
        return new PlayList(playlistId, cursor.getString(1),cursor.getString(2),
                (int)getPlaylistSongCount(playlistId));
    }
    public int update(int id,PlayList playList){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAMEPL,playList.getNameList());

        return db.update(TABLE_PL,values, "ID =?",new String[] { String.valueOf(id)});


    }

    public void deletePlist(PlayList list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PL, NAMEPL + "=?", new String[]{String.valueOf(list.getNameList())});
        db.delete( TABLE_PLAYLIST_SONGS, SONG_KEY_PLAYLIST_ID + "=?" ,new String[]{String.valueOf(list.getNameList())});
        db.close();
    }

    public void addSong(int song, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SONG_KEY_REAL_ID, song);
        values.put(SONG_KEY_PLAYLIST_ID, playlistId);

        db.insert(TABLE_PLAYLIST_SONGS, null, values);
        db.close();
    }

    public void addSong(ArrayList<Integer> songs, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < songs.size(); i++) {
            ContentValues values = new ContentValues();
            values.putNull(SONG_KEY_ID);
            values.put(SONG_KEY_REAL_ID, songs.get(i));
            values.put(SONG_KEY_PLAYLIST_ID, playlistId);

            db.insert(TABLE_PLAYLIST_SONGS, null, values);
        }
        db.close();
    }
    public void removeSong(int songId, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYLIST_SONGS + " WHERE " +
                SONG_KEY_REAL_ID + "='" + songId + "' AND "
                + SONG_KEY_PLAYLIST_ID + "='" + playlistId + "'");
        db.close();
    }

    public ArrayList<Song> getAllPlaylistSongs(int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Integer> songsIds = getAllPlaylistSongsIds(db, playlistId);

        ArrayList<Song> songList = new ArrayList<>();
        System.gc();
        for (int i = 0; i < songsIds.size(); i++) {
            final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " +
                    MediaStore.Audio.Media._ID + "=" + songsIds.get(i).toString();
            Cursor musicCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, where, null, null);
            if (musicCursor != null && musicCursor.moveToFirst()) {
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                int pathColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DATA);
                int albumIdColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM_ID);
                do {
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    Uri thisSongLink = Uri.parse(musicCursor.getString(pathColumn));
                    long some = musicCursor.getLong(albumIdColumn);
                    Uri uri = ContentUris.withAppendedId(sArtworkUri, some);
                    songList.add(new Song(thisId, thisTitle, thisArtist, uri.toString(),
                            thisSongLink.toString()));
                }
                while (musicCursor.moveToNext());
            }
            if (musicCursor != null)
                musicCursor.close();
        }
        db.close();
        return songList;
    }
    private ArrayList<Integer> getAllPlaylistSongsIds(SQLiteDatabase db, int playlistId) {
        String query = "SELECT  * FROM " + TABLE_PLAYLIST_SONGS + " WHERE "
                + SONG_KEY_PLAYLIST_ID + "='" + playlistId + "'";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> songsId = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                songsId.add(Integer.parseInt(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return songsId;
    }
    public long getPlaylistSongCount(int playlistId) {
        String query = "select count(*) from " + TABLE_PLAYLIST_SONGS + " where "
                + SONG_KEY_PLAYLIST_ID + "='" + playlistId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement s = db.compileStatement(query);
        long count = s.simpleQueryForLong();
        db.close();
        return count;
    }
}
