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
    private String idSender;
    private String idGetter;
    private String name;
    private String text;
    private long time;
    private String dateTime;
    private int timezone;
    private boolean senderIsMe;
    public Message(String name, String text, long time, boolean senderIsMe, String idSender, String idGetter){
        this.guid = String.valueOf(java.util.UUID.randomUUID());
        this.name = name;
        this.text = text;
        this.time = time;
        this.senderIsMe = senderIsMe;
        this.idSender = idSender;
        this.idGetter = idGetter;
        this.dateTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
        this.dateTime = this.dateTime.charAt(0) == "0".charAt(0)? this.dateTime.substring(1) : this.dateTime;
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
    public void setText(String text){
        this.text = text;
    }
    public long getTime(){return time;}
    public String getDateTime(){return dateTime;}
    public int getTimezone(){return timezone;}
    public boolean getSenderIsMe(){
        return senderIsMe;
    }
    public void setSenderIsMe(boolean senderIsMe){
        this.senderIsMe = senderIsMe;
    }
    public String getIdSender() {
        return idSender;
    }
    public String getIdGetter() {
        return idGetter;
    }
    public String getGuid(){return guid;}
    public void generateGuid(){guid = String.valueOf(java.util.UUID.randomUUID());}

    @Override
    public String toString() {
        return "Message{" +
                "guid='" + guid + '\'' +
                ", id_sender='" + idSender + '\'' +
                ", id_getter='" + idGetter + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", datetime='" + dateTime + '\'' +
                ", timezone=" + timezone +
                ", senderIsMe=" + senderIsMe +
                '}';
    }
}

