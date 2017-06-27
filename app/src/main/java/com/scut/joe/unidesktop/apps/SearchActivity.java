package com.scut.joe.unidesktop.apps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.util.dbManager;

import java.util.List;

/**
 * Created by Idoit on 2017/6/26.
 */

public class SearchActivity extends Activity{
    private RecyclerView resultRecyclerView;
    private AppAdapater mAppAdapter;
    private EditText keyInput;
    private Context mContext;
    private int mode ;
    private dbManager db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        db = new dbManager(mContext);
        mode = getSharedPreferences("mode",MODE_PRIVATE).getInt("choose",-1);
        setContentView(R.layout.activity_search);
        resultRecyclerView = (RecyclerView)findViewById(R.id.show_serach_app);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        keyInput = (EditText)findViewById(R.id.key_input);
        keyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")) {
                    List<AppItem> apps = db.search(mode, s.toString());
                    if (mAppAdapter == null) {
                        mAppAdapter = new AppAdapater(apps);
                        resultRecyclerView.setAdapter(mAppAdapter);
                    } else {
                        mAppAdapter.setApps(apps);
                        mAppAdapter.notifyDataSetChanged();
                    }
                }else {
                    mAppAdapter.setApps(null);
                    mAppAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }

    private class AppHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private TextView mAppName;
        private ImageView mAppIcon;

        private AppItem mApp;

        public AppHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mAppName = (TextView) itemView.findViewById(R.id.list_name);
            mAppIcon = (ImageView) itemView.findViewById(R.id.list_icon);


        }
        public void bindApp(AppItem app){
            mApp = app;
            mAppName.setText((mApp.getAppName()));
            mAppIcon.setImageDrawable(mApp.getAppIcon());
        }

        @Override
        public void onClick(View view) {
            ComponentName componentName = new ComponentName(mApp.getPackageName(),mApp.getClassName());
            Intent i = new Intent(Intent.ACTION_MAIN)
                    .setComponent(componentName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, 0);
        }
    }

    private class AppAdapater extends RecyclerView.Adapter<AppHolder>{
        private List<AppItem> mApps;

        public AppAdapater(List<AppItem> apps){
            mApps = apps;
        }

        @Override
        public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.list_item_app,parent,false);
            return new AppHolder(view);
        }

        @Override
        public void onBindViewHolder(AppHolder holder, int position) {
            AppItem app = mApps.get(position);
            holder.bindApp(app);
        }

        @Override
        public int getItemCount() {
            if(mApps == null) return 0;
            return mApps.size();
        }

        public void setApps(List<AppItem> apps){
            mApps = apps;
        }
    }
}
