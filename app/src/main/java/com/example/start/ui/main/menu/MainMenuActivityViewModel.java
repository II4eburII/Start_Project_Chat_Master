package com.example.start.ui.main.menu;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.start.MyApp;
import com.example.start.data.Friend;
import com.example.start.data.Post;
import com.example.start.data.UserInChat;
import com.example.start.db.RealmDatabase;
import com.example.start.network.Network;
import com.example.start.ui.main.main.MainActivityViewModel;
import com.example.start.ui.main.menu.FriendsAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuActivityViewModel extends AndroidViewModel {
    private final RealmDatabase mDatabase;
    private FriendsAdapter mAdapterFriends;
    private MainActivityViewModel viewModel;
    public MainMenuActivityViewModel(@NonNull Application application) {
        super(application);

        mDatabase = ((MyApp)application).getDatabase();

        mAdapterFriends = new FriendsAdapter(mDatabase.getFriends());

        //mDatabase.clear(Message.class);
    }
    public FriendsAdapter getAdapter() {
        return mAdapterFriends;
    }
    public void addFriend(Friend friend){
        mDatabase.addFriendDB(friend);
    }
    public void deleteMessage(Friend friend){
        //mDatabase.deleteById(message.getId());
    }
    public Friend getContextClickOperation(){
        return mAdapterFriends.getContextClickOperation();
    }
}