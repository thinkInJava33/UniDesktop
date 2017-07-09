package com.scut.joe.unidesktop.util;

/**
 * Created by Idoit on 2017/6/21.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    final String CREATE_TABLE_IP_SQL=
            "create table individuality_tb(_id integer primary " +
                    "key ,name text,icon ,package_name text," +
                    "class_name text,page_num integer,page_index integer," +
                    "is_empty integer,length integer)";
    final String CREATE_TABLE_GP_SQL=
            "create table guardianship_tb(_id integer primary " +
                    "key ,name text,icon ,package_name text," +
                    "class_name text,page_num integer,page_index integer)";
    final String CREATE_TABLE_EP_SQL=
            "create table elderly_tb(_id integer primary " +
                    "key ,name text,icon ,package_name text," +
                    "class_name text,page_num integer,page_index integer)";


    public MyDatabaseHelper(Context context, String name,
                            CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_IP_SQL);
        db.execSQL(CREATE_TABLE_GP_SQL);
        db.execSQL(CREATE_TABLE_EP_SQL);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        System.out.println("---------"+oldVersion+"------->"+newVersion);
    }
}