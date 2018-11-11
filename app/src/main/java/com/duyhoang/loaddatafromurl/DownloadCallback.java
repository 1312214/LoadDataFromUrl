package com.duyhoang.loaddatafromurl;

public interface DownloadCallback {

    void onDownloadFinished(String result);
    void onDownloadFailed(String message);
}
