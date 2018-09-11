package com.memories.appointment.auth;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Callback {

    private EditText tvEmail, tvPass;
    private ResponseHandler handler;
    private RequestRepository rRepository;
    private DBHelperUser dbHelperUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tvEmail = findViewById(R.id.tvEmail);
        tvPass = findViewById(R.id.tvPass);

        handler = new ResponseHandler();
        rRepository = new RequestRepository();
        dbHelperUser = new DBHelperUser(this);

        rRepository.setCallback(this);
        findViewById(R.id.create).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
    }

    private class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RequestRepository.REQUEST_SUCCESS:
                    dbHelperUser.addUser((User) msg.obj);
                    Intent intent = new Intent(LoginActivity.this, AppointmentApp.class);
                    startActivity(intent);
                    finish();
                    break;
                case RequestRepository.REQUEST_FAILURE:
                    String message_error = RequestRepository.REQUEST_FAILURE_TEXT;
                    if (msg.obj != null) {
                        message_error = msg.obj.toString();
                    }
                    Toast.makeText(LoginActivity.this, message_error, Toast.LENGTH_LONG).show();
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
            case R.id.create:
                Intent intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnLogin:
                final String email = tvEmail.getText().toString().trim();
                final String pass = tvPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    tvEmail.setError(SignupActivity.TEXT_REQUIRED_FIELD);
                    return;
                } else if (pass.length() < 7) {
                    tvPass.setError(SignupActivity.TEXT_REQUIRED_FIELD_PASS);
                    return;
                }

                if (!InternetTest.hasConnection(this)) {
                    Toast.makeText(this, InternetTest.NOT_FOUND_INTERNET, Toast.LENGTH_LONG).show();
                    return;
                }

                rRepository.getUser(email, User.md5(pass));
                break;
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        handler.sendEmptyMessage(RequestRepository.REQUEST_FAILURE);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final Message message = new Message();
        final String jsonData = response.body().string();
        JSONObject inObjJSON = null;
        User mUser = null;

        try {
            inObjJSON = new JSONObject(jsonData);
            final String statusRequest = inObjJSON.getString("status");
            if (statusRequest.equals("ok")) {
                inObjJSON = new JSONObject(inObjJSON.getString("response"));
                inObjJSON = new JSONObject(inObjJSON.getString("user"));
                final String name = inObjJSON.getString("name");
                final String email = inObjJSON.getString("email");
                final String hash = inObjJSON.getString("hash");
                mUser = new User(name, email, hash);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mUser != null) {
            message.what = RequestRepository.REQUEST_SUCCESS;
            message.obj = mUser;
        } else {
            message.what = RequestRepository.REQUEST_FAILURE;
            if (!inObjJSON.isNull("text")) {
                try {
                    message.obj = inObjJSON.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        handler.sendMessage(message);
    }
}
