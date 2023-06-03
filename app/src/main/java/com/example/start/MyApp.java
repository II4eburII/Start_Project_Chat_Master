package com.example.start;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.start.data.Friend;
import com.example.start.data.User;
import com.example.start.data.UserInChat;
import com.example.start.db.RealmDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MyApp extends Application {
    private RealmDatabase mDatabase;
    private boolean isDataBaseListening;
    private String name;
    private String email;
    private String id;
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        mDatabase = new RealmDatabase(
                new  RealmConfiguration.Builder().
                        name(Realm.DEFAULT_REALM_NAME).
                        schemaVersion(0).
                        allowQueriesOnUiThread(true).
                        allowWritesOnUiThread(true).
                        deleteRealmIfMigrationNeeded().
                        build());

    }
    public boolean updateDataBaseListening(){
        boolean currentValue = isDataBaseListening;
        Log.d("MainActivity", "FireBaseListening = " + String.valueOf(isDataBaseListening));
        isDataBaseListening = true;
        return currentValue;
    }
    public void resetDataBaseListening(){
        isDataBaseListening = false;
    }
    public User getUser() {
        return mDatabase.getUserDB();
    }

    public void setUser(User user) {
        mDatabase.setUserDB(user);
    }
    public void signOut(){
        mDatabase.signOut();
    }
    public void backToMainMenu(){
        mDatabase.backToMainMenu();
    }
    public RealmDatabase getDatabase(){
        return mDatabase;
    }
    public boolean checkUser(){return mDatabase.checkUser();}
    public boolean checkChat(){return mDatabase.checkChat();}
    private User user;
    public void addFriend(String Email){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore.getInstance().collection("client").document(Email)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                user = new User(documentSnapshot.getData().values().stream().map(Object::toString).collect(Collectors.joining(",")));
                                String[] userInfo = user.getInfo().split(",");
                                Log.d("MainActivity", user.getInfo());
                                for (Object info : documentSnapshot.getData().values().stream().map(Object::toString).collect(Collectors.joining(",")).split(",")){
                                    if (info.toString().contains("NAME-")){
                                        name = info.toString().replace("NAME-", "");
                                    } else if (info.toString().contains("EMAIL-")){
                                        email = info.toString().replace("EMAIL-", "");
                                    } else if (info.toString().contains("ID-")){
                                        id = info.toString();
                                    }

                                }
                                Log.d("MainActivity", name + " "  + email + " " + id);
                                Friend friend = new Friend(name, email, id);
                                mDatabase.addFriendDB(friend);
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {}
                        });
            }
        }).start();
    }
    public Friend getFriend(String Email){return mDatabase.getFriendDB(Email);}
    public RealmResults<User> getUsers(){
        if (mDatabase.getUsersDB() == null){
            Log.d("MainActivity", "null");
            return null;
        } else {
            Log.d("MainActivity", "not null");
            return mDatabase.getUsersDB();
        }
    }
    public void setChat(UserInChat user){
        mDatabase.setChat(user);
    }
    public UserInChat getChat(){
        return mDatabase.getChat();
    }
    public void setLastMessage(String lastMessage){mDatabase.setChatLastMessage(lastMessage);}
    public void setMessageGUID(String GUID){
        mDatabase.setMessageGUID(GUID);
    }
    public String getMessageGUID(){
        return mDatabase.getMessageGUID();
    }
    public void clearAll(){mDatabase.clearAll();}
}
