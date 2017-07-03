package com.scut.joe.unidesktop.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.scut.joe.unidesktop.model.AppItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Idoit on 2017/6/22.
 */

public class dbManager {
    private MyDatabaseHelper mydbHelper;
    private SQLiteDatabase db = null;

    public dbManager(Context context){
        mydbHelper = new MyDatabaseHelper(context, "uniDeskTop.db",
                null, 1);
        db = mydbHelper.getReadableDatabase();
    }

    public void addItem(int mode,int id, String name,Drawable icon,String packageName,
            String className,int pageNum, int index,int row, int isEmpty) {
        //第一步，将Drawable对象转化为Bitmap对象
        //第二步，声明并创建一个输出字节流对象
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if(icon != null) {
            Bitmap bmp = (((BitmapDrawable) icon).getBitmap());
            //第三步，调用compress将Bitmap对象压缩为PNG格式，第二个参数为PNG图片质量，第三个参数为接收容器，即输出字节流os
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            //第四步，将输出字节流转换为字节数组，并直接进行存储数据库操作，注意，所对应的列的数据类型应该是BLOB类型
        }
        ContentValues cv=new ContentValues();
        cv.put("_id",id);
        cv.put("name", name);
        cv.put("icon", os.toByteArray());
        cv.put("package_name", packageName);
        cv.put("class_name", className);
        cv.put("page_num", pageNum);
        cv.put("page_index", index);
        cv.put("page_row", row);
        cv.put("is_empty", isEmpty);

        db.insert(mode2tableName(mode),null,cv);
    }

    public void addItem(int mode,int id, String name,Drawable icon,String packageName,
                        String className,int pageNum, int index) {
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
        cv.put("page_index", index);
        db.insert(mode2tableName(mode),null,cv);
    }

    public void addEmptyItem(int mode,int id,int pageNum,int index){
        addItem(mode,id,"",null,"","",pageNum,index,-1,1);
    }

    public void hide(int mode,int id){
        ContentValues cv = new ContentValues();
        cv.put("page_index", -1);
        //修改条件
        String whereClause = "_id=?";
        //修改添加参数
        String[] whereArgs={String.valueOf(id)};
        db.update(mode2tableName(mode),cv,whereClause,whereArgs);
    }

    public void delete(int mode, int id){
        //修改条件
        String whereClause = "_id=?";
        //修改添加参数
        String[] whereArgs={String.valueOf(id)};
        db.delete(mode2tableName(mode),whereClause,whereArgs);
    }

    public List<AppItem> search(int mode,String key){
        ArrayList<AppItem> apps = new ArrayList<>();
        Cursor cursor;
        cursor = db.rawQuery("select * from " + mode2tableName(mode) + " where page_index<>-1",
                null);
        while(cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex("name")).toLowerCase().indexOf(key.toLowerCase()) != -1){
                AppItem app = new AppItem();
                //第一步，从数据库中读取出相应数据，并保存在字节数组中
                byte[] blob = cursor.getBlob(cursor.getColumnIndex("icon"));
                //第二步，调用BitmapFactory的解码方法decodeByteArray把字节数组转换为Bitmap对象
                Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                //第三步，调用BitmapDrawable构造函数生成一个BitmapDrawable对象，该对象继承Drawable对象，所以在需要处直接使用该对象即可
                BitmapDrawable bd = new BitmapDrawable(bmp);
                app.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                app.setAppName(cursor.getString(cursor.getColumnIndex("name")));
                app.setAppIcon(bd);
                app.setPackageName(cursor.getString(cursor.getColumnIndex("package_name")));
                app.setClassName(cursor.getString(cursor.getColumnIndex("class_name")));
                app.setIndex(cursor.getInt(cursor.getColumnIndex("page_index")));
                app.setPageNum(cursor.getInt(cursor.getColumnIndex("page_num")));
                apps.add(app);
            }
        }
        cursor.close();
        return apps;
    }

    public void exchange(int mode,int start,int end,List<AppItem> apps){
        for(int i = start; i <= end; i++){
            updateApp(mode,apps.get(i));
        }
    }

    public void updateApp(int mode,AppItem app){
        ContentValues cv = new ContentValues();
        cv.put("page_index",app.getIndex());
        db.update(mode2tableName(mode),cv,"_id = ?",new String[]{String.valueOf(app.getId())});
    }



    public List<AppItem> getApps(int mode,int pageNum) {
        ArrayList<AppItem> apps = new ArrayList<>();
        Cursor cursor;
        cursor = db.rawQuery(
                "select * from " + mode2tableName(mode) + " where page_index<>-1 and page_num = ? order by page_index",
                new String[] { String.valueOf(pageNum) });
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
            app.setIndex(cursor.getInt(cursor.getColumnIndex("page_index")));
            app.setPageNum(cursor.getInt(cursor.getColumnIndex("page_num")));
            app.setIsEmpty(cursor.getInt(cursor.getColumnIndex("is_empty")));
            app.setPageRow(cursor.getInt(cursor.getColumnIndex("page_row")));
            apps.add(app);
        }
        cursor.close();
        return apps;
    }

    /**
     * 判断某表是否有数据
     * @param mode 桌面模式
     * @return result 表为空时返回false
     */
    public boolean tableHasData(int mode){
        Cursor cursor = null;
        String sql = "select * from " + mode2tableName(mode);
        cursor = db.rawQuery(sql, null);
        return cursor.moveToFirst();
    }

    public String mode2tableName(int mode){
        String tableName = null;
        switch (mode){
            case 0:
                tableName  = "elderly_tb";
                break;
            case 1:
                tableName = "guardianship_tb";
                break;
            case 2:
                tableName = "individuality_tb";
                break;
            default:
                break;
        }
        return tableName;
    }
}
