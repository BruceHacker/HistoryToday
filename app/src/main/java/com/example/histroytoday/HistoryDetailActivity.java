package com.example.histroytoday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.histroytoday.base.BaseActivity;
import com.example.histroytoday.base.ContentURL;
import com.example.histroytoday.bean.HistoryDetailBean;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;

public class HistoryDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_WHAT = 1;

    private ImageView backView;
    private ImageView shareView;
    private TextView titleTv;
    private TextView contentView;
    private ImageView picture;
    private HistoryDetailBean.ResultBean resultBean;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        backView = findViewById(R.id.history_detail_back);
        shareView = findViewById(R.id.history_detail_share);
        backView.setOnClickListener(this);
        shareView.setOnClickListener(this);
        titleTv = findViewById(R.id.hsitory_detail_title);
        contentView = findViewById(R.id.history_detail_content);
        picture = findViewById(R.id.history_detail_pic);
        mHandler = new MyHandler();
        String e_id = getIntent().getStringExtra("id");
        String url = ContentURL.getHistoryDetail(e_id);
        loadData(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_detail_back:
                finish();
                break;
            case R.id.history_detail_share:
                String text = "我发现一款好用的软件--历史上的今天，快来一起探索这个app吧";
                if (resultBean != null) {
                    text = "想要了解" + resultBean.getTitle() + "详情吗？快来下载历史上今天这个app吧";
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        HistoryDetailBean historyDetailBean = new Gson().fromJson(response.body().string(), HistoryDetailBean.class);
        resultBean = historyDetailBean.getResult().get(0);
        // 异步加载数据，更新UI需要用handler切到主线程
        Message message = new Message();
        message.what = MSG_WHAT;
        mHandler.sendMessage(message);
    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_WHAT) {
                titleTv.setText(resultBean.getTitle());
                contentView.setText(resultBean.getContent());
                if (resultBean.getPicUrl().size() != 0) {
                    String picUrl = resultBean.getPicUrl().get(0).getUrl();
                    if (TextUtils.isEmpty(picUrl)) {
                        picture.setVisibility(View.GONE);
                    } else {
                        picture.setVisibility(View.VISIBLE);
                        Picasso.get().load(picUrl).into(picture);
                    }
                } else {
                    picture.setVisibility(View.GONE);
                }
            }
        }
    }
}