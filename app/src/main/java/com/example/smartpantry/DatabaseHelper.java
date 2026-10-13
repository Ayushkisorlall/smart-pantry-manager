package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// SQLite database for the pantry items
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // Create
    public long addItem(PantryItem item) {
        return getWritableDatabase().insert("pantry", null, toValues(item));
    }

    // Read all
    public List<PantryItem> getAllItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE")) {
            while (c.moveToNext()) {
                list.add(fromCursor(c));
            }
        }
        return list;
    }

    // Read one
    public PantryItem getItem(int id) {
        PantryItem item = null;
        try (Cursor c = getReadableDatabase().query("pantry", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) {
                item = fromCursor(c);
            }
        }
        return item;
    }

    // Update
    public int updateItem(PantryItem item) {
        return getWritableDatabase().update("pantry", toValues(item), "id=?",
                new String[]{String.valueOf(item.id)});
    }

    // Delete
    public int deleteItem(int id) {
        return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put("name", item.name);
        v.put("quantity", item.quantity);
        v.put("unit", item.unit);
        v.put("expiry", item.expiry);
        return v;
    }

    private PantryItem fromCursor(Cursor c) {
        return new PantryItem(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry")));
    }
}