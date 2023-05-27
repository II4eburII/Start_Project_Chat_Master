package com.example.start.ui.main.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.start.MyApp;
import com.example.start.data.User;
import com.example.start.db.RealmDatabase;

public class LoginViewModel extends AndroidViewModel {
    private final RealmDatabase mDatabase;
    private User user;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        mDatabase = ((MyApp)application).getDatabase();

    }
    public void setUser(User user){
        mDatabase.setUserDB(user);
        this.user = user;
    }
    public User getUser(){
        return user;
    }
    public void signOut(){
        mDatabase.checkUser();
    }
}
