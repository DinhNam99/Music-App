package com.mymusic.dell.mymusic.model;

public class PlayList {
    private int id;
    private String nameList;
    private String imageList;
    private int numSongs;

    public PlayList() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public PlayList(int id, String nameList, String imageList, int numSongs) {
        this.id = id;
        this.nameList = nameList;
        this.imageList = imageList;
        this.numSongs = numSongs;
    }

    public PlayList(String nameList) {
        this.nameList = nameList;
    }

    public PlayList(String nameList, String imageList, int numSongs) {
        this.nameList = nameList;
        this.imageList = imageList;
        this.numSongs = numSongs;
    }

    public int getNumSongs() {
        return numSongs;
    }

    public void setNumSongs(int numSongs) {
        this.numSongs = numSongs;
    }

    public String getImageList() {
        return imageList;
    }

    public String getNameList() {
        return nameList;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }
}
