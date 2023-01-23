package com.harry9425.yaari;

public class postmodel {

    String url, caption, postuid, location, postby;
    Long time;

    public postmodel(String uid) {
        this.url = url;
    }

    public postmodel() {
    }

    public String getPostby() {
        return postby;
    }

    public void setPostby(String postby) {
        this.postby = postby;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostuid() {
        return postuid;
    }

    public void setPostuid(String postuid) {
        this.postuid = postuid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}