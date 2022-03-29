package utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by User on 3/18/2018.
 */

public class NoneSwipeAbleViewPager extends ViewPager {

    private boolean enable = false;

    public NoneSwipeAbleViewPager(Context context) {
        super(context);
    }

    public NoneSwipeAbleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.enable && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.enable && super.onTouchEvent(ev);
    }
}
