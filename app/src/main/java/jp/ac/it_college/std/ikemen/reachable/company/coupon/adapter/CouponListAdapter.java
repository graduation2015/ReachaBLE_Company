package jp.ac.it_college.std.ikemen.reachable.company.coupon.adapter;

import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.OnActionClickHandler;
import jp.ac.it_college.std.ikemen.reachable.company.OnActionClickListener;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.adapter.CouponAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

/**
 * クーポンリストに対応したアダプタークラス
 */
public class CouponListAdapter extends CouponAdapter {
    private OnActionClickListener mActionClickListener;

    public CouponListAdapter(List<CouponInfo> couponInfoList, OnActionClickListener actionClickListener) {
        super(couponInfoList);
        this.mActionClickListener = actionClickListener;
    }

    @Override
    public CouponAdapter.CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_card, parent, false);

        return new CouponListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        CouponListViewHolder viewHolder = (CouponListViewHolder) holder;

        if (viewHolder.mAdvertiseButton != null) {
            viewHolder.mAdvertiseButton.setOnClickListener(
                    new OnActionClickHandler(viewHolder, mActionClickListener));
        }

        if (viewHolder.mDeleteButton != null) {
            viewHolder.mDeleteButton.setOnClickListener(
                    new OnActionClickHandler(viewHolder, mActionClickListener));
        }
    }

    public class CouponListViewHolder extends CouponAdapter.CouponViewHolder {
        protected AppCompatButton mAdvertiseButton;
        protected AppCompatButton mDeleteButton;

        public CouponListViewHolder(View itemView) {
            super(itemView);
            mAdvertiseButton = (AppCompatButton) itemView.findViewById(R.id.btn_advertise);
            mDeleteButton = (AppCompatButton) itemView.findViewById(R.id.btn_delete);
        }

        public AppCompatButton getAdvertiseButton() {
            return mAdvertiseButton;
        }

        public AppCompatButton getDeleteButton() {
            return mDeleteButton;
        }
    }
}
