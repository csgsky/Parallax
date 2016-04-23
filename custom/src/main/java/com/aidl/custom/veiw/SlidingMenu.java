package com.aidl.custom.veiw;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 这边为什么需要继承FrameLayout呢？因为继承了FrameLayout以后，onMeasure的方法就不需要我们自己去实现了
 * 在父类里面会自动帮我们实现
 */
public class SlidingMenu extends FrameLayout {
    private ViewDragHelper viewDragHelper;
    View menuView;
    View mainView;
    int mianWidth;
    int mainHeight;
    int menuWidth;
    int menuHeight;
    int dragRange;
    FloatEvaluator floatEvaluator;
    public enum SlideState{
        Open,Close
    }
    private SlideState mState = SlideState.Close;//默认是关闭状态
    public SlidingMenu(Context context) {
        super(context);
        init();
    }
    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //当完成xml布局填充以后，就可以获取到子View了
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    /**
     *  当Onmeaseure的方法执行完成以后，就可以获得子view的宽和高了
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mianWidth = mainView.getMeasuredWidth();
        mainHeight = mainView.getMeasuredHeight();
        menuWidth = menuView.getMeasuredWidth();
        menuHeight = menuView.getMeasuredHeight();
        //设定DragRange的值
        dragRange = (int) (0.6*mianWidth);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
    }

    //viewdragHelper是否应该拦截事件

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }



    //viewDragEvent处理当前的事件。记住返回值要返回true，表示一直在消耗这个事件。
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private void init() {
        //处理ViewGroup中对子View的拖拽
        viewDragHelper = ViewDragHelper.create(this,new ViewDragHelpercallBack());
       floatEvaluator = new FloatEvaluator();
    }
    private class ViewDragHelpercallBack extends ViewDragHelper.Callback{
        /**
         * 判断是否捕获当前Child的触摸事件
         * @param child  当前触摸的View
         * @param pointerId 当前触摸的id，这个参数主要是多点触摸的时候使用
         * @return  返回值，返回true表示捕获当前的View，返回false表示不捕获。
         */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mainView || child == menuView ;
            }

        /**
         * 这个方法看起来是用来限制水平方向上的拖拽，实际上不是，目前该方法最好不要返回0，他的内部会判断事都大于0 ，如果大于0 ，
         * 就会进行水平移动，否则不是进行水平移动。这个方法还有就是在释放的时候来计算平滑滚动的操作。
         * @param child  表示当前捕获的子view
         * @return
         */
            @Override
            public int getViewHorizontalDragRange(View child) {
                return dragRange;
            }

            //一般是用于初始化的操作
            //capturedChild:表示当前捕获的View,这边不需要处理
            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }
        /**
         * //水平方向上的移动
         * @param child   被捕获到的子View
         * @param left    左边移动的距离
         * @param dx      移动的偏差
         * @return        返回值表示实际上移动的位置
         */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (child == mainView){
                    if (left>dragRange){
                        left = dragRange ;
                    }else if (left<0){
                        left = 0 ;
                    }
                }
                return left;
            }
        /**
         * 当位置发生改变的时候调用的方法,一般在该方法写伴随改变的操作，比如不同的子View的伴随移动
         * @param changedView  发生改变的子View
         * @param left  改变后left
         * @param top   改变后的Top
         * @param dx    水平方向上的移动
         * @param dy    垂直方法上的移动
         *     ****这个方法随着位置的不断改变在不断的执行的
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //当位置发生改变的时候，我们手动的来设置布局
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menuView){
                //保持menu的位置不变
                menuView.layout(0,0,menuWidth,menuHeight);
                int newLeft = mainView.getLeft() + dx ;
                if (newLeft > dragRange) newLeft = dragRange;
                if (newLeft < 0) newLeft = 0;
                mainView.layout(newLeft,mainView.getTop(),newLeft+mianWidth,mainView.getBottom());
            }
            //计算滑动百分比
            float fraction =  (mainView.getLeft()*1f/dragRange);

            //执行各种动画
           ExecuteAnimation(fraction);

            //执行状态改变的逻辑,思考：为什么执行要放在这边来执行，难道放在onViewReleased里面执行不可以吗？
            //最好方法这边，原因是：随着滚动，状态是在不断的发生改变的。
            if (mainView.getLeft() == 0 && mState !=SlideState.Close){
                //当前状态为关闭
                mState =SlideState.Close;
                if (listener!=null){
                    listener.slideclose();
                }
            }else if (mainView.getLeft()==dragRange && mState!=SlideState.Open){
                //当前状态为打开
                mState = SlideState.Open;
                if (listener!=null){
                    listener.slideopen();
                }
            }
                if (listener!=null){
                    listener.sliding(fraction);
                }


        }

        /**
         *   当抬起手的时候，调用的方法
         */
        @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mainView.getLeft()>dragRange/2){
                    //打开菜单
                    open();
                }else
                {
                    //关闭菜单
                    close();
                }


        }
    }

    //执行伴随动画
    private void ExecuteAnimation(float fraction) {
        //属性值的变化，因为这边的fraction是不断变化的，这边的1是开始值，0.8f是结束值
        Float evaluate = floatEvaluator.evaluate(fraction, 1, 0.8f);
        ViewCompat.setScaleX(mainView,evaluate);
        ViewCompat.setScaleY(mainView,evaluate);

        ViewCompat.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
        ViewCompat.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
        ViewCompat.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1f));
        ViewCompat.setTranslationX(menuView,floatEvaluator.evaluate(fraction,-menuWidth/2,0));
    }
    //关闭
    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView,0,0);
        ViewCompat.postInvalidateOnAnimation(this);
    }
    //打开
    public void open() {
        viewDragHelper.smoothSlideViewTo(mainView,dragRange,0);
        ViewCompat.postInvalidateOnAnimation(this);

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)){
                ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    private onSlidingMenuStateChange listener;
    public void  setOnslidingMenuStateChangeListener(onSlidingMenuStateChange listener){
        this.listener = listener;
    }
    public interface  onSlidingMenuStateChange{
        void slideopen();
        void slideclose();
        void sliding(float fraction);
    }

    public SlideState getSlideState(){
        return  mState;
    }
}
