package com.memories.appointment.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.memories.appointment.R;
import com.memories.appointment.RequestRepository;
import com.memories.appointment.db.user.DBHelperUser;
import com.memories.appointment.db.user.User;

public class AppointmentApp extends AppCompatActivity {

    public static final String TAG = "myLog";

    private RequestRepository rRepository;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        rRepository = new RequestRepository();
        getUser();

        final TextView textView = findViewById(R.id.textView);
        textView.setText("Name = " + mUser.getName() + "\nHash = " + mUser.getHash());
    }

    private void getUser() {
        final DBHelperUser dbHelperUser = new DBHelperUser(this);
        mUser = dbHelperUser.getUser();
        dbHelperUser.close();
    }
}
