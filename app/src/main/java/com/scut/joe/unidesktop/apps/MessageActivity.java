package com.scut.joe.unidesktop.apps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.adapter.SMSAdapter;
import com.scut.joe.unidesktop.model.SMSBean;
import com.scut.joe.unidesktop.util.GetSMS;

import java.util.List;

/**
 * Created by joe on 17-6-19.
 */

public class MessageActivity extends AppCompatActivity {
    private ListView smsListView;
    private Button sendButton;
    private SMSAdapter smsAdapter;
    private GetSMS getSMS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list_view);
        smsListView = (ListView)findViewById(R.id.sms_list);
        sendButton = (Button)findViewById(R.id.send_message_button);

        getSMS = new GetSMS(MessageActivity.this);
        List<SMSBean> list = getSMS.getThreadsNum(getSMS.getThreads(0));
        smsAdapter = new SMSAdapter(MessageActivity.this, list);
        smsListView.setAdapter(smsAdapter);

        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SMSBean smsBean = (SMSBean) smsAdapter.getItem(position);
                Intent i = new Intent(MessageActivity.this, MessageDetailActivity.class);
                i.putExtra("phoneNumber", smsBean.getAddress());
                i.putExtra("threadId", smsBean.getThread_id());
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }
}
