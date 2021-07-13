package com.example.histroytoday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.histroytoday.bean.HistoryBean;

import java.util.List;

/**
 * User: aWanShao
 * Date: 2021/7/8
 * Time: 21:20
 */
public class HistoryAdapter extends BaseAdapter {
    Context mContext;
    private List<HistoryBean.ResultBean> mDatas;

    public HistoryAdapter(Context context, List<HistoryBean.ResultBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_main_timeline, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HistoryBean.ResultBean resultBean = mDatas.get(position);
        if (position != 0) {
            HistoryBean.ResultBean lastResultBean = mDatas.get(position - 1);
            if (resultBean.getDate() == lastResultBean.getDate()) {
                viewHolder.timeLayout.setVisibility(View.GONE);
            } else {
                viewHolder.timeLayout.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.timeLayout.setVisibility(View.VISIBLE);
        }
        viewHolder.timeView.setText(resultBean.getDate());
        viewHolder.titleView.setText(resultBean.getTitle());

        return convertView;
    }

    class ViewHolder {
        TextView timeView, titleView;
        LinearLayout timeLayout;

        public ViewHolder(View itemView) {
            timeView = itemView.findViewById(R.id.item_main_time);
            titleView = itemView.findViewById(R.id.item_main_title);
            timeLayout = itemView.findViewById(R.id.item_main_ll);
        }
    }
}
