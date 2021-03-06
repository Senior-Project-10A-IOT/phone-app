package com.example.testapp;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketListener;

public class WebsocketWrapper {
    private static final String REMOTE_SERVER = "ws://gang-and-friends.com:8765/phone";
    private static final String LOCAL_SERVER = "ws://10.0.2.2:8765/phone";

    private static WebSocketListener listener;
    private static WebSocket ws;
    private static boolean connected = false;

    public static boolean connect(WebSocketListener listener, boolean isRemote) {
        try {
            if (ws != null) {
                ws.disconnect();
            }

            SecurityApplication.factory.setConnectionTimeout(1000);
            ws = SecurityApplication.factory.createSocket(currentServer(isRemote));
            WebsocketWrapper.listener = listener;

            ws.addListener(listener);
            ws.connect();

            connected = true;
        } catch (Exception e) {
            connected = false;
        }

        return connected;
    }

    public static void disconnect() {
        if (ws != null) {
            ws.disconnect();
        }

        connected = false;
    }

    public static String currentServer(boolean isRemote) {
        if (isRemote) {
            return REMOTE_SERVER;
        } else {
            return LOCAL_SERVER;
        }
    }

    public static boolean isConnected() {
        return connected;
    }

    public static void sendText(String text) {
        SecurityApplication.logDebug("send message '" + text + "'");
        ws.sendText(text);
    }
}
