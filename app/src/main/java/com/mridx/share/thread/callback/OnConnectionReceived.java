package com.mridx.share.thread.callback;

public interface OnConnectionReceived {
    void onReceived(boolean success, String ip, int port);
}
