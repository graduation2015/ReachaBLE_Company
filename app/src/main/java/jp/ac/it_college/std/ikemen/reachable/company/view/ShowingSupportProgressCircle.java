package jp.ac.it_college.std.ikemen.reachable.company.view;

import android.content.Context;
import android.util.AttributeSet;

import com.github.jorgecastilloprz.FABProgressCircle;

/**
 * ProgressCircleの表示中かどうかに対応したクラス
 */
public class ShowingSupportProgressCircle extends FABProgressCircle {

    private boolean mIsShown;

    public ShowingSupportProgressCircle(Context context) {
        super(context);
    }

    public ShowingSupportProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowingSupportProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShowingSupportProgressCircle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void show() {
        super.show();
        mIsShown = true;
    }

    @Override
    public void hide() {
        super.hide();
        mIsShown = false;
    }

    @Override
    public boolean isShown() {
        return mIsShown;
    }

    @Override
    public void onArcAnimationComplete() {
        super.onArcAnimationComplete();
        mIsShown = false;
    }
}
