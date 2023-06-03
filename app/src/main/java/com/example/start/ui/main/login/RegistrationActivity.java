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

import com.example.start.R;
import com.example.start.ui.main.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity  {
    EditText emailAdress;
    EditText firstPassword;
    EditText secondPassword;
    EditText name;
    Button send;
    Button toLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "FROM REGISTR");
        setContentView(R.layout.register_layout);
        emailAdress = findViewById(R.id.emailaddress);
        firstPassword = findViewById(R.id.firstpassword);
        secondPassword = findViewById(R.id.secondpassword);
        name = findViewById(R.id.name);
        send = findViewById(R.id.btnRegister);
        toLogin = findViewById(R.id.btnToLogin);
        FirebaseFirestore firebaseFirestore;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        if (!emailAdress.getText().toString().trim().isEmpty() &&
                             !firstPassword.getText().toString().trim().isEmpty() &&
                              !secondPassword.getText().toString().trim().isEmpty() &&
                               !name.getText().toString().trim().isEmpty() &&
                                firstPassword.getText().toString().equals(secondPassword.getText().toString())) {

                                FirebaseFirestore.getInstance().collection("client").document(emailAdress.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            Toast.makeText(getApplicationContext(), "Already registered", Toast.LENGTH_SHORT).show();
                                        } else {
                                            addNewUser();
                                            Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
                                            Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
                                            startActivity(login);
                                            finish();
                                        }
                                    }
                                });
                        }
            }
        });
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(login);
                finish();

            }
        });
    }
    private void addNewUser(){
        Map<String, String> reg_entry = new HashMap<>();
        reg_entry.put("Email", "EMAIL-" + emailAdress.getText().toString());
        reg_entry.put("Password", "PASSWORD-" + firstPassword.getText().toString());
        reg_entry.put("Name", "NAME-" + name.getText().toString());
        reg_entry.put("TimeRegistration", "TIMEREG-" + String.valueOf(System.currentTimeMillis()));
        reg_entry.put("Id", "ID-" + UUID.randomUUID().toString());
        reg_entry.put("MobNumber", "Soon");
        reg_entry.put("Is2FaOn", "false");
        FirebaseFirestore.getInstance().collection("client").document(emailAdress.getText().toString()).set(reg_entry);
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
                            Log.d("MyApp", "Email sent.");
                        }
                    }
                });
    }
}
