package com.example.start.ui.main.main;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.start.MyApp;
import com.example.start.data.Message;
import com.example.start.data.User;
import com.example.start.data.UserInChat;
import com.example.start.db.RealmDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityViewModel extends AndroidViewModel {
    private final RealmDatabase mDatabase;
    private MessagesAdapter mAdapter;
    private String UserId;
    private String ChatId;
    private UserInChat user;
    private MutableLiveData<String> liveDataString;
    private ValueEventListener valueEventListener;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mDatabase = ((MyApp)application).getDatabase();
        mAdapter = new MessagesAdapter(mDatabase.getMessages(((MyApp)application).getUser().getUserId(), ((MyApp)application).getChat().getChatId()));
        UserId = mDatabase.getUserDB().getUserId();
        ChatId = mDatabase.getChat().getChatId();
        Log.d("MainActivity", UserId + "//" + ChatId);
        setListeningFireBase();


    }
    public MutableLiveData<String> setMessageChange() {
        if (liveDataString == null) {
            liveDataString = new MutableLiveData<String>();
        }

        return liveDataString;
    }
    public void setListeningFireBase(){
        if (((MyApp) getApplication()).updateDataBaseListening()){return;}
        Log.d("MainActivity", "Success Listening");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {return;}
                Log.d("MainActivity", "In firebase");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MainActivity", "add message");
                    Message message = snapshot.getValue(Message.class);
                    dataSnapshot.getRef().getParent().child(message.getGuid()).setValue(message);
                    snapshot.getRef().removeValue();
                    message.setSenderIsMe(!message.getSenderIsMe());
                    message.generateGuid();
                    addMessage(message);
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("MainActivity", "Failed to read app title value.", error.toException());
            }
        };
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserId).child("NewMessages").addValueEventListener(valueEventListener);
    }
    public void removeDataBaseListening(){
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserId).child("NewMessages").removeEventListener(valueEventListener);
    }
    public MessagesAdapter getAdapter() {
        return mAdapter;
    }
    public void addMessage(Message message){
        setMessageChange().setValue(java.util.UUID.randomUUID().toString());
        Log.d("MainActivity", "send message");
        mDatabase.setChatLastMessage(message.getText());
        mDatabase.insert(message);

    }
    public void deleteMessage(Message message){
        Message deletedMessage = message;

        deletedMessage.setText("*Сообщение удалено*");
        addMessage(deletedMessage);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Messages")
                .child(ChatId)
                .child("NewMessages")
                .child(String.valueOf(message.getGuid())).setValue(deletedMessage);
    }
    public void changeMessage(Message message, String text){
        Message changedMessage = message;

        changedMessage.setText(text);
        addMessage(changedMessage);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Messages")
                .child(ChatId)
                .child("NewMessages")
                .child(String.valueOf(message.getGuid())).setValue(changedMessage);
    }
    public Message getContextClickOperation(){
        return mAdapter.getContextClickOperation();
    }
    public void setChat(UserInChat user){
        this.user = user;
    }
    public UserInChat getChat(){
        return user;
    }
}