package com.harry9425.yaari;

public class commentclass {
    String uname,message,dp,uid,commentid,type,outid;
    Long date; int likes;
    int seemore;

    public commentclass() {}

    public commentclass(String message, String uid, String commentid, Long date, int likes) {
        this.message = message;
        this.uid = uid;
        this.commentid = commentid;
        this.date = date;
        this.likes = likes;
    }

    public int getSeemore() {
        return seemore;
    }

    public void setSeemore(int seemore) {
        this.seemore = seemore;
    }

    public String getOutid() {
        return outid;
    }

    public void setOutid(String outid) {
        this.outid = outid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
