package com.example.lenovo.uploadpic.MediaAdapter;

import android.content.Intent;

import java.util.ArrayList;

public class MediaObject {

    private int id;
    private String ImageName;
    private String ImagePath;
    private String Filetype;
    private int agree;
    private String username;
    private String profilepic;
    private String address;
    private int comments;
    private ArrayList<Integer> image_liked;

    public MediaObject(int id, String ImageName, String ImagePath, String Filetype, int agree, String username, String profilepic,String address, int comments){

        this.id=id;
        this.ImageName= ImageName;
        this.ImagePath= ImagePath;
        this.Filetype= Filetype;
        this.agree=agree;
        this.username = username;
        this.profilepic=profilepic;
        this.address=address;
        this.comments=comments;
    }

    public void setImage_liked(ArrayList<Integer> i){
        this.image_liked = i;
    }

    public String getImageName(){
        return  this.ImageName;
    }

    public String getImagePath() {
        return this.ImagePath;
    }

    public String getFiletype() {return this.Filetype;}

    public int getAgree() {return this.agree;}

    public int getId(){return this.id;}

    public String getUsername(){return this.username;}

    public String getProfilepic(){return this.profilepic;}

    public String getAddress() {
        return address;
    }

    public int getComments() {
        return comments;
    }

    public ArrayList<Integer> getImage_liked(){ return image_liked; }
}
