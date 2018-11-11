package com.duyhoang.loaddatafromurl;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask implements Runnable{
    @SuppressWarnings("FieldCanBeLocal")
    private final int CONNECTION_TIME_OUT = 5000;
    private final int READ_TIME_OUT = 5000;
    private final int MAX_READ_SIZE = 3000;

    private String url;
    private Thread thread;
    private DownloadCallback downloadCallback;
    private Handler handler;

    public void setDownloadCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    public DownloadTask(String url) {
        this.url = "http://" + url;
        handler = new Handler(Looper.getMainLooper());
    }

    public void performDownload() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        InputStream inputStream = null;


        try {
            URL myUrl = new URL(url);
            connection = (HttpURLConnection) myUrl.openConnection();
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            inputStream = connection.getInputStream();
            if(inputStream != null) {
                final String result = readInputStream(inputStream);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(downloadCallback != null)
                            downloadCallback.onDownloadFinished(result);
                    }
                });
            }
        } catch (final IOException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(downloadCallback != null)
                        downloadCallback.onDownloadFailed(e.getMessage());
                }
            });

        } finally {
            try {
                if(inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(connection != null) connection.disconnect();

        }




    }

    private String readInputStream(InputStream inputStream) throws IOException {

        int remainReadSize = MAX_READ_SIZE;
        int readSize;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] rawBuffer = new char[MAX_READ_SIZE];
        StringBuilder stringBuilder = new StringBuilder();

        while((readSize = bufferedReader.read(rawBuffer)) != -1 && remainReadSize > 0) {
            stringBuilder.append(rawBuffer, 0, readSize);
            remainReadSize -= readSize;
        }

        return stringBuilder.toString();
    }


    public void finish() {
        if(thread.isInterrupted())
            thread.interrupt();
    }


}
