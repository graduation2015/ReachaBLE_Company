package jp.ac.it_college.std.ikemen.reachable.company;

import android.view.View;

import jp.ac.it_college.std.ikemen.reachable.company.coupon.CouponListAdapter;

/**
 * クーポンアクションボタン押下時の処理を決定するハンドラークラス
 */
public class OnActionClickHandler implements View.OnClickListener {
    private final CouponListAdapter.CouponListViewHolder mHolder;
    private final OnActionClickListener mListener;

    public OnActionClickHandler(CouponListAdapter.CouponListViewHolder mHolder,
                                OnActionClickListener mListener) {
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
                mListener.onAdvertiseClick(mHolder.getAdvertiseButton(), mHolder.getAdapterPosition());
                break;
            case R.id.btn_delete:
                mListener.onDeleteClick(mHolder.getDeleteButton(), mHolder.getAdapterPosition());
                break;
        }
    }

}
