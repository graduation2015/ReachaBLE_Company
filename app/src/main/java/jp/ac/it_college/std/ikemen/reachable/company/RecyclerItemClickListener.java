package jp.ac.it_college.std.ikemen.reachable.company;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
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

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        // タッチした箇所のViewを取得
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            /* onInterceptTouchEventのタイミングだとアイテムのtouch feedbackがつく前にonItemClickが
            呼ばれてしまうので、明示的にsetPressed(true)を呼んでいます */
            childView.setPressed(true);
            int position = view.getChildAdapterPosition(childView);
            mListener.onItemClick(childView, position);

            //CardViewに配置されているアクションボタンを取得
            AppCompatButton advertiseButton = (AppCompatButton) childView.findViewById(R.id.btn_advertise);
            AppCompatButton deleteButton = (AppCompatButton) childView.findViewById(R.id.btn_delete);
            //アクションボタン押下時の処理を設定
            setUpActionButtonListener(advertiseButton, deleteButton, childView, position);
        }
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
     * 各アクションボタン押下時の処理を設定する
     * @param advertiseButton Advertiseを開始するボタン
     * @param deleteButton クーポンを削除するボタン
     * @param childView RecyclerView#findChildViewUnder(float x, float y)で取得できるView
     * @param position RecyclerView#getChildAdapterPosition(childView)で取得できるクーポンのポジション
     */
    private void setUpActionButtonListener(AppCompatButton advertiseButton, AppCompatButton deleteButton,
                                           final View childView, final int position) {
        if (advertiseButton != null) {
            advertiseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAdvertiseClick(childView, position);
                }
            });
        }

        if (deleteButton != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeleteClick(childView, position);
                }
            });
        }
    }

    /**
     * クーポンリストアイテムクリック時のリスナークラス
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onAdvertiseClick(View view, int position);
        void onDeleteClick(View view, int position);
    }

}
