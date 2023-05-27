package com.example.start.ui.main.menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.start.MyApp;
import com.example.start.R;
import com.example.start.data.Friend;
import com.example.start.data.UserInChat;
import com.example.start.databinding.ActivityMainMenuBinding;
import com.example.start.ui.main.login.LoginActivity;
import com.example.start.ui.main.login.RegistrationActivity;
import com.example.start.ui.main.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainMenuActivity extends AppCompatActivity {
    private MainMenuActivityViewModel viewModel;
    private ActivityMainMenuBinding binding;
    private DatabaseReference mDatabase;
    private MyApp app;
    private AppBarConfiguration mAppBarConfiguration;

    //snackbar - livedata / доделать friend
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);
        viewModel = new ViewModelProvider(this).get(MainMenuActivityViewModel.class);
        app = ((MyApp) getApplicationContext());


        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recyclerView.setAdapter(viewModel.getAdapter());

        viewModel.getAdapter().getClickFriendItem().observe(this, new Observer<Friend>() {
            @Override
            public void onChanged(Friend friend) {
                setChatInMainMenu(friend);
                Toast.makeText(getApplicationContext(), friend.getFriendName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.FrindAddBtn.setOnClickListener(view -> {

            final EditText input = new EditText(getApplicationContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.friend_add)
                        .setTitle("Добавить друга")
                        .setView(input)
                        .setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore.getInstance().collection("client").document(input.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            Log.d("MainActivity", "yes");
                                            if (document.exists()) {
                                                Log.d("MainActivity", "YES");
                                                app.addFriend(input.getText().toString());
                                            } else {
                                                Log.d("MainActivity", "no");
                                                Toast.makeText(getApplicationContext(), "Кажется такого не существует", Toast.LENGTH_SHORT);
                                            }
                                        } else {
                                            Log.d("MainActivity", "NO");
                                        }
                                    }
                                });


                            }
                        })
                        .setCancelable(true)
                    .create()
                    .show();
        });
        binding.MainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.signOut();
                Intent intent = new Intent(MainMenuActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onFriendClick(View v) {
        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
    }
    public void setChatInMainMenu(Friend friend){
        Log.d("MyApp", friend.getFriendName() + "//" + friend.getFriendEmail() + "//" + friend.getFriendId());
        app.setChat(new UserInChat(friend.getFriendName(), friend.getFriendEmail(), friend.getFriendId(), app.getUser().getInfo().split(",")[1]));
        Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
