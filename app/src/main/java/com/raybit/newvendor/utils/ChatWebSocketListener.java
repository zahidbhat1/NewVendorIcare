package com.raybit.newvendor.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatWebSocketListener extends WebSocketListener {

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
        Log.e("TEST","text");
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Log.e("TEST",text);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        // Handle when the WebSocket connection is closed
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
        Log.e("TEST","text");
    }
}


