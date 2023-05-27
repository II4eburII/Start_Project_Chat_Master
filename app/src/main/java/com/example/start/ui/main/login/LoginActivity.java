package com.example.start.ui.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.start.MyApp;
import com.example.start.R;
import com.example.start.data.User;
import com.example.start.ui.main.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.stream.Collectors;

public class LoginActivity extends AppCompatActivity  {
    EditText emailAdress;
    EditText Password;
    Button send;
    Button toRegister;
    LoginViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        emailAdress = findViewById(R.id.emailaddress);
        Password = findViewById(R.id.password);
        send = findViewById(R.id.btnLogin);
        toRegister = findViewById(R.id.btnToRegister);
        Log.d("MainActivity", "FROM LOGIN");
        FirebaseFirestore firebaseFirestore;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        if (!emailAdress.getText().toString().trim().isEmpty() &&
                                !Password.getText().toString().trim().isEmpty()) {
                            FirebaseFirestore.getInstance().collection("client").document(emailAdress.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.getResult().exists()) {
                                        SignIn();
                                    } else {
                                        Log.d("LoginActivity", "Never was registered");
                                        Toast.makeText(getApplicationContext(), "Never was registered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
            }
        });
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(register);
                finish();
            }
        });
    }
    private void SignIn(){
        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        FirebaseFirestore.getInstance().collection("client").document(emailAdress.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("LoginActivity", "Task success");
                    if (task.getResult().getData().values().toArray()[6].toString().equals(Password.getText().toString())) {
                        MyApp app = ((MyApp) getApplicationContext());
                        User user = new User(task.getResult().getData().values().stream().map(Object::toString).collect(Collectors.joining(",")));
                        app.setUser(user);
                        Log.d("LoginActivity",  "User - " + app.getUser().getInfo());
                        //viewModel.getUser().setUser(Arrays.toString(task.getResult().getData().values().toArray()));

                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(main);
                        Log.d("LoginActivity", "Successfully start Activity");
                        finish();
                    } else {
                        Log.d("LoginActivity", "login fail " + task.getResult().getData().values().toArray()[6].toString() + "///" + Password.getText().toString());
                    }
                } else {
                    Log.d("LoginActivity", "Task database error");
                }
            }
        });
    }
    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            sendEmailVerification();
        }
    };
    private void sendEmailVerification() {
        // [START send_email_verification]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });
    }
}
