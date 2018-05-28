package com.usoroos.usorosyncprototype;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.usoroos.usorosyncprototype.TCP.Client;

import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static com.usoroos.usorosyncprototype.ExtractUrl.extractUrl;
import static com.usoroos.usorosyncprototype.R.string;
import static com.usoroos.usorosyncprototype.R.string.message_sent;
import static com.usoroos.usorosyncprototype.R.string.server_offline;
import static com.usoroos.usorosyncprototype.R.string.share_error;
import static com.usoroos.usorosyncprototype.R.string.wifi_off;

public class ReceiveActivity extends AppCompatActivity {
    private static final String TAG = "UsoroShareReceiver";
    static WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

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
       // Uri BitmapUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Toast.makeText(this, R.string.in_progress,
                Toast.LENGTH_SHORT).show();
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String url = extractUrl(sharedText);
        sendMessage(url);
    }

    static void sendMessage(String msg) throws IllegalArgumentException {
        wifi = (WifiManager) MyApp.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = wifi.isWifiEnabled();
        if (isWifiEnabled) {
            try {
                new Client(msg);
                Log.i(TAG, "Sent Message");
                Toast.makeText(MyApp.getContext(), message_sent,
                        LENGTH_SHORT).show();
            } catch (IllegalArgumentException ae) {
                Log.i(TAG, "Server is Offline");
                Toast.makeText(MyApp.getContext(), server_offline,
                        LENGTH_LONG).show();
                throw ae;
            } catch (Exception e) {
                Toast.makeText(MyApp.getContext(), share_error + e.toString(),
                        LENGTH_LONG).show();
            }
        } else {
            Log.i(TAG, "No Wifi");
             Toast.makeText(MyApp.getContext(), wifi_off,
                     LENGTH_LONG).show();
     }
    }

   /* void sendImage(Uri BitmapUri) {
        try {
            final InputStream imageStream = getContentResolver().openInputStream(BitmapUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/
         /*   String bitmap = bos.toString();
            String msg = "IMG" + bitmap;
            sendMessage(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
*/
    static void DisableSharing() {
        PackageManager pm = MyApp.getContext().getPackageManager();
        ComponentName compName =
                new ComponentName("com.usoroos.usorosyncprototype", "com.usoroos.usorosyncprototype" + ".ReceiveActivity");
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    static void EnableSharing() {
        PackageManager pm = MyApp.getContext().getPackageManager();
        ComponentName compName =
                new ComponentName("com.usoroos.usorosyncprototype", "com.usoroos.usorosyncprototype" + ".ReceiveActivity");
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

}
