package com.aidl.slidingdelete.View;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2016/4/22.
 */
public class SlideDelete extends FrameLayout {

    private View contentView;
    private View deleteView;
    private int contentHeight;
    private int contentWidth;
    private int deleteHeight;
    private int deleteWidth;
    private ViewDragHelper viewDragHelper;
    private boolean result;

    public SlideDelete(Context context) {
        super(context);
        init();
    }
    public SlideDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideDelete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化的方法
     */
    private void init() {
        viewDragHelper = ViewDragHelper.create(this,new callback());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView  =  getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentHeight = contentView.getMeasuredHeight();
        contentWidth = contentView.getMeasuredWidth();
        deleteHeight = deleteView.getMeasuredHeight();
        deleteWidth = deleteView.getMeasuredWidth();
    }

    //在ViewGroup中的设计
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        contentView.layout(0,0,contentWidth,contentHeight);
        deleteView.layout(contentWidth,0,contentWidth+deleteWidth,deleteHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    public  class callback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contentView || child ==deleteView;
        }

        //水平方向上的移动
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 100;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView){
                if (left<-deleteWidth){
                    left = -deleteWidth;
                }else if (left>0){
                    left = 0 ;
                }
            }
            if (child == deleteView){
                if (left < (contentWidth -deleteWidth)){
                    left = (contentWidth -deleteWidth);
                }
                if (left>contentWidth){
                    left = contentWidth;
                }
            }
            return left;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (releasedChild == contentView ){
                if (contentView.getLeft()<-deleteWidth/2){
                    opendeldete();
                }else if (contentView.getLeft()>-deleteWidth/2){
                    //关闭
                    closedelete();

                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView== contentView){
                int newLeft = contentView.getRight() + dx;
                deleteView.layout(newLeft,contentView.getTop(),newLeft+deleteWidth,contentView.getBottom());
            }
            if (changedView == deleteView){
                int newRight = deleteView.getLeft()+dx;
                contentView.layout(newRight-contentWidth,contentView.getTop(),newRight,contentView.getBottom());
            }
        }
    }
    //关闭的操作
    public void opendeldete() {
        viewDragHelper.smoothSlideViewTo(contentView,0,contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }
    //打开的操作
    public void closedelete(){
        viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
