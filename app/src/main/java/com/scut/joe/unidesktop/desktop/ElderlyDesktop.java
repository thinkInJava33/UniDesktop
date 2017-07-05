package com.scut.joe.unidesktop.desktop;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.page.ElderlyPage;

/**
 * Created by joe on 17-6-16.
 */

public class ElderlyDesktop extends Desktop {
    private static Context mContext;
    ViewPager viewPager;

    //要申请的权限
    private String[] permissions = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS};
    private AlertDialog dialog;

    public static ElderlyDesktop newInstance(Context context){
        Bundle args = new Bundle();
        mContext = context;
        ElderlyDesktop elderlyDesktop = new ElderlyDesktop();
        elderlyDesktop.setArguments(args);
        return elderlyDesktop;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //版本判断
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            boolean needShow = false;
            //检查该权限是否已经获取
            for(String permission: permissions){
                int i = ContextCompat.checkSelfPermission(mContext, permission);
                if(i != PackageManager.PERMISSION_GRANTED){
                    needShow = true;
                }
            }
            if(needShow){
                Log.v("perTest", "调用showDialog");
                showDialogTipUserRequestPermission();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("test", "this is elderlyDesktop");
        View view = inflater.inflate(R.layout.fragment_elderly_desktop, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.container);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return ElderlyPage.newInstance(position);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        return view;
    }

    //提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission(){
        new AlertDialog.Builder(mContext)
                .setTitle("权限不可用")
                .setMessage("由于老人模式需要获取联系人、通话记录和短信；" +
                        "\n否则，你无法打开电话、短信")
                .setPositiveButton("立即开启",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v("perTest", "请求权限");
                                requestPermissions(permissions, 440);
                                Log.v("perTest", "是否应该解释"+ shouldShowRequestPermissions(permissions));
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext, "电话、短信不能使用", Toast.LENGTH_LONG).show();
                            }
                        }).setCancelable(false).show();
    }

    //用户权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        Log.v("perTest", "回调");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 440){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    //判断用户是否点击了不再提醒
                    if(shouldShowRequestPermissions(permissions)){
                        requestPermissions(permissions, 440);
                    }
                    else{
                        //提示用户去应用设置界面手动开启权限
                        Log.v("perTest", "应该提示去设置界面开启权限");
                        showDialogTipUserGoToAppSetting();
                    }
                }else{
                    Toast.makeText(mContext, "权限获取成功",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean shouldShowRequestPermissions(String[] permissions){
        for(String permission: permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    (Activity)mContext, permission)){
                return true;
            }
        }
        return false;
    }

    private void showDialogTipUserGoToAppSetting(){
        dialog = new AlertDialog.Builder(mContext)
                .setTitle("权限不可用")
                .setMessage("请在-应用设置-权限-中，允许使用获取联系人、短信权限")
                .setPositiveButton("立即开启", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goToAppSetting();
                            }
                        })
                .setNegativeButton("取消", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).setCancelable(false).show();
    }

    //跳转到当前应用的设置界面
    private void goToAppSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                for(String permission: permissions){
                    if(ContextCompat.checkSelfPermission(mContext, permission)!= PackageManager.PERMISSION_GRANTED){
                        showDialogTipUserGoToAppSetting();
                    }
                }
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(mContext, "权限TMD获取成功", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void startApp() {

    }

    @Override
    public void arrangeApp() {

    }

    @Override
    public void hideApp() {

    }

    @Override
    public void addTool() {

    }

    @Override
    public void setWallpaper() {

    }

    @Override
    public void searchApp() {

    }

    @Override
    public void moveApp() {

    }
}
