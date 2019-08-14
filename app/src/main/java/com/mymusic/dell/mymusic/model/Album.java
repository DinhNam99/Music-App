package com.mymusic.dell.mymusic.model;

public class Album {
    private String nameAlbum;
    private int idAlbum;
    private String nameArtist;
    private String numSong;
    private String imageAlbum;


    public Album(String nameAlbum, String nameArtist, String numSong, String imageAlbum, int idAlbum) {
        this.nameAlbum = nameAlbum;
        this.nameArtist = nameArtist;
        this.numSong = numSong;
        this.imageAlbum = imageAlbum;
        this.idAlbum = idAlbum;
    }

    public String getImageAlbum() {
        return imageAlbum;
    }

    public void setImageAlbum(String imageAlbum) {
        this.imageAlbum = imageAlbum;
    }

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.nameAlbum = nameAlbum;
    }

    public String getNameArtist() {
        return nameArtist;
    }

    public void setNameArtist(String nameArtist) {
        this.nameArtist = nameArtist;
    }

    public String getNumSong() {
        return numSong;
    }

    public void setNumSong(String numSong) {
        this.numSong = numSong;
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }
}
