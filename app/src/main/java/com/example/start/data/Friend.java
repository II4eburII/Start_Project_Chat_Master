package com.example.start.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Friend extends RealmObject {
    @PrimaryKey
    private String Email;
    private String name;
    private String id;
    private String lastMessage;
    public Friend(){}
    public Friend(String name, String Email, String id){
        this.name = name;
        this.Email = Email;
        this.id = id;
    }
    public String getFriendName(){
        return name;
    }
    public String getFriendEmail(){
        return Email;
    }
    public String getFriendId(){
        return id;
    }
    public String getLastMessage(){
        return lastMessage;
    }
    public void setLastMessage(String lastMessage){this.lastMessage = lastMessage;}
    public String getFriendNameLetter(){return name.substring(0, 1).toUpperCase();}
}
