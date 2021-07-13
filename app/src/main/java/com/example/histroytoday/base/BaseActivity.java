package com.example.histroytoday.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.IllegalFormatCodePointException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * User: aWanShao
 * Date: 2021/7/11
 * Time: 16:54
 */
public class BaseActivity extends AppCompatActivity implements Callback {

    // 网络加载用的Okhttp
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOkHttpClient = new OkHttpClient.Builder().build();
    }

    public void loadData(String url) {
        Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(this);
    }


    @Override
    public void onFailure(Call call, IOException e) {
        loadDataFailured();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }

    private void loadDataFailured() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
