package jp.ac.it_college.std.ikemen.reachable.company;

import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.CouponViewHolder>{

    private List<CouponInfo> mCouponInfoList;

    public CouponListAdapter(List<CouponInfo> couponInfoList) {
        this.mCouponInfoList = couponInfoList;
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_card, parent, false);

        return new CouponViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        CouponInfo info = getCouponInfoList().get(position);
        holder.mCouponPic.setImageBitmap(BitmapFactory.decodeFile(info.getFilePath()));
        holder.mTitleView.setText(info.getTitle());
        holder.mDescriptionView.setText(info.getDescription());
        holder.mTagsView.setText(info.getCategoryToString());
    }

    @Override
    public int getItemCount() {
        return getCouponInfoList().size();
    }

    public List<CouponInfo> getCouponInfoList() {
        return mCouponInfoList;
    }

    class CouponViewHolder extends RecyclerView.ViewHolder {
        protected AppCompatImageView mCouponPic;
        protected AppCompatTextView mTitleView;
        protected AppCompatTextView mDescriptionView;
        protected AppCompatTextView mTagsView;

        public CouponViewHolder(View itemView) {
            super(itemView);
            mCouponPic = (AppCompatImageView) itemView.findViewById(R.id.img_coupon_pic);
            mTitleView = (AppCompatTextView) itemView.findViewById(R.id.txt_title);
            mDescriptionView = (AppCompatTextView) itemView.findViewById(R.id.txt_description);
            mTagsView = (AppCompatTextView) itemView.findViewById(R.id.txt_tags);
        }
    }
}
