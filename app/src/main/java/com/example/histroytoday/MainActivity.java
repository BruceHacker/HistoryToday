package com.example.histroytoday;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.histroytoday.base.BaseActivity;
import com.example.histroytoday.base.ContentURL;
import com.example.histroytoday.bean.HistoryBean;
import com.example.histroytoday.bean.LaoHuangLiBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int MSG_WHAT1 = 1;
    private static final int MSG_WHAT2 = 2;

    private Handler mHandler;
    private ListView mListView;
    private ImageButton mImgBtn;
    private List<HistoryBean.ResultBean> mDatas;
    private Calendar mCalendar;
    private Date mDate;
    private HistoryAdapter mAdapter;
    private HistoryBean historyBean;
    private LaoHuangLiBean.ResultBean resultBean;
    private TextView yinliTv, dayTv, weekTv, yangliTv, baijiTv, wuxingTv, chongshaTv, jishenTv, xiongshenTv, yiTv, jiTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler();
        mListView = findViewById(R.id.main_lv);
        mImgBtn = findViewById(R.id.main_imgbtn);
        mImgBtn.setOnClickListener(this);

        mDatas = new ArrayList<>();
        mAdapter = new HistoryAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, HistoryDetailActivity.class);
                // 这里因为添加了headerView导致position与item对不上了
                // 通过parent的getAdapter().getItem(position)就能取得正确的元素了
                // 当listView有headerView的时候，getAdapter()会返回一个HeaderViewListAdapter
                //这个Adapter其实是包装了我们一开始传进去的adapter。能够返回正确的listView item。
                String e_id = ((HistoryBean.ResultBean)parent.getAdapter().getItem(position)).getE_id();
                intent.putExtra("id", e_id);
                startActivity(intent);
            }
        });
        // 获取日历对象
        mCalendar = Calendar.getInstance();
        mDate = new Date();
        mCalendar.setTime(mDate);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        String todayHistoryURL = ContentURL.getTodayHistoryURL(month + "/" + day);
        loadData(todayHistoryURL);
        addHeaderAndFooterView();
    }

    private void addHeaderAndFooterView() {
        View headView = LayoutInflater.from(this).inflate(R.layout.main_headerview, null);
        initHeadView(headView);
        // mListView.addHeaderView(headView) 这种添加方式默认HeaderView是可点击的
        // 下面这种添加HeaderView的方式HeaderView是不可点击的
        mListView.addHeaderView(headView, null, false);
        View footerView = LayoutInflater.from(this).inflate(R.layout.main_footer, null);
        footerView.setTag("footer");
        footerView.setOnClickListener(this);
        mListView.addFooterView(footerView);
    }

    private void initHeadView(View headView) {
        yinliTv = headView.findViewById(R.id.main_header_tv_nongli);
        dayTv = headView.findViewById(R.id.main_header_tv_day);
        weekTv = headView.findViewById(R.id.main_header_tv_week);
        yangliTv = headView.findViewById(R.id.main_header_tv_yangli);
        baijiTv = headView.findViewById(R.id.main_header_tv_baiji);
        wuxingTv = headView.findViewById(R.id.main_header_tv_wuxing);
        chongshaTv = headView.findViewById(R.id.main_header_tv_chongsha);
        jishenTv = headView.findViewById(R.id.main_header_tv_jishen);
        xiongshenTv = headView.findViewById(R.id.main_header_tv_xiongshen);
        yiTv = headView.findViewById(R.id.main_header_tv_yi);
        jiTv = headView.findViewById(R.id.main_header_tv_ji);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = simpleDateFormat.format(mDate);
        String url = ContentURL.getLaoHuangLiURL(time);
        loadHeaderData(url);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        mDatas.clear();
        historyBean = new Gson().fromJson(response.body().string(), HistoryBean.class);
        List<HistoryBean.ResultBean> list = historyBean.getResult();
        for (int i = list.size() - 1; i > list.size() - 6; i--) {
            mDatas.add(list.get(i));
        }
        // 异步加载数据，更新UI需要用handler切到主线程
        Message message = new Message();
        message.what = MSG_WHAT1;
        mHandler.sendMessage(message);
    }

    private void loadHeaderData(String url) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mDatas.clear();
                LaoHuangLiBean laoHuangLiBean = new Gson().fromJson(response.body().string(), LaoHuangLiBean.class);
                resultBean = laoHuangLiBean.getResult();
                // 异步加载数据，更新UI需要用handler切到主线程
                Message message = new Message();
                message.what = MSG_WHAT2;
                mHandler.sendMessage(message);
            }
        });
    }

    private void setLaoHuangLi() {
        yinliTv.setText("农历 " + resultBean.getYinli());
        String[] yangliArr = resultBean.getYangli().split("-");
        String week = getWeek(Integer.parseInt(yangliArr[0]), Integer.parseInt(yangliArr[1]),
                Integer.parseInt(yangliArr[2]));
        yangliTv.setText("公历 " + yangliArr[0] + "年" + yangliArr[1] + "月" + yangliArr[2] + "日 " + week);
        dayTv.setText(yangliArr[2]);
        weekTv.setText(week);
        baijiTv.setText("彭祖百忌：" + resultBean.getBaiji());
        wuxingTv.setText("五行：" + resultBean.getWuxing());
        chongshaTv.setText("冲煞：" + resultBean.getChongsha());
        jishenTv.setText("吉神宜趋：" + resultBean.getJishen());
        xiongshenTv.setText("凶神宜避：" + resultBean.getXiongshen());
        yiTv.setText("宜：" + resultBean.getYi());
        jiTv.setText("忌：" + resultBean.getJi());
    }

    private String getWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weeks[index];
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_imgbtn) {
            popCalendarDialog();
            return;
        }
        String tag = (String) v.getTag();
        if (tag.equals("footer")) {
            Intent intent = new Intent(this, MoreHistoryActivity.class);
            if (historyBean != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("history", historyBean);
                intent.putExtras(bundle);
            }
            startActivity(intent);
        }
    }

    private void popCalendarDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // 改变老黄历的显示内容
                String time = year + "-" + (month + 1) + "-" + dayOfMonth;
                String url = ContentURL.getLaoHuangLiURL(time);
                loadHeaderData(url);
                // 改变历史上的今天的数据
                String date = (month + 1) + "/" + dayOfMonth;
                String url2 = ContentURL.getTodayHistoryURL(date);
                loadData(url2);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_WHAT1:
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_WHAT2:
                    setLaoHuangLi();
            }
        }
    }
}