package com.duyhoang.loaddatafromurl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DownloadCallback, View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    EditText etUrl;
    Button btnDownload;
    TextView txtContent;
    DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        etUrl = findViewById(R.id.editText_url);
        btnDownload = findViewById(R.id.button_download);
        txtContent = findViewById(R.id.textView_content);

        btnDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_download)
            downloadDataFromUrl();
    }



    @Override
    public void onDownloadFinished(String result) {
        Toast.makeText(this, "Download sucessfully", Toast.LENGTH_SHORT).show();
        txtContent.setText(result);
        downloadTask.finish();
    }

    @Override
    public void onDownloadFailed(String message) {
        Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        txtContent.setText(message);
        downloadTask.finish();
    }

    private void downloadDataFromUrl() {
        if(Util.isInternetAvailable(this)) {
            Log.i(TAG, "Internet available");
            String url = etUrl.getText().toString();
            if(url != null && url.length() > 0) {
                downloadTask = new DownloadTask(url);
                downloadTask.setDownloadCallback(this);
                downloadTask.performDownload();
            }
            
        } else {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }

    }
}
