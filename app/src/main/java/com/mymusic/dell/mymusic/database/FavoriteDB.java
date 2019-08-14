package com.mymusic.dell.mymusic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "SongFavorite";
    public static final String TABLE_NAME = "Favorite";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String IMAGE = "image";
    public static final String LINK = "link";

    public static final String CREATE_TABLEBOOK = "create table " + TABLE_NAME + "( " +
            ID + " int primary key, " +
            TITLE + " text, " +
            ARTIST + " text, " +
            IMAGE + " text, " +
            LINK + " text " +
            ")";

    private Context context;

    public FavoriteDB(Context context) {
        super(context, DB_NAME, null, 1);
        Log.e("DATABASE", "Success");
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLEBOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addSong(Song song) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, song.getId());
        values.put(TITLE, song.getTitle());
        values.put(ARTIST, song.getArtist());
        values.put(IMAGE, song.getThumbnail());
        values.put(LINK, song.getSongLink());

        database.insert(TABLE_NAME, null, values);
        database.close();
        Log.e("ADD", "success");
    }

    public ArrayList<Song> getListSong() {
        ArrayList<Song> songs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                song.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(ARTIST)));
                song.setThumbnail(cursor.getString(cursor.getColumnIndex(IMAGE)));
                song.setSongLink(cursor.getString(cursor.getColumnIndex(LINK)));
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }

    public void deleteBook(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + "=?", new String[]{String.valueOf(song.getId())});
        Log.e("DELETE", "DELETE");
        db.close();
    }

    public Song getSong(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID, TITLE, ARTIST, IMAGE, LINK}, ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        assert cursor != null;
        Song note = new Song(cursor.getInt(cursor.getColumnIndex(ID)), cursor.getString(cursor.getColumnIndex(TITLE)), cursor.getString(cursor.getColumnIndex(ARTIST)), cursor.getString(cursor.getColumnIndex(IMAGE)), cursor.getString(cursor.getColumnIndex(LINK)));
        cursor.close();
        db.close();
        return note;
    }

    public boolean checkFavorite(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}