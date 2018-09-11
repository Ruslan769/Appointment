package com.memories.appointment.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.memories.appointment.activity.AppointmentApp;
import com.memories.appointment.InternetTest;
import com.memories.appointment.R;
import com.memories.appointment.RequestRepository;
import com.memories.appointment.db.user.DBHelperUser;
import com.memories.appointment.db.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, Callback {

    public static final String TEXT_REQUIRED_FIELD = "Пожалуйста заполните поле";
    public static final String TEXT_REQUIRED_FIELD_PASS = "Пароль не должен быть меньше 8 символов";
    private EditText tvNameReg, tvEmailReg, tvPassReg;

    private User mUser;
    private ResponseHandler handler;
    private RequestRepository rRepository;
    private DBHelperUser dbHelperUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        tvNameReg = findViewById(R.id.tvNameReg);
        tvEmailReg = findViewById(R.id.tvEmailReg);
        tvPassReg = findViewById(R.id.tvPassReg);

        mUser = new User();
        handler = new ResponseHandler();
        rRepository = new RequestRepository();
        dbHelperUser = new DBHelperUser(this);

        rRepository.setCallback(this);
        findViewById(R.id.tvSignin).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
    }

    private class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RequestRepository.REQUEST_SUCCESS:
                    final boolean request_status = dbHelperUser.addUser(mUser);
                    if (request_status) {
                        Intent intent = new Intent(SignupActivity.this, AppointmentApp.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, RequestRepository.REQUEST_FAILURE_TEXT, Toast.LENGTH_LONG).show();
                    }
                    break;
                case RequestRepository.REQUEST_FAILURE:
                    String message_error = RequestRepository.REQUEST_FAILURE_TEXT;
                    if (msg.obj != null) {
                        message_error = msg.obj.toString();
                    }
                    Toast.makeText(SignupActivity.this, message_error, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelperUser.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSignin:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnSignUp:
                final String nameReg = tvNameReg.getText().toString().trim();
                final String emailReg = tvEmailReg.getText().toString().trim();
                final String passReg = tvPassReg.getText().toString().trim();

                if (TextUtils.isEmpty(nameReg)) {
                    tvNameReg.setError(TEXT_REQUIRED_FIELD);
                    return;
                } else if (TextUtils.isEmpty(emailReg)) {
                    tvEmailReg.setError(TEXT_REQUIRED_FIELD);
                    return;
                } else if (passReg.length() < 7) {
                    tvPassReg.setError(TEXT_REQUIRED_FIELD_PASS);
                    return;
                }

                if (!InternetTest.hasConnection(this)) {
                    Toast.makeText(this, InternetTest.NOT_FOUND_INTERNET, Toast.LENGTH_LONG).show();
                    return;
                }

                mUser.setName(nameReg);
                mUser.setEmail(emailReg);
                mUser.setHash(User.md5(passReg));

                rRepository.addUser(mUser);
                break;
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        handler.sendEmptyMessage(RequestRepository.REQUEST_FAILURE);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        boolean success = false;
        final Message message = new Message();
        final String jsonData = response.body().string();
        JSONObject inObjJSON = null;

        try {
            inObjJSON = new JSONObject(jsonData);
            final String statusRequest = inObjJSON.getString("status");
            if (statusRequest.equals("ok")) success = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (success) {
            handler.sendEmptyMessage(RequestRepository.REQUEST_SUCCESS);
        } else {
            message.what = RequestRepository.REQUEST_FAILURE;
            if (!inObjJSON.isNull("text")) {
                try {
                    message.obj = inObjJSON.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            handler.sendMessage(message);
        }
    }
}
