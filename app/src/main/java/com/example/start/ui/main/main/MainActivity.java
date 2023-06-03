package com.example.start.ui.main.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.start.MyApp;
import com.example.start.data.Message;
import com.example.start.R;
import com.example.start.data.User;
import com.example.start.databinding.ActivityMainBinding;
import com.example.start.databinding.LoginLayoutBinding;
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

import java.nio.file.LinkPermission;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel viewModel;
    private ActivityMainBinding binding;
    private DatabaseReference mDatabase;
    private MyApp app;
    //snackbar - livedata / доделать friend
    private MutableLiveData<String> currentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = ((MyApp) getApplicationContext());
        checkCurrentUser(); // computer - pass
                            // aa - telephone

        if (!app.checkUser() || !app.checkChat()){
            return;
        }
        Log.d("MainActivity", "CHAT - " + app.getChat().getChatEmail());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recyclerView.setAdapter(viewModel.getAdapter());
        binding.recyclerView.scrollToPosition(binding.recyclerView.getAdapter().getItemCount() - 1);

        binding.name.setText(app.getChat().getChatName());




        final Observer<String> messageChange = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String string) {
                binding.recyclerView.scrollToPosition(binding.recyclerView.getAdapter().getItemCount() - 1);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.setMessageChange().observe(this, messageChange);



        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            public void onClick(View v) {
                if (!binding.message.getText().toString().trim().isEmpty()) {
                    Message message = new Message(app.getUser().getInfo().split(",")[6],
                            binding.message.getText().toString(),
                            System.currentTimeMillis() - TimeZone.getDefault().getOffset(System.currentTimeMillis()),
                            true,
                            app.getUser().getUserId(),
                            app.getChat().getChatId());
                    Log.d("MainActivity", app.getUser().getUserId() + "/" + app.getUser().getInfo().split(",")[1] + " / " + app.getChat().getChatId() + "/" + app.getChat().getChatEmail() + " = " + app.getUser().getUserId().equals(app.getChat().getChatId()));
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Messages")
                            .child(app.getChat().getChatId())
                            .child("NewMessages")
                            .child(String.valueOf(message.getGuid())).setValue(message);
                    //app.setLastMessage(binding.message.getText().toString());
                    //app.setMessageGUID(message.getGuid());
                    viewModel.addMessage(message);
                    binding.recyclerView.scrollToPosition(binding.recyclerView.getAdapter().getItemCount() - 1);
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
        registerForContextMenu(binding.recyclerView);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.operation_message, menu);
    }
    public void onDeleteMessage(MenuItem item){
        viewModel.deleteMessage(viewModel.getContextClickOperation());
        Log.d("MainActivity", "click delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.message:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    public void onChangeMessage(MenuItem item){
        final EditText input = new EditText(getApplicationContext());
        input.setText(viewModel.getContextClickOperation().getText());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.message)
                .setTitle("Изменение сообщения")
                .setView(input)
                .setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.changeMessage(viewModel.getContextClickOperation(), input.getText().toString());
                    }
                }).setCancelable(true)
                .create()
                .show();
    }
    public boolean checkCurrentUser() {
        //Log.d("MainActivity", String.valueOf(!app.checkUser()) + " / in check user / " + app.getUser());
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
        //Log.d("MainActivity", String.valueOf(!app.checkChat()) + " / in check chat / " + app.getChat());
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