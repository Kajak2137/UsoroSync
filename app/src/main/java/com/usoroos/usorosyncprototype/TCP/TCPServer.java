package com.usoroos.usorosyncprototype.TCP;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.koushikdutta.async.*;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import com.usoroos.usorosyncprototype.MainActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.content.ContentValues.TAG;
import static com.usoroos.usorosyncprototype.ExtractUrl.extractUrl;

public class TCPServer {

    private InetAddress host;
    private int port;
    private String recv;

    public TCPServer() {
        try {
            this.host = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.port = 1488;

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
                    Log.i(TAG, "[Usoro Listener] Listening for data");
            }

            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                Log.i(TAG, "[Usoro Listener] Successful shutdown");

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
                OpenInApp();
            }
        });
    }

    private void OpenInApp() {
        String url = extractUrl(recv);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.getContext().startActivity(intent);
    }

    public static void stop() { AsyncServer.getDefault().stop(); }

}


