package com.memories.appointment.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.memories.appointment.InternetTest;
import com.memories.appointment.R;
import com.memories.appointment.RequestRepository;
import com.memories.appointment.auth.LoginActivity;
import com.memories.appointment.db.user.DBHelperUser;
import com.memories.appointment.db.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity implements Callback {

    private RequestRepository rRepository;
    private DBHelperUser dbHelperUser;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        rRepository = new RequestRepository();
        dbHelperUser = new DBHelperUser(this);

        rRepository.setCallback(this);

        mUser = dbHelperUser.getUser();

        if (mUser != null) {
            if (InternetTest.hasConnection(this)) {
                rRepository.getUser(mUser.getEmail(), mUser.getHash());
            } else {
                startApp(true);
            }
        } else {
            startApp(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelperUser.close();
    }

    private void startApp(boolean auth) {
        final Intent intent = new Intent();
        if (auth) {
            intent.setClass(this, AppointmentApp.class);
        } else {
            intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        startApp(false);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        boolean request_success = false;
        final String jsonData = response.body().string();

        try {
            JSONObject inObjJSON = new JSONObject(jsonData);
            final String statusRequest = inObjJSON.getString("status");
            if (statusRequest.equals("ok")) {

                inObjJSON = new JSONObject(inObjJSON.getString("response"));
                inObjJSON = new JSONObject(inObjJSON.getString("user"));

                final String name = inObjJSON.getString("name");

                if (!name.equals(mUser.getName())) {
                    mUser.setName(name);
                    dbHelperUser.updateUser(mUser);
                }

                request_success = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (request_success) {
                startApp(true);
            } else {
                startApp(false);
            }
        }
    }
}
