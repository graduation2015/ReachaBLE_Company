package jp.ac.it_college.std.ikemen.reachable.company.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import jp.ac.it_college.std.ikemen.reachable.company.R;

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

    /**
     * CardViewのトグル処理を実行する
     * @param checked Viewがチェックされている場合trueを渡す
     */
    private void toggleCardView(boolean checked) {
        //CardViewの背景色を変更する
        setCardBackgroundColor(checked ? Color.LTGRAY : Color.WHITE);

        //CheckCircleの可視性を変更する
        View checkCircle = findViewById(R.id.img_check_circle);
        if (checkCircle != null) {
            checkCircle.setVisibility(checked ? VISIBLE : GONE);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsChecked != checked) {
            mIsChecked = checked;
            toggleCardView(mIsChecked);
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
