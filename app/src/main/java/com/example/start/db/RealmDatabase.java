package com.example.start.db;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.start.data.Friend;
import com.example.start.data.Message;
import com.example.start.data.MessageGUID;
import com.example.start.data.User;
import com.example.start.data.UserInChat;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class RealmDatabase {
    private final RealmConfiguration mRealmConfiguration;
    private final Realm mRealm;

    public RealmDatabase(RealmConfiguration realmConfiguration){
        mRealmConfiguration = realmConfiguration;
        mRealm = Realm.getInstance(mRealmConfiguration);
    }
    public void insert(Message message){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealm(message);
            }
        });
    }
    public RealmResults<Message> getMessages(String sortField){
        return mRealm.where(Message.class).sort(sortField).findAll();
    }

    public void deleteById(long id){
        Message message = mRealm.where(Message.class).equalTo("id", id).findFirst();
        if (message == null)
            return;
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                message.deleteFromRealm();
            }
        });
    }
    public void clear(Class<? extends RealmModel> table){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.delete(table);
            }
        });
    }
    public void clearUser(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(Message.class).findFirst().deleteFromRealm();
            }
        });
    }
    public long getMessageNextId(){
        Number number = mRealm.where(Message.class).findAll().max("id");
        if (number == null)
            return 0;
        return number.longValue() + 1;
    }
    public void setUserDB(User user){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(user);
            }
        });

    }
    public User getUserDB(){
        return mRealm.where(User.class).findFirst();
    }
    public RealmResults<User> getUsersDB(){
        return mRealm.where(User.class).findAll();
    }
    public void signOut(){
        setUserDB(new User());

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(new User());
            }
        });
    }
    public void backToMainMenu(){
        Friend friend = mRealm.where(Friend.class).equalTo("Email", getChat().getChatEmail()).findFirst();
        mRealm.beginTransaction();
        friend.setLastMessage(getChat().getLastMessage());
        mRealm.commitTransaction();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(friend);
            }
        });
        setChat(new UserInChat());
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(new UserInChat());
            }
        });
    }
    public boolean checkUser(){
        if (mRealm.where(User.class).findFirst() == null){
            return false;
        }
        if (!mRealm.where(User.class).findFirst().getIsLoginned()){
            return false;
        }
        return true;
    }
    public boolean checkChat(){
        if (mRealm.where(UserInChat.class).findFirst() == null){
            return false;
        }
        if (mRealm.where(UserInChat.class).findFirst().getChatId() == null){
            return false;
        }
        return true;
    }
    public void setChat(UserInChat chat){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(chat);
            }
        });
    }
    public void clearChat(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(new UserInChat());
            }
        });
    }
    public void setChatLastMessage(String lastMessage) {
        UserInChat user = mRealm.where(UserInChat.class).findFirst();
        if (user != null) {
            mRealm.beginTransaction();
            user.setLastMessage(lastMessage);
            mRealm.commitTransaction();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.copyToRealmOrUpdate(user);
                }
            });
        }
    }
    public void addFriendDB(Friend friend){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealmOrUpdate(friend);
            }
        });

    }
    public Friend getFriendDB(String Email){
        return mRealm.where(Friend.class).equalTo("Email", Email).findFirst();
    }
    public RealmResults<Friend> getFriends(){
        return mRealm.where(Friend.class).findAll();
    }
    public UserInChat getChat(){
        return mRealm.where(UserInChat.class).findFirst();
    }
    public void setMessageGUID(String GUID){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(new MessageGUID(GUID));
            }
        });

    }
    public String getMessageGUID(){
        return mRealm.where(MessageGUID.class).findFirst().getGUID();
    }
}