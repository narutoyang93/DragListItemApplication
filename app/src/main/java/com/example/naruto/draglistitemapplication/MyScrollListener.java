package com.example.naruto.draglistitemapplication;

import android.view.MotionEvent;
import android.view.View;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate ${2018-07-02}
 * @Note
 */
public class MyScrollListener implements View.OnTouchListener {
    private float downX;//初始触摸点X坐标
    private float preX;//上一个触摸点X坐标
    private View aimView;//目标视图，需要随着手指滑动的视图
    private boolean theHideViewIsShow = false;//隐藏视图是否处于不可见状态
    private float offsetSumX_finger;//手指位移，向左为正，向右为负
    private int offsetSumX_view;//目标视图相对于可见状态时的X轴偏移量
    private int maxOffset = -1;//最大移动距离
    private static final float VALID_SCROLL_SCALE = 0.5f;//有效移动比例，小于这个值则回滚
    private boolean isHaveTriggerDown = false;//是否触发了ACTION_DOWN事件（有时触发了ACTION_MOVE却没有检测到ACTION_DOWN）
    private float currentX;//当前触摸点坐标
    private float offsetX;//当前ACTION_MOVE事件的偏移量
    private HideStateChangeListener hideStateChangeListener;//监听隐藏视图状态
    private OnInterceptActionDownListener onInterceptActionDownListener;//判断是否拦截ActionDown事件


    public MyScrollListener(View aimView, int maxOffset) {
        this.aimView = aimView;
        this.maxOffset = maxOffset;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        boolean b = false;//是否拦截本次事件
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (onInterceptActionDownListener != null) {
                    if (onInterceptActionDownListener.onActionDown()) {
                        b = true;
                        break;
                    }
                }
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                b = actionUp(event);
                break;
        }
        return b;
    }

    /**
     * ACTION_DOWN 处理
     *
     * @param event
     */
    public void actionDown(MotionEvent event) {
        downX = event.getRawX();
        preX = event.getRawX();
        offsetSumX_finger = 0;
        offsetSumX_view = theHideViewIsShow ? maxOffset : 0;
        isHaveTriggerDown = true;
    }

    /**
     * ACTION_MOVE 处理
     *
     * @param event
     */
    public void actionMove(MotionEvent event) {
        if (!isHaveTriggerDown) {
            actionDown(event);//由于没有触发ACTION_DOWN，本次ACTION_MOVE代替ACTION_DOWN调用actionDown(event)初始化数据
            return;
        }
        currentX = event.getRawX();
        offsetX = preX - currentX;
        offsetSumX_finger += offsetX;
        //System.out.println("--->maxOffset=" + maxOffset + ";offsetX=" + offsetX + ";offsetSumX_view=" + offsetSumX_view + ";offsetSumX_finger=" + offsetSumX_finger);
        if (offsetX > 0) {//往左拉
            if (offsetSumX_view == maxOffset) {//已经是最大偏移量，不能再往左拉了

            } else {
                if (offsetSumX_view < maxOffset) {//view总位移小于最大位移，还可以继续移动
                    if (offsetSumX_finger > maxOffset) {//手指总位移大于最大位移，最后移动一次，使总位移等于最大位移
                        aimView.scrollBy(maxOffset - offsetSumX_view, 0);
                        offsetSumX_view = maxOffset;
                    } else {
                        aimView.scrollBy((int) offsetX, 0);
                        offsetSumX_view += (int) offsetX;
                    }
                }
            }
        } else {//往右拉
            if (offsetSumX_view == 0) {//已经拉回原位，不能继续往右拉了

            } else {//跟随手指移动直至偏移量为0
                if (offsetSumX_view > 0) {//view总位移大于0，还可以继续移动
                    if (offsetSumX_finger < -maxOffset) {//手指总位移大于最大偏移量，最后移动一次，使总位移等于0
                        aimView.scrollBy(-offsetSumX_view, 0);
                        offsetSumX_view = 0;
                    } else {
                        aimView.scrollBy((int) offsetX, 0);
                        offsetSumX_view += (int) offsetX;
                    }
                }
            }
        }
        preX = currentX;
    }

    /**
     * ACTION_UP 处理
     *
     * @param event
     */
    public boolean actionUp(MotionEvent event) {
        boolean b = true;//是否拦截本次事件
        if (!isHaveTriggerDown) {
            return true;
        }
        if (offsetSumX_view == 0) {
            theHideViewIsShow = false;
        } else if (offsetSumX_view == maxOffset) {
            theHideViewIsShow = true;
        } else {
            currentX = event.getRawX();
            if (downX - currentX > 0) {//往左移动了
                if (offsetSumX_view < maxOffset * VALID_SCROLL_SCALE) {//小于有效滚动比例，回滚
                    aimView.scrollBy(-offsetSumX_view, 0);
                    theHideViewIsShow = false;
                } else {
                    aimView.scrollBy(maxOffset - offsetSumX_view, 0);
                    theHideViewIsShow = true;
                }
            } else {//往右移动了
                if (maxOffset - offsetSumX_view < maxOffset * VALID_SCROLL_SCALE) {//小于有效滚动比例，回滚
                    aimView.scrollBy(maxOffset - offsetSumX_view, 0);
                    theHideViewIsShow = true;
                } else {
                    aimView.scrollBy(-offsetSumX_view, 0);
                    theHideViewIsShow = false;
                }
            }
        }
        isHaveTriggerDown = false;
        b = !(!theHideViewIsShow && event.getAction() == MotionEvent.ACTION_UP && Math.abs(offsetSumX_finger) < 50);//如果没有show,触发up事件，若手指滑动位移小于50，可认为是点击，不拦截事件
        if (hideStateChangeListener != null) {
            hideStateChangeListener.onChange(theHideViewIsShow);
        }
        return b;
    }

    public boolean isTheHideViewIsShow() {
        return theHideViewIsShow;
    }

    public void setTheHideViewIsShow(boolean theHideViewIsShow) {
        this.theHideViewIsShow = theHideViewIsShow;
    }

    public void setHideStateChangeListener(HideStateChangeListener listener) {
        this.hideStateChangeListener = listener;
    }

    public void setOnInterceptActionDownListener(OnInterceptActionDownListener onInterceptActionDownListener) {
        this.onInterceptActionDownListener = onInterceptActionDownListener;
    }

    /**
     * 隐藏视图状态监听
     */
    public static interface HideStateChangeListener {
        void onChange(boolean theHideViewIsShow);
    }

    /**
     * 判断是否拦截ActionDown事件（类似列表item侧滑删除的功能需要用到，当有某条Item A侧滑菜单已打开，触摸其他Item B则使A的侧滑菜单隐藏，同时拦截本次事件）
     */
    public static interface OnInterceptActionDownListener {
        boolean onActionDown();
    }

}
