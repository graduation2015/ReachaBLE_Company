package jp.ac.it_college.std.ikemen.reachable.company;

import android.view.View;

/**
 * クーポンアクションボタン押下時の処理を決定するハンドラークラス
 */
public class OnActionClickHandler implements View.OnClickListener {
    private final CouponAdapter.CouponViewHolder mHolder;
    private final OnActionClickListener mListener;

    public OnActionClickHandler(CouponAdapter.CouponViewHolder mHolder, OnActionClickListener mListener) {
        this.mHolder = mHolder;
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.btn_advertise:
                mListener.onAdvertiseClick(mHolder.mAdvertiseButton, mHolder.getAdapterPosition());
                break;
            case R.id.btn_delete:
                mListener.onDeleteClick(mHolder.mDeleteButton, mHolder.getAdapterPosition());
                break;
        }
    }

}
