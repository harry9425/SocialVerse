package com.harry9425.yaari;



public class chatmodel{

    String name,lastmess,userdp,uid,time,isgroup;

    public chatmodel(String uid) {
        this.uid = uid;
    }

    public chatmodel() {}

    public String getTime() {
        return time;
    }

    public String getIsgroup() {
        return isgroup;
    }

    public void setIsgroup(String isgroup) {
        this.isgroup = isgroup;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getLastmess() {
        return lastmess;
    }

    public void setLastmess(String lastmess) {
        this.lastmess = lastmess;
    }
}
