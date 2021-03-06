package com.wefeng.launcher.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * Created by Administrator on 13-10-8.
 */
public class GetApk {

    private Context mContext = null;
    private PackageManager mManager = null;
    private List<PackageInfo> mApkList = null;
    private HashMap<String, Drawable> mApkHash = null;

    private Thread mInitApkThread = new Thread()
    {
        @Override
        public void run() {
            super.run();
            mInitApkInfo.run();
        }
    };

    private Runnable mInitApkInfo = new Runnable() {
        @Override
        public void run() {
            mApkList = getInstallerApk();
            mApkHash = new HashMap<String, Drawable>();

            for(PackageInfo apk:mApkList)
            {
                Drawable icon=getApkIcon(apk);
                String label = getApkLabel(apk);

                mApkHash.put(label, icon);
            }

            mListen.loadApkFinishListen(mApkHash, mApkList);
        }
    };

    public interface LoadApkFinishListen
    {
        void loadApkFinishListen(HashMap<String, Drawable> apkList, List<PackageInfo> packageList);
    }

    private LoadApkFinishListen mListen = null;

    public GetApk(Context context, LoadApkFinishListen listen)
    {
        mContext = context;

        mManager = context.getPackageManager();
        mInitApkThread.start();
        mListen = listen;
    }

    public HashMap<String, Drawable> getApk()
    {
        return mApkHash;
    }

    private List<PackageInfo> getInstallerApk()
    {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        List<PackageInfo> packages = mManager.getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo pak = packages.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }

        return apps;

    }

    public String getApkLabel(PackageInfo apk)
    {
        return mManager.getApplicationLabel(apk.applicationInfo).toString();
    }

    public Drawable getApkIcon(PackageInfo apk)
    {
        return mManager.getApplicationIcon(apk.applicationInfo);
    }

    public void runApk(PackageInfo apk)
    {
        Intent mainIntent = mContext.getPackageManager().getLaunchIntentForPackage(apk.packageName);

        assert mainIntent != null;

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(mainIntent);
    }


    public static void startNewApp(Context context, String packageName)
    {
        Intent mainIntent = context
                .getPackageManager()
                .getLaunchIntentForPackage(packageName);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
    }

    public static void startNewApp(Context context, PackageInfo apk)
    {
        Intent mainIntent = context
                .getPackageManager()
                .getLaunchIntentForPackage(apk.packageName);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
    }
}
