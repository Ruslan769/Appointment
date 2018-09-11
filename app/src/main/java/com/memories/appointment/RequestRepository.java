package com.memories.appointment;

import com.memories.appointment.db.user.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestRepository {

    private final String DOMAIN_NAME = "http://139.59.212.56";
    private final String USER_URL = DOMAIN_NAME + "/appointment/users/";
    public static final String REQUEST_FAILURE_TEXT = "Ошибка запроса";
    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_FAILURE = -1;
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private final void sendRequest(final String url, final Map params) {
        if (callback == null)
            throw new RuntimeException("No callback");

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final JSONObject objParams = new JSONObject(params);
        final RequestBody body = RequestBody.create(JSON, objParams.toString());
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        final OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    public final void getUser(final String email, final String hash) {
        final Map<String, String> mapUser = new HashMap<>();
        mapUser.put("email", email);
        mapUser.put("hash", hash);

        final Map<String, Map> methodName = new HashMap<>();
        methodName.put("query", mapUser);

        sendRequest(USER_URL, methodName);
    }

    public final void addUser(final User mUser) {
        final Map<String, String> mapUser = new HashMap<>();
        mapUser.put("name", mUser.getName());
        mapUser.put("email", mUser.getEmail());
        mapUser.put("hash", mUser.getHash());

        final Map<String, Map> methodName = new HashMap<>();
        methodName.put("add", mapUser);

        sendRequest(USER_URL, methodName);
    }
}
