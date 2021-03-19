package org.flying.keycreateor.Interface;

public interface SocketCallBack {

    void onStart();

    void onSuccess(String result);

    void onFailure(String error);

    void onFinished();

}
