package com.harry9425.yaari;

public class requestmodelclass {

    String name,uid,userdp,type,profile;

    public requestmodelclass(String uid) {
        this.uid = uid;
    }
    public requestmodelclass() {}

    public String getType() {
        return type;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserdp() {
        return userdp;
    }

    public void setUserdp(String userdp) {
        this.userdp = userdp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
