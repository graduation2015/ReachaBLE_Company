package jp.ac.it_college.std.ikemen.reachable.company.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Checkable;

/**
 * チェック処理に対応したCardViewクラス
 */
public class CheckableCardView extends CardView implements Checkable {
    private boolean mIsChecked;
    private static final int[] CHECKED_STATE = { android.R.attr.state_checked };

    public CheckableCardView(Context context) {
        super(context);
    }

    public CheckableCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsChecked != checked) {
            mIsChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mIsChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        if (mIsChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE);
        }

        return drawableState;
    }
}
