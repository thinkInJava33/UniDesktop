package com.scut.joe.unidesktop.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.model.CallLogBean;
import com.scut.joe.unidesktop.model.CommonViewHolder;

import java.util.List;

/**
 * Created by joe on 17-7-3.
 */

public class DialogAdapter extends CommonAdapter<CallLogBean> {
    public DialogAdapter(Context context, List<CallLogBean> list) {
        super(context, list, R.layout.contact_record_list_item);
    }

    @Override
    public void setViewContent(CommonViewHolder viewHolder, final CallLogBean callLogBean) {
        viewHolder.setText(R.id.name, callLogBean.getName())
                .setText(R.id.number, callLogBean.getNumber())
                .setText(R.id.time, callLogBean.getDate());
        switch (callLogBean.getType()){
            case 1:
                viewHolder.setBackground(R.id.call_type, R.drawable.ic_calllog_outgoing_nomal);
                break;
            case 2:
                viewHolder.setBackground(R.id.call_type, R.drawable.ic_calllog_incomming_normal);
                break;
            case 3:
                viewHolder.setBackground(R.id.call_type, R.drawable.ic_calllog_missed_normal);
                break;
            default:
                break;
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + callLogBean.getNumber());
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                            Manifest.permission.CALL_PHONE)){

                    }else{
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE},
                                1);
                    }
                }
                mContext.startActivity(intent);
            }
        };
        viewHolder.addViewListener(R.id.call_btn, listener);
    }
}
