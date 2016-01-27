package jp.ac.it_college.std.ikemen.reachable.company.view.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * RecyclerViewのアイテムクリックリスナークラス
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector mGestureDetector;
    private OnItemClickListener mListener;
    private View mChildView;
    private int mChildViewPosition;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        // タッチした箇所のViewを取得
        mChildView = view.findChildViewUnder(e.getX(), e.getY());
        // タッチした箇所のViewのポジションを取得
        mChildViewPosition = view.getChildAdapterPosition(mChildView);
        // タッチした箇所のViewのonTouchEventを発生させる
        mGestureDetector.onTouchEvent(e);

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        // Do nothing
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * クーポンリストアイテムクリック時のリスナークラス
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongPress(View view, int position);
    }

    /**
     * RecyclerViewのアイテムクリックイベントをリスナーに通知するクラス
     */
    protected class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mChildView != null && mListener != null) {
                mListener.onItemClick(mChildView, mChildViewPosition);
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mChildView != null && mListener != null) {
                mListener.onItemLongPress(mChildView, mChildViewPosition);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
