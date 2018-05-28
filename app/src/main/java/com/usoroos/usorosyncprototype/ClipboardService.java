package com.usoroos.usorosyncprototype;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Objects;

import static com.usoroos.usorosyncprototype.TCP.TCPServer.mClip;

public class ClipboardService extends Service {
    private ClipboardManager clipBoard;
    private static boolean bHasClipChangedListener = false;
    public static boolean mRunning = false;
    private String mPreText = "";
    final ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener;
    private final String TAG = "ClipboardService";

    public ClipboardService() {
        {
            mPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
                public void onPrimaryClipChanged() {
                    ClipData clipData = clipBoard.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                    String text = "CB" + item.getText().toString();
                    if (!Objects.equals(text, mClip)) {
                        if (!Objects.equals(mPreText, text))
                            ReceiveActivity.sendMessage(text);
                    }
                    mPreText = text;
                }
            };
        }
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Started");
        mRunning = true;
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        RegPrimaryClipChanged();

    }

    private void RegPrimaryClipChanged() {
        if (!bHasClipChangedListener) {
            clipBoard.addPrimaryClipChangedListener(mPrimaryClipChangedListener);
            bHasClipChangedListener = true;
        }
    }

    private void UnRegPrimaryClipChanged() {
        if (bHasClipChangedListener) {
            clipBoard.removePrimaryClipChangedListener(mPrimaryClipChangedListener);
            bHasClipChangedListener = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UnRegPrimaryClipChanged();
        mRunning = false;
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}