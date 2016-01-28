package jp.ac.it_college.std.ikemen.reachable.company.coupon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

/**
 * 宣伝画面用のクーポンアダプタークラス
 */
public class AdvertiseCouponAdapter extends CouponListAdapter {

    public AdvertiseCouponAdapter(Context context, List<CouponInfo> couponInfoList) {
        super(context, couponInfoList);
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_card_advertise, parent, false);

        return new AdvertiseCouponViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        AdvertiseCouponViewHolder advertiseViewHolder = (AdvertiseCouponViewHolder) holder;
        CouponInfo info = getCouponInfoList().get(position);

        advertiseViewHolder.mDescriptionView.setText(info.getDescription());
    }

    protected class AdvertiseCouponViewHolder extends CouponViewHolder {
        protected TextView mDescriptionView;

        public AdvertiseCouponViewHolder(View itemView) {
            super(itemView);
            mDescriptionView = (TextView) itemView.findViewById(R.id.txt_description);
        }
    }
}
