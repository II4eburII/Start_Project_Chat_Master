package com.example.start.ui.main.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.start.MyApp;
import com.example.start.data.Message;
import com.example.start.R;
import com.example.start.data.User;
import com.example.start.databinding.ActivityMainBinding;
import com.example.start.ui.main.login.RegistrationActivity;
import com.example.start.ui.main.menu.MainMenuActivity;
import com.example.start.ui.main.menu.MainMenuActivityViewModel;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel viewModel;
    private ActivityMainBinding binding;
    private DatabaseReference mDatabase;
    private MyApp app;
    //snackbar - livedata / доделать friend

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        app = ((MyApp) getApplicationContext());
        checkCurrentUser();

        if (!app.checkUser() || !app.checkChat()){
            return;
        }
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recyclerView.setAdapter(viewModel.getAdapter());

        binding.name.setText(app.getChat().getChatName());


        Log.d("MainActivity", "User Success and Chat Success");

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            public void onClick(View v) {
                if (!binding.message.getText().toString().trim().isEmpty()) {
                    Message message = new Message(app.getUser().getInfo().split(",")[6],
                            binding.message.getText().toString(),
                            System.currentTimeMillis() - TimeZone.getDefault().getOffset(System.currentTimeMillis()),
                            true,
                            app.getUser().getInfo().split(",")[5],
                            app.getChat().getChatEmail());
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(app.getUser().getInfo().split(",")[1]).child(app.getChat().getChatEmail()).setValue(message.getGuid());
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(app.getChat().getChatEmail()).child(app.getUser().getInfo().split(",")[1]).setValue(message.getGuid());
                    FirebaseDatabase.getInstance().getReference().child("Messages").child(app.getChat().getChatEmail()).child(String.valueOf(message.getGuid())).setValue(message);
                    //app.setLastMessage(binding.message.getText().toString());
                    app.setMessageGUID(message.getGuid());
                    viewModel.addMessage(message);
                    binding.message.getText().clear();
                }
            }
        });
        binding.btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //app.signOut();
                //checkCurrentUser();
                app.backToMainMenu();
                checkCurrentChat();

            }
        });
        /*FirebaseDatabase.getInstance().getReference().child("Messages")
                .child(app.getUser().getInfo().split(",")[1])
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "////////////////1");
                if (dataSnapshot.getValue() == null){return;}
                Log.d("MainActivity", "////////////////2");
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                Log.d("MainActivity", "Message - /3" + map);
                map = (HashMap<String, Object>) map.get(app.getUser().getInfo().split(",")[1]);
                try {
                    Log.d("MainActivity", "Message - /4" + map.values().toArray()[0].toString().split(",").toString());
                }catch (Throwable throwable){}

                //Message message = new Message();
                Log.d("MainActivity", String.valueOf(dataSnapshot.child(app.getMessageGUID()).getValue()));
                FirebaseDatabase.getInstance().getReference().child("Messages").child(app.getUser().getInfo().split(",")[1]).child(app.getMessageGUID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task != null){
                        Object map1 = task.getResult().getValue();
                        Log.d("MainActivity", String.valueOf(map1) + "///1");
                        //Message message = new Message(map1.toString().replace("{", "").replace("}", "").split(","));
                        Log.d("MainActivity", app.getMessageGUID() + " Message - ////2" + String.valueOf(map1).replace("{", "").replace("}", "").split(","));
                        //Log.d("MainActivity", app.getMessageGUID() + " Message - ////");
                        }
                        }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("MainActivity", "Failed to read app title value.", error.toException());
            }
        });*/
        FirebaseDatabase.getInstance().getReference().child("Messages").child(app.getUser().getInfo().split(",")[1]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    return;
                }
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                Log.d("MainActivity", "Message - /3" + map);
                map = (HashMap<String, Object>) map.get(app.getUser().getInfo().split(",")[1]);
                Log.d("MainActivity", "Message - /3" + map);
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("Users")
                        .child(app.getUser().getInfo().split(",")[1])
                        .child(app.getChat().getChatEmail()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data" + task.getResult() + task.getException());
                                }
                                else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                }
                            }
                        });
                FirebaseDatabase.getInstance().getReference().child("Messages").child(app.getUser().getInfo().split(",")[1]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task != null){
                            Object map1 = task.getResult().getValue();
                            Log.d("MainActivity",   app.getChat().getChatEmail() + String.valueOf(map1) + "///1");

                                    //Message message = new Message(map1.toString().replace("{", "").replace("}", "").split(","));

                            //Log.d("MainActivity", " Message - ////2" + message.getResult());
                             //String.valueOf(map1).replace("{", "").replace("}", "").split(",")
                            //Log.d("MainActivity", app.getMessageGUID() + " Message - ////");
                        }
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("MainActivity", "Failed to read app title value.", error.toException());
            }
        });
    }

    public boolean checkCurrentUser() {
        Log.d("MainActivity", String.valueOf(!app.checkUser()) + " / in check user / " + app.getUser());
        if (!app.checkUser()) {

            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        } else {
            checkCurrentChat();
        }

        return !app.checkUser() && !app.checkChat();
    }
    public boolean checkCurrentChat() {
        Log.d("MainActivity", String.valueOf(!app.checkChat()) + " / in check chat / " + app.getChat());
        if (!app.checkChat()) {
            Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        return  !app.checkChat();
    }

    public void sendEmailVerification() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("MyApp", "Email sent.");
                        }
                    }
                });
    }

    public void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("MainActivity", "User account deleted.");
                        }
                    }
                });
    }
}