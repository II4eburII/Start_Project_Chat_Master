package com.example.start.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private String id = "1";
    private String user_id;
    private String info;
    private boolean isLoginned;
    public User(String info){
        user_id = info.split(",")[3];
        this.info = info;
        isLoginned = true;
    }
    public User(){
    }
    public String getInfo(){
        return info;
    }
    public void setInfo(String info){
        user_id = info.split(",")[3];
        this.info = info;
        isLoginned = true;
    }
    public void signOut(){
        user_id = "";
        info = "";
        isLoginned = false;
    }
    public boolean getIsLoginned(){
        return isLoginned;
    }
    public String getUserId(){return user_id;}
    public void setUserId(String id){this.user_id = id;}
}
