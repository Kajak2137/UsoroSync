package com.usoroos.usorosyncprototype.TCP;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.koushikdutta.async.*;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import com.usoroos.usorosyncprototype.MyApp;
import com.usoroos.usorosyncprototype.getLocalIP;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static android.content.Context.CLIPBOARD_SERVICE;
import static com.usoroos.usorosyncprototype.ExtractUrl.extractUrl;

public class TCPServer {

    private InetAddress host;
    private int port;
    private String recv;
    private ClipData mPreText;
    public static boolean mServerRunning = false;
    public static String mClip;

    public TCPServer() {
        try {
            this.host = InetAddress.getByName(getLocalIP.getIp());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.port = 1488;
        mServerRunning = true;
        setup();
    }

    private void setup() {
        AsyncServer.getDefault().listen(host, port, new ListenCallback() {

            @Override
            public void onAccepted(final AsyncSocket socket) {
                handleAccept(socket);
            }

            @Override
            public void onListening(AsyncServerSocket socket) {
                String x = null;
                try {
                    x = InetAddress.getByName("localhost").toString();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    mServerRunning=false;
                }
                Log.i(TAG, "[Usoro Listener] Listening for data" + x);
            }

            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                Log.i(TAG, "[Usoro Listener] Successful shutdown");
                mServerRunning=false;

            }
        });
    }

    private void handleAccept(final AsyncSocket socket) {
        System.out.println("[Usoro Listener] New Connection " + socket.toString());

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                recv = new String(bb.getAllByteArray());
                System.out.println("[Usoro Listener] Received data " + recv);
                if (recv.startsWith("CB"))
                    PutInClipboard();
                else
                OpenInApp();
            }
        });
    }

    private void OpenInApp() {
        String url = extractUrl(recv);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApp.getContext().startActivity(intent);
    }

    private void PutInClipboard() {
        mClip = recv;
        String txt = recv.substring(2);
        ClipboardManager clipboard = (ClipboardManager) MyApp.getContext().getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clipData = clipboard.getPrimaryClip();
            ClipData clip = ClipData.newPlainText("UsoroClipboard", txt);
            if (!Objects.equals(mPreText, clipData))
                clipboard.setPrimaryClip(clip);
            mPreText = clip;
        }
    }

    public static void stop() { AsyncServer.getDefault().stop();
        mServerRunning = false; }

}


