package com.aidl.custom.veiw;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/4/22.
 */
public class MyLinearLayout extends LinearLayout {

    private SlidingMenu slidingMenu;
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSlideMenu(SlidingMenu slideMenu) {
        this.slidingMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slidingMenu!=null && slidingMenu.getSlideState()== SlidingMenu.SlideState.Open){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slidingMenu!=null && slidingMenu.getSlideState()== SlidingMenu.SlideState.Open){
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                slidingMenu.close();
            }
        }
        return super.onTouchEvent(event);
    }
}
