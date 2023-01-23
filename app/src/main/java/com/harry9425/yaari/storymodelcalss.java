package com.harry9425.yaari;

public class storymodelcalss {

    int status;
    int count;
    String userdp;
    String uid;
    String imageurl;
    String username;
    String ismusic;
    String postuid;
    String musicurl;
    Long time;
    int initime;
    int endtime;
    int duration;

    public storymodelcalss(String imageurl, String userdp, String uid) {
        this.userdp = userdp;
        this.imageurl=imageurl;
        this.uid = uid;
    }

    public storymodelcalss(){}

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPostuid() {
        return postuid;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setPostuid(String postuid) {
        this.postuid = postuid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getIsmusic() {
        return ismusic;
    }

    public void setIsmusic(String ismusic) {
        this.ismusic = ismusic;
    }

    public String getMusicurl() {
        return musicurl;
    }

    public void setMusicurl(String musicurl) {
        this.musicurl = musicurl;
    }

    public int getInitime() {
        return initime;
    }

    public void setInitime(int initime) {
        this.initime = initime;
    }

    public int getEndtime() {
        return endtime;
    }

    public void setEndtime(int endtime) {
        this.endtime = endtime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserdp() {
        return userdp;
    }

    public void setUserdp(String userdp) {
        this.userdp = userdp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
