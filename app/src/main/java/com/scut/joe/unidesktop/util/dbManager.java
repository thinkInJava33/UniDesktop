package com.scut.joe.unidesktop.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Idoit on 2017/6/22.
 */

public class dbManager {
    private MyDatabaseHelper mydbHelper;

    public dbManager(Context context){
        mydbHelper = new MyDatabaseHelper(context, "uniDeskTop.db",
                null, 1);
    }

    public void add_individuality_tb(SQLiteDatabase db, String subject, String body,
                           String date) {
        db.execSQL("insert into individuality_tb values(null,?,?,?)", new String[] {
                subject, body, date });

    }

    public void add_guardianship_tb(SQLiteDatabase db, String subject, String body,
                                     String date) {
        db.execSQL("insert into guardianship_tb values(null,?,?,?)", new String[] {
                subject, body, date });

    }

    public void add_elderly_tb(SQLiteDatabase db, String subject, String body,
                                     String date) {
        db.execSQL("insert into elderly_tb values(null,?,?,?)", new String[] {
                subject, body, date });

    }

    public Cursor queryMemento(SQLiteDatabase db, String subject, String body,
                               String date) {
        Cursor cursor = db.rawQuery(
                "select * from memento_tb where subject like ? and body like ? and date like ?",
                new String[] { "%" + subject + "%", "%" + body + "%",
                        "%" + date + "%" });
        return cursor;
    }
}
