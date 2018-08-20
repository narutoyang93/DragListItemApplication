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
    private OnItemClickListener onItemClickListener;
    private OnItemDeleteClickListener onDeleteClickListener;

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

    @Override
    public void onItemClick(MyViewHolder viewHolder) {
        onItemClickListener.onClick(viewHolder);
    }

    @Override
    public void onDeleteClick(MyViewHolder viewHolder) {
        onDeleteClickListener.onClick(viewHolder);
    }

    @Override
    public int getHeaderCount() {
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnDeleteClickListener(OnItemDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
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
