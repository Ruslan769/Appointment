package com.memories.appointment.db.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelperUser extends SQLiteOpenHelper {

    private final String TABLE_NAME = "USER";
    private final String QUERY_ID = "_id";
    private final String QUERY_NAME = "NAME";
    private final String QUERY_EMAIL = "EMAIL";
    private final String QUERY_HASH = "HASH";
    private final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + QUERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + QUERY_NAME + " TEXT, "
            + QUERY_EMAIL + " TEXT, "
            + QUERY_HASH + " TEXT);";

    public DBHelperUser(Context context) {
        super(context, "NOTEBOOK", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public User getUser() {
        User user = null;
        final Cursor cursor = getWritableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return user;
        }

        final int idIndex = cursor.getColumnIndex(QUERY_ID);
        final int nameIndex = cursor.getColumnIndex(QUERY_NAME);
        final int emailIndex = cursor.getColumnIndex(QUERY_EMAIL);
        final int hashIndex = cursor.getColumnIndex(QUERY_HASH);

        final int id = cursor.getInt(idIndex);
        final String name = cursor.getString(nameIndex);
        final String email = cursor.getString(emailIndex);
        final String hash = cursor.getString(hashIndex);

        user = new User(id, name, email, hash);
        cursor.close();

        return user;
    }

    public void clearTable() {
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    public boolean addUser(final User user) {
        clearTable();
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(QUERY_NAME, user.getName());
        contentValues.put(QUERY_EMAIL, user.getEmail());
        contentValues.put(QUERY_HASH, user.getHash());
        final boolean result = db.insert(TABLE_NAME, null, contentValues) != -1;
        db.close();
        return result;
    }

    public boolean updateUser(User user) {
        final String user_id = String.valueOf(user.getId());
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(QUERY_NAME, user.getName());
        final boolean result = db.update(TABLE_NAME, contentValues, QUERY_ID + "= ?", new String[] {user_id}) != -1;
        db.close();
        return result;
    }
}
