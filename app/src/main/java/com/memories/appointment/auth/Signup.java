package com.memories.appointment.auth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.memories.appointment.AppointmentApp;
import com.memories.appointment.R;

public class Signup extends AppCompatActivity {

    public static final String TEXT_REQUIRED_FIELD = "Пожалуйста заполните поле";
    private EditText tvNameReg, tvEmailReg, tvPassReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        tvNameReg = findViewById(R.id.tvNameReg);
        tvEmailReg = findViewById(R.id.tvEmailReg);
        tvPassReg = findViewById(R.id.tvPassReg);

        findViewById(R.id.tvSignin).setOnClickListener(v -> finish());
        findViewById(R.id.btnSignUp).setOnClickListener(v -> {
            if (TextUtils.isEmpty(tvNameReg.getText())) {
                tvNameReg.setError(TEXT_REQUIRED_FIELD);
                return;
            } else if (TextUtils.isEmpty(tvEmailReg.getText())) {
                tvEmailReg.setError(TEXT_REQUIRED_FIELD);
                return;
            } else if (TextUtils.isEmpty(tvPassReg.getText())) {
                tvPassReg.setError(TEXT_REQUIRED_FIELD);
                return;
            }
            Log.d(AppointmentApp.LOG, "Данные введены корректно!");
        });
    }
}
