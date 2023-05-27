package com.example.start.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageGUID extends RealmObject {
    @PrimaryKey
    private int key = 1;
    private String GUID;
    public MessageGUID(){}
    public MessageGUID(String GUID){
        this.GUID = GUID;
    }
    public String getGUID(){return GUID;}
    public void setGUID(String GUID){this.GUID = GUID;}

}
