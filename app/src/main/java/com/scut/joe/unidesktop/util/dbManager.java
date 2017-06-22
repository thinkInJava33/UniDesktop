package com.scut.joe.unidesktop.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.scut.joe.unidesktop.model.AppItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Idoit on 2017/6/22.
 */

public class dbManager {
    private MyDatabaseHelper mydbHelper;
    private SQLiteDatabase db;

    public dbManager(Context context){
        mydbHelper = new MyDatabaseHelper(context, "uniDeskTop.db",
                null, 1);
        db = mydbHelper.getReadableDatabase();
    }

    public void addItem(int mode,int id, String name,Drawable icon,String packageName,
            String className,int pageNum, int rowIndex, int columnIndex) {
        //第一步，将Drawable对象转化为Bitmap对象
        Bitmap bmp = (((BitmapDrawable)icon).getBitmap());
        //第二步，声明并创建一个输出字节流对象
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //第三步，调用compress将Bitmap对象压缩为PNG格式，第二个参数为PNG图片质量，第三个参数为接收容器，即输出字节流os
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
       //第四步，将输出字节流转换为字节数组，并直接进行存储数据库操作，注意，所对应的列的数据类型应该是BLOB类型
        ContentValues values = new ContentValues();
        ContentValues cv=new ContentValues();

        cv.put("_id", id);
        cv.put("name", name);
        cv.put("icon", os.toByteArray());
        cv.put("package_name", packageName);
        cv.put("class_name", className);
        cv.put("page_num", pageNum);
        cv.put("row_index", rowIndex);
        cv.put("column_index", columnIndex);
        switch (mode){
            case 0:
                db.insert("elderly_tb",null,cv);
                break;
            case 1:
                db.insert("guardianship _tb",null,cv);
                break;
            case 2:
                db.insert("individuality_tb",null,cv);
                break;
        }

    }

    public void hide(int mode,int id){
        ContentValues cv = new ContentValues();
        cv.put("row_index", -1);
        cv.put("column_index", -1);
        //修改条件
        String whereClause = "id=?";
        //修改添加参数
        String[] whereArgs={String.valueOf(id)};
        switch (mode){
            case 0:
                db.update("elderly_tb",cv,whereClause,whereArgs);
                break;
            case 1:
                db.update("guardianship_tb",cv,whereClause,whereArgs);
                break;
            case 2:
                db.update("individuality_tb",cv,whereClause,whereArgs);
                break;
        }
    }

    public void delete(int mode, int id){
        //修改条件
        String whereClause = "id=?";
        //修改添加参数
        String[] whereArgs={String.valueOf(id)};
        switch (mode){
            case 0:
                db.delete("elderly_tb",whereClause,whereArgs);
                break;
            case 1:
                db.delete("guardianship_tb",whereClause,whereArgs);
                break;
            case 2:
                db.delete("individuality_tb",whereClause,whereArgs);
                break;
        }
    }



    public List<AppItem> getApps(int mode,int pageNum) {
        ArrayList<AppItem> apps = new ArrayList<>();
        Cursor cursor;
        switch (mode){
            case 0:
                cursor = db.rawQuery(
                        "select * from elderly_tb where row_index<>-1 and page_num = ? order by row_index,column_index",
                        new String[] { String.valueOf(pageNum) });
                break;
            case 1:
                cursor = db.rawQuery(
                        "select * from guardianship_tb where row_index<>-1 and page_num = ? order by row_index,column_index",
                        new String[] { String.valueOf(pageNum) });
                break;
            default:
                cursor = db.rawQuery(
                        "select * from individuality_tb where row_index<>-1 and page_num = ? order by row_index,column_index",
                        new String[] { String.valueOf(pageNum) });
                break;
        }
        while (cursor.moveToNext()) {
            //第一步，从数据库中读取出相应数据，并保存在字节数组中
            byte[] blob = cursor.getBlob(cursor.getColumnIndex("icon"));
            //第二步，调用BitmapFactory的解码方法decodeByteArray把字节数组转换为Bitmap对象
            Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            //第三步，调用BitmapDrawable构造函数生成一个BitmapDrawable对象，该对象继承Drawable对象，所以在需要处直接使用该对象即可
            BitmapDrawable bd = new BitmapDrawable(bmp);
            AppItem app = new AppItem();
            app.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            app.setAppName(cursor.getString(cursor.getColumnIndex("name")));
            app.setAppIcon(bd);
            app.setPackageName(cursor.getString(cursor.getColumnIndex("package_name")));
            app.setClassName(cursor.getString(cursor.getColumnIndex("class_name")));
            app.setRowIndex(cursor.getInt(cursor.getColumnIndex("row_index")));
            app.setColIndex(cursor.getInt(cursor.getColumnIndex("column_index")));
            app.setPageNum(cursor.getInt(cursor.getColumnIndex("page_num")));
            apps.add(app);
        }
        cursor.close();
        return apps;


    }
}
