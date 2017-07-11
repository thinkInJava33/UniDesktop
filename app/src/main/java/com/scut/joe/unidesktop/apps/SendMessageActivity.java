package com.scut.joe.unidesktop.apps;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.scut.joe.unidesktop.R;

import java.util.ArrayList;

/**
 * Created by joe on 17-7-11.
 */

public class SendMessageActivity extends AppCompatActivity {
    String SENT = "ACTION_SENT";
    String DELIVERED = "ACTION_DELIVERED";

    private ImageButton clearButton;
    private Button sendButton;
    private ImageButton chooseButton;
    private EditText numberET;
    private EditText contentET;

    BroadcastReceiver sendStateBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(SendMessageActivity.this,
                            "短信发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(SendMessageActivity.this,
                            "短信发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(SendMessageActivity.this,
                            "短信发送失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(SendMessageActivity.this,
                            "短信发送失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    BroadcastReceiver receiverStateBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(SendMessageActivity.this,
                            "短信接收成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(SendMessageActivity.this,
                            "短信接收失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_CANCEL_CURRENT);
        final PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), PendingIntent.FLAG_CANCEL_CURRENT);
        registerReceiver(sendStateBR, new IntentFilter(SENT));
        registerReceiver(receiverStateBR, new IntentFilter(DELIVERED));

        final Boolean isNewMessage = getIntent().getBooleanExtra("newMessage", true);
        setContentView(R.layout.send_message_view);
        clearButton = (ImageButton) findViewById(R.id.clear_button);
        sendButton = (Button) findViewById(R.id.send_button);
        chooseButton = (ImageButton) findViewById(R.id.contact_choose);
        numberET = (EditText) findViewById(R.id.send_number);
        contentET = (EditText) findViewById(R.id.message_content);
        if(!isNewMessage){
            final String number = getIntent().getStringExtra("phoneNumber");
            numberET.setText(number);
        }

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentET.setText("");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager manager = SmsManager.getDefault();
                ArrayList<String> divideContents = manager.divideMessage(contentET.getText().toString());
                for(String text: divideContents){
                    manager.sendTextMessage(numberET.getText().toString(), null, text, sentPI, deliveredPI);
                }
            }
        });

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(sendStateBR);
        unregisterReceiver(receiverStateBR);
        super.onDestroy();
    }
}
