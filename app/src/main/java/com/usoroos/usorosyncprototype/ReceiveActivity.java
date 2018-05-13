package com.usoroos.usorosyncprototype;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.usoroos.usorosyncprototype.TCP.Client;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.usoroos.usorosyncprototype.ExtractUrl.extractUrl;

@SuppressWarnings("unused")
public class ReceiveActivity extends AppCompatActivity {
    private WifiManager wifi;
    String TAG = "UsoroShareReceiver";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //Check WiFi Status
        wifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("text/")) {
                handleSendText(intent);
                finish();
            }

            if (type.startsWith("image/")) {
                handleSendImage(intent);
                finish();
            }
        }
    }

    private void handleSendImage(Intent intent) {
        Uri BitmapUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Toast.makeText(this, R.string.in_progress,
                Toast.LENGTH_SHORT).show();
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String url = extractUrl(sharedText);
        sendMessage(url);
    }


    private void sendMessage(String msg) throws IllegalArgumentException {
        if (wifi.isWifiEnabled()) {
            try {
                new Client(msg);
                Toast.makeText(this, R.string.message_sent,
                        Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, R.string.server_offline,
                        Toast.LENGTH_LONG).show();
                throw e;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(this, R.string.share_error + e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.wifi_off,
                    Toast.LENGTH_LONG).show();
        }
    }

    void sendImage(Uri BitmapUri) {
        try {
            final InputStream imageStream = getContentResolver().openInputStream(BitmapUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            String bitmap = bos.toString();
            String msg = "IMG" + bitmap;
            sendMessage(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void DisableSharing() {
        PackageManager pm = MainActivity.getContext().getPackageManager();
        ComponentName compName =
                new ComponentName("com.usoroos.usorosyncprototype", "com.usoroos.usorosyncprototype" + ".ReceiveActivity");
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    static void EnableSharing() {
        PackageManager pm = MainActivity.getContext().getPackageManager();
        ComponentName compName =
                new ComponentName("com.usoroos.usorosyncprototype", "com.usoroos.usorosyncprototype" + ".ReceiveActivity");
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

}
