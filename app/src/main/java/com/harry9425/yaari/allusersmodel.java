package com.harry9425.yaari;

public class allusersmodel {


    String name;
    String phoneno;
    String status;
    String userdp;
    String uid;
    String username;
    String profile;
    String roomselect;

    public allusersmodel(String name, String phoneno, String status, String userdp, String uid,String profile) {
        this.name = name;
        this.phoneno = phoneno;
        this.status = status;
        this.userdp = userdp;
        this.uid = uid;
        this.profile=profile;
    }

    public allusersmodel(){}

    public String getRoomselect() {
        return roomselect;
    }

    public void setRoomselect(String roomselect) {
        this.roomselect = roomselect;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
