package com.example.start.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserInChat extends RealmObject {
    @PrimaryKey
    private int key = 1;
    private String Email;
    private String name;
    private String id;
    private String lastMessage;
    private String FriendEmail;
    public UserInChat(){}
    public UserInChat(String name, String Email, String id, String FriendEmail){
        this.name = name;
        this.Email = Email;
        this.id = id;
        this.FriendEmail = FriendEmail;
    }
    public String getChatName(){
        return name;
    }
    public String getChatEmail(){
        return Email;
    }
    public String getChatId(){
        return id;
    }
    public String getLastMessage(){
        return lastMessage;
    }
    public void setLastMessage(String lastMessage){this.lastMessage = lastMessage;}
    public void setFriendEmail(String FriendEmail){this.FriendEmail = FriendEmail;}
    public String getFriendEmail(){return FriendEmail;}
}
