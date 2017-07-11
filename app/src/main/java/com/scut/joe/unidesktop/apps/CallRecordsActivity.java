package com.scut.joe.unidesktop.apps;


import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.adapter.DialogAdapter;
import com.scut.joe.unidesktop.model.CallRecordsBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joe on 17-6-19.
 */

public class CallRecordsActivity extends AppCompatActivity {
    private ListView callLogList;
    private Button contactsButton;
    private Button phoneButton;
    private AsyncQueryHandler asyncQuery;
    private DialogAdapter adapter;
    private List<CallRecordsBean> callLogs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_record_list_view);

        callLogList = (ListView)findViewById(R.id.call_log_list);
        contactsButton = (Button)findViewById(R.id.contacts_button);
        phoneButton = (Button)findViewById(R.id.phone_button);

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName componentName = new ComponentName("com.scut.joe.unidesktop",
                        "com.scut.joe.unidesktop.apps.ContactsActivity");
                Intent i = new Intent().setComponent(componentName);
                startActivity(i);
            }
        });
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                startActivity(i);
            }
        });

        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        init();
    }

    private void init() {
        Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
        // 查询的列
        String[] projection = { CallLog.Calls.DATE, // 日期
                CallLog.Calls.NUMBER, // 号码
                CallLog.Calls.TYPE, // 类型
                CallLog.Calls.CACHED_NAME, // 名字
                CallLog.Calls._ID, // id
        };
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
    }

    private class MyAsyncQueryHandler extends AsyncQueryHandler{
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if(cursor != null && cursor.getCount() > 0){
                callLogs = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");
                Date date;
                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++){
                    cursor.moveToPosition(i);
                    date = new Date(cursor.getLong(cursor
                    .getColumnIndex(CallLog.Calls.DATE)));
                    String number = cursor.getString(cursor
                            .getColumnIndex(CallLog.Calls.NUMBER));
                    int type = cursor.getInt(cursor
                            .getColumnIndex(CallLog.Calls.TYPE));
                    String cachedName = cursor.getString(cursor
                            .getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
                    int id = cursor.getInt(cursor
                            .getColumnIndex(CallLog.Calls._ID));

                    CallRecordsBean callRecordsBean = new CallRecordsBean();
                    callRecordsBean.setId(id);
                    callRecordsBean.setNumber(number);
                    callRecordsBean.setName(cachedName);
                    if (null == cachedName || "".equals(cachedName)) {
                        callRecordsBean.setName(number);
                    }
                    callRecordsBean.setType(type);
                    callRecordsBean.setDate(sdf.format(date));

                    callLogs.add(callRecordsBean);
                }
                if (callLogs.size() > 0) {
                    setAdapter(callLogs);
                }
                if(cursor != null){
                    cursor.close();
                }
            }
        }
    }

    private void setAdapter(List<CallRecordsBean> callLogs) {
        adapter = new DialogAdapter(this, callLogs);
        callLogList.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
