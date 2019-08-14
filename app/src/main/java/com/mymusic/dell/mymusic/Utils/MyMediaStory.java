package com.mymusic.dell.mymusic.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.mymusic.dell.mymusic.model.Album;
import com.mymusic.dell.mymusic.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyMediaStory {

    private static Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    ContentResolver contentResolver;

    public MyMediaStory(Context context){
        contentResolver = context.getContentResolver();
    }

    public ArrayList<Song> getSong(){
        ArrayList<Song> mSongList = new ArrayList<>();
        //retrieve item_song info

        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = contentResolver.query(musicUri, null, null, null, null);
        return getDataMedia(musicCursor);
    }

    public ArrayList<Song> getSongByAlbum(int albumId) {
        String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
        String[] selctionAgr = {albumId + ""};
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, selection, selctionAgr, null);

        return getDataMedia(cursor);

    }
    public ArrayList<Song> getDataMedia(Cursor musicCursor) {
        ArrayList<Song> mSongList = new ArrayList<>();

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int albumID = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int songLink = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Uri thisSongLink = Uri.parse(musicCursor.getString(songLink));
                long some = musicCursor.getLong(albumID);
                Uri uri = ContentUris.withAppendedId(sArtworkUri, some);
                mSongList.add(new Song(thisId, thisTitle, thisArtist, uri.toString(),
                        thisSongLink.toString()));
            }
            while (musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        // Sort music alphabetically

        return mSongList;
    }
    public  long getIdSong(String title){
        long id = 0;
        for(int i = 0; i < getSong().size();i++){
            if(title.equals(getSong().get(i).getTitle())){
                id = getSong().get(i).getId();
            }
        }
        return id;
    }

    public ArrayList<Album> getAlbumData() {
        ArrayList<Album> arr = new ArrayList<>();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null,
                null, null, null);

        cursor.moveToFirst();


        int indexAlbum = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
        int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
        int indexNumSong = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
        int indexImage = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
        int indexId = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
        while (cursor.isAfterLast() == false) {
            String nameAlbum = cursor.getString(indexAlbum);
            String artist = cursor.getString(indexArtist);
            int numSong = cursor.getInt(indexNumSong);
            String nOfSong = String.valueOf(numSong);
            String image = cursor.getString(indexImage);
            int id = cursor.getInt(indexId + 1);
            Album album1 = new Album(nameAlbum, artist, nOfSong, image, id);
            arr.add(album1);
            cursor.moveToNext();
        }
        return arr;
    }
}
