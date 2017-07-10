package com.scut.joe.unidesktop.adapter;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.model.MessageBean;

import java.util.List;

/**
 * Created by joe on 17-7-9.
 */

public class MessageBoxListAdapter extends BaseAdapter {

    private List<MessageBean> mbList;
    private Context ctx;
    private LinearLayout layout_father;
    private LayoutInflater vi;
    private LinearLayout layout_child;
    private TextView tvDate;
    private TextView tvText;

    public MessageBoxListAdapter(Context context, List<MessageBean> coll) {
        ctx = context;
        vi = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mbList = coll;
    }

    public int getCount() {
        return mbList.size();
    }

    public Object getItem(int position) {
        return mbList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        MessageBean mb = mbList.get(position);
        int itemLayout = mb.getLayoutID();
        layout_father = new LinearLayout(ctx);
        vi.inflate(itemLayout, layout_father, true);

        layout_father.setBackgroundColor(Color.TRANSPARENT);
        layout_child = (LinearLayout) layout_father
                .findViewById(R.id.layout_bj);

        tvText = (TextView) layout_father
                .findViewById(R.id.message_detail_text);
        tvText.setText(mb.getText());

        tvDate = (TextView) layout_father
                .findViewById(R.id.message_detail_date);
        tvDate.setText(mb.getDate());

        addListener(tvText, tvDate, layout_child, mb);

        return layout_father;
    }

    public void addListener(final TextView tvText, final TextView tvDate,
                            LinearLayout layout_bj, final MessageBean mb) {

        layout_bj.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        layout_bj.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                tvText.setTextColor(0xffffffff);
                showListDialog(newtan, mb);
                return true;
            }
        });

        layout_bj.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_MOVE:
                        tvText.setTextColor(0xffffffff);
                        break;

                    default:
                        tvText.setTextColor(Color.BLACK);
                        break;
                }
                return false;
            }
        });
    }

    private String[] newtan = new String[] { "转发", "复制文本内容", "删除", "查询信息详情" };

    private void showListDialog(final String[] arg, final MessageBean mb) {
        new AlertDialog.Builder(ctx).setTitle("信息选项")
                .setItems(arg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                break;

                            case 1:
                                ClipboardManager cmb = (ClipboardManager) ctx
                                        .getSystemService(ctx.CLIPBOARD_SERVICE);
                                cmb.setText(mb.getText());
                                break;
                            case 2:

                                break;
                            case 3:
                                break;
                        }
                        ;
                    }
                }).show();
    }
}
