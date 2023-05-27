package com.example.start.data;

import android.util.Log;

import com.google.gson.annotations.Expose;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Message extends RealmObject {
    @PrimaryKey
    private String guid;
    private String id_sender;
    private String id_getter;
    private String name;
    private String text;
    private long time;
    private String datetime;
    private int timezone;
    private boolean senderIsMe;
    public Message(String name, String text, long time, boolean senderIsMe, String id_sender, String id_getter){
        this.guid = String.valueOf(java.util.UUID.randomUUID());
        this.name = name;
        this.text = text;
        this.time = time;
        this.senderIsMe = senderIsMe;
        this.id_sender = id_sender;
        this.id_getter = id_getter;
        this.datetime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
        this.datetime = this.datetime.charAt(0) == "0".charAt(0)? this.datetime.substring(1) : this.datetime;
        this.timezone = TimeZone.getDefault().getOffset(System.currentTimeMillis())/3600;
    }
    public Message(Message message) {
        this(message.getName(), message.getText(), message.getTime(), message.getSenderIsMe(), message.getIdSender(), message.getIdGetter());
    }

    public Message() {

    }


    public String getName(){
        return name;
    }
    public String getText(){
        return text;
    }
    public long getTime(){return time;}
    public String getDateTime(){return datetime;}
    public int getTimezone(){return timezone;}
    public boolean getSenderIsMe(){
        return senderIsMe;
    }
    public void setSenderIsMe(boolean senderIsMe){
        this.senderIsMe = senderIsMe;
    }
    public String getIdSender() {
        return id_sender;
    }
    public String getIdGetter() {
        return id_getter;
    }
    public String getGuid(){return guid;}

    @Override
    public String toString() {
        return "Message{" +
                "guid='" + guid + '\'' +
                ", id_sender='" + id_sender + '\'' +
                ", id_getter='" + id_getter + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", datetime='" + datetime + '\'' +
                ", timezone=" + timezone +
                ", senderIsMe=" + senderIsMe +
                '}';
    }
}

