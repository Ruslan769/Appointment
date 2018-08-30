package com.memories.appointment.auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.memories.appointment.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        findViewById(R.id.create).setOnClickListener(v -> {
            Intent it = new Intent(Login.this, Signup.class);
            startActivity(it);
        });
    }
}
