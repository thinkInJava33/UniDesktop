package com.scut.joe.unidesktop.adapter;

import android.content.Context;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.model.CommonViewHolder;
import com.scut.joe.unidesktop.model.SMSBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by joe on 17-7-7.
 */

public class SMSAdapter extends CommonAdapter<SMSBean> {
    private SimpleDateFormat sdf;

    public SMSAdapter(Context context, List<SMSBean> list) {
        super(context, list, R.layout.sms_list_item);
        this.sdf = new SimpleDateFormat("MM/dd");
    }

    @Override
    public void setViewContent(CommonViewHolder viewHolder, final SMSBean smsBean) {
        viewHolder.setText(R.id.sms_number, smsBean.getAddress())
                .setText(R.id.sms_date, this.sdf.format(smsBean.getDate()));
    }
}
