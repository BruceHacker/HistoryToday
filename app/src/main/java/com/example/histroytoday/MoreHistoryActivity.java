package com.example.histroytoday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.histroytoday.bean.HistoryBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoreHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private ImageView backImg;
    private TextView emptyTv;
    private List<HistoryBean.ResultBean> mDatas;
    private HistoryAdapter mAdapter;
    private HistoryBean historyBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_history);
        backImg = findViewById(R.id.history_iv_back);
        backImg.setOnClickListener(this);
        listView = findViewById(R.id.more_history_lv);
        emptyTv = findViewById(R.id.history_no_data);
        mDatas = new ArrayList<>();
        mAdapter = new HistoryAdapter(this, mDatas);
        listView.setAdapter(mAdapter);
        try {
            Bundle bundle = getIntent().getExtras();
            historyBean = (HistoryBean) bundle.getSerializable("history");
            mDatas.addAll(historyBean.getResult());
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            emptyTv.setVisibility(View.VISIBLE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MoreHistoryActivity.this, HistoryDetailActivity.class);
                String e_id = mDatas.get(position).getE_id();
                intent.putExtra("id", e_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.history_iv_back) {
            finish();
        }
    }
}