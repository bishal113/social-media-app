package com.example.lenovo.uploadpic.comment;

/**
 * Created by Lenovo on 12-06-2018.
 */

public class CommentObject {
    String username,comment;

    public CommentObject(String username,String comment){
        this.username=username;
        this.comment=comment;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }
}
