package com.example.naruto.draglistitemapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate ${2018-07-02}
 * @Note
 */
public class DragListItem extends LinearLayout {

    private Context mContext;
    private View mHiddenDragView;
    private LinearLayout mContentView;//将包裹实际的内容
    private LinearLayout mHiddenLayout;
    private Scroller mScroller;
    private int dragOutWidth;//完全侧滑出来的距离
    private double fraction = 0.75;//触发自动侧滑的临界点
    private boolean isDrag = false;
    private MyScrollListener myScrollListener;//滑动监听
    private boolean scrollable = true;//是否允许滑动
    private int positionInList;//在列表中的位置
    private static final String TAG = "DragListItem";

    public DragListItem(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public DragListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public DragListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);

        //merge进来整个listItem，在这里可以自己定义删除按钮的显示的布局，随便按照的喜好修改都行
        mHiddenDragView = View.inflate(mContext, R.layout.hide_drag_item, this);

        mContentView = mHiddenDragView.findViewById(R.id.show_content_view);
        mHiddenLayout = mHiddenDragView.findViewById(R.id.hide_view);
        mScroller = new Scroller(mContext);

        mHiddenLayout.post(new Runnable() {
            @Override
            public void run() {
                dragOutWidth = mHiddenLayout.getWidth();
                myScrollListener.setMaxOffset(dragOutWidth);
            }
        });

        myScrollListener = new MyScrollListener(DragListItem.this);
        setOnTouchListener(myScrollListener);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        if (this.scrollable != scrollable) {
            if (scrollable) {
                setOnTouchListener(myScrollListener);
            } else {
                setOnTouchListener(null);
            }
            this.scrollable = scrollable;
        }
    }

    private boolean hsaMove = false;//该条目是否已经监听过手势的滑动，用来作为判断是否进行条目左右滑动的条件之一

    public boolean isHsaMove() {
        return hsaMove;
    }

    public void setHsaMove(boolean hsaMove) {
        this.hsaMove = hsaMove;
    }

    public void setIsDrag(boolean isDrag) {
        this.isDrag = isDrag;
    }

    public int getPositionInList() {
        return positionInList;
    }

    public void setPositionInList(int positionInList) {
        this.positionInList = positionInList;
    }

    public void setHideStateChangeListener(MyScrollListener.HideStateChangeListener listener) {
        this.myScrollListener.setHideStateChangeListener(listener);
    }

    public void setOnActionFinishListener(MyScrollListener.OnActionFinishListener onActionFinishListener) {
        this.myScrollListener.setOnActionFinishListener(onActionFinishListener);
    }

    public void setOnInterceptActionDownListener(final MyScrollListener.OnInterceptActionDownListener onInterceptActionDownListener) {
        if (scrollable) {
            this.myScrollListener.setOnInterceptActionDownListener(onInterceptActionDownListener);
        } else if (onInterceptActionDownListener != null) {
            this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        return onInterceptActionDownListener.onActionDown();
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 隐藏菜单
     */
    public void hideMenu() {
        if (myScrollListener.isTheHideViewIsShow()) {//如果隐藏菜单已被拉出
            this.scrollBy(-dragOutWidth, 0);
            myScrollListener.setTheHideViewIsShow(false);
        }
    }

    /**
     * 拉出菜单
     */
    public void showMenu() {
        if (!myScrollListener.isTheHideViewIsShow()) {//如果隐藏菜单处于隐藏状态
            this.scrollBy(dragOutWidth, 0);
            myScrollListener.setTheHideViewIsShow(true);
        }
    }

    private void autoScrollToX(int finalX, int duration) {
        mScroller.startScroll(getScrollX(), 0, finalX - getScrollX(), 0, duration);
        invalidate();
    }

    public boolean getDragState() {
        return isDrag;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 更改隐藏页的文字
     */
    public void setFirstHiddenView(CharSequence charSequence) {
        TextView textView = (TextView) mHiddenLayout.findViewById(R.id.hide_delete);
        textView.setText(charSequence);
    }

    /**
     * 给使用者添加隐藏页的视图（不仅仅是删除）
     */
    public void addHiddenView(TextView view) {
        mHiddenLayout.addView(view);
    }

    /**
     * 给使用者设置listItem的实际内容（布局中必须有一个view（或其内部view） 是relativityLayout且其内有view设置layout_alignParentRight，否则会导致侧滑UI宽度出现问题）
     */
    public void setContentView(View view) {
        mContentView.addView(view);
    }

    public View getContentView() {
        return mContentView.getChildCount() > 0 ? mContentView.getChildAt(0) : null;
    }

    public double getFraction() {
        return fraction;
    }

    public void setFraction(double fraction) {
        this.fraction = fraction;
    }

}
