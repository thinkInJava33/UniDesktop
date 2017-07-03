package com.scut.joe.unidesktop.adapter;

import android.content.Context;

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
    public void setViewContent(CommonViewHolder viewHolder, CallLogBean callLogBean) {
        viewHolder.setText(R.id.name, callLogBean.getName())
                .setText(R.id.number, callLogBean.getNumber())
                .setText(R.id.time, callLogBean.getDate());
    }
}
