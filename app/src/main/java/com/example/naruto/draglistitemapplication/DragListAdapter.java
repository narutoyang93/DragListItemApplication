package com.example.naruto.draglistitemapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate: 2018/7/29 0029 22:10
 * @Note
 */
public abstract class DragListAdapter<T, VH extends DragListAdapter.DragListViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context context;
    protected List<T> dataList;
    protected RecyclerView recyclerView;
    private int showScrollMenuItemPosition = -1;//哪个Item侧滑菜单已打开
    private static final String TAG = "DragListAdapter";
    private int itemLayoutRes;

    public DragListAdapter(Context context, List<T> dataList, RecyclerView recyclerView, int itemLayoutRes) {
        this.context = context;
        this.dataList = dataList;
        this.recyclerView = recyclerView;
        this.itemLayoutRes = itemLayoutRes;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayoutRes, parent, false);
        DragListItem dragListItem = new DragListItem(context);//侧滑布局
        dragListItem.setContentView(v);//将item布局放入侧滑布局内
        // 实例化viewHolder
        final VH viewHolder = initViewHolder(dragListItem);
        //点击监听
        viewHolder.dragListItem.setClickable(true);
        viewHolder.dragListItem.setOnActionFinishListener(new MyScrollListener.OnActionFinishListener() {
            @Override
            public void onActionFinish(boolean isEventIntercepted) {
                if (!isEventIntercepted) {
                    onItemClick(viewHolder);
                }
            }
        });

        //删除监听
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(viewHolder);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        BindData(position, holder);
        if (position == showScrollMenuItemPosition) {
            holder.dragListItem.showMenu();//拉出侧滑菜单
        } else {
            holder.dragListItem.hideMenu();//隐藏侧滑菜单
        }

        holder.dragListItem.setHideStateChangeListener(new MyScrollListener.HideStateChangeListener() {
            @Override
            public void onChange(boolean theHideViewIsShow) {
                int headerCount = getHeaderCount();
                showScrollMenuItemPosition = theHideViewIsShow ? holder.getLayoutPosition() - headerCount : -1;
            }
        });

        holder.dragListItem.setOnInterceptActionDownListener(new MyScrollListener.OnInterceptActionDownListener() {
            @Override
            public boolean onActionDown() {
                int headerCount = getHeaderCount();
                return hideMenu(showScrollMenuItemPosition != holder.getLayoutPosition() - headerCount);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public int getShowScrollMenuItemPosition() {
        return showScrollMenuItemPosition;
    }

    public void setShowScrollMenuItemPosition(int showScrollMenuItemPosition) {
        this.showScrollMenuItemPosition = showScrollMenuItemPosition;
    }


    /**
     * 关闭侧滑菜单
     */
    public boolean hideMenu() {
        return hideMenu(true);
    }


    /**
     * 关闭侧滑菜单
     *
     * @param condition 额外的判断条件
     * @return
     */
    public boolean hideMenu(boolean condition) {
        boolean result = false;
        if (showScrollMenuItemPosition != -1 && condition) {//有Item侧滑菜单已打开
            DragListViewHolder viewHolder = (DragListViewHolder) recyclerView.findViewHolderForAdapterPosition(showScrollMenuItemPosition + getHeaderCount());
            if (viewHolder != null) {
                viewHolder.dragListItem.hideMenu();
                result = true;
            }
            showScrollMenuItemPosition = -1;
        }
        return result;
    }

    public abstract void BindData(int position, VH holder);

    public abstract VH initViewHolder(DragListItem dragListItem);

    public abstract void onItemClick(VH viewHolder);

    public abstract void onDeleteClick(VH viewHolder);

    public abstract int getHeaderCount();


    /**
     * @Purpose ViewHolder
     * @Author Naruto Yang
     * @CreateDate 2018/7/30 0030
     * @Note
     */
    public static class DragListViewHolder extends RecyclerView.ViewHolder {
        public TextView deleteButton;
        public DragListItem dragListItem;

        public DragListViewHolder(View itemView) {
            super(itemView);
            this.dragListItem = (DragListItem) itemView;
            deleteButton = itemView.findViewById(R.id.hide_delete);
        }
    }

    /**
     * 点击事件监听接口
     */
    public interface OnItemClickListener {
        void onClick(DragListViewHolder holder);
    }

    public interface OnItemDeleteClickListener {
        void onClick(DragListViewHolder holder);
    }

}
