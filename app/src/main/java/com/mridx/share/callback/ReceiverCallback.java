package com.mridx.share.callback;

import java.io.IOException;

public interface ReceiverCallback {

    void onConnected(boolean connected, IOException error);

    void onComplete();

    void onStartReceiving();

    void onReceivingProgress(String p1);
}
