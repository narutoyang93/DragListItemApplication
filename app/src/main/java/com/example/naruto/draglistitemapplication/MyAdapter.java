package com.example.naruto.draglistitemapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate: 2018/7/29 0029 22:10
 * @Note
 */
public class MyAdapter extends DragListAdapter<String, MyAdapter.MyViewHolder> {

    public MyAdapter(Context context, List<String> dataList, RecyclerView recyclerView, int itemLayoutRes) {
        super(context, dataList, recyclerView, itemLayoutRes);
    }

    @Override
    public void BindData(int position, MyViewHolder holder) {
        holder.tvData.setText(dataList.get(position));
    }


    @Override
    public MyViewHolder initViewHolder(DragListItem dragListItem) {
        return new MyViewHolder(dragListItem);
    }

    /**
     * @Purpose
     * @Author Naruto Yang
     * @CreateDate 2018/8/4 0004
     * @Note
     */
    class MyViewHolder extends DragListAdapter.DragListViewHolder {
        private TextView tvData;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tv_data);
        }
    }
}
