package jp.ac.it_college.std.ikemen.reachable.company;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.CouponViewHolder> {

    private static final int BITMAP_HEIGHT_SIZE = 200;
    private static final int BITMAP_WIDTH_SIZE = 200;

    private List<CouponInfo> mCouponInfoList;
    private OnActionClickListener mActionClickListener;

    public CouponListAdapter(List<CouponInfo> couponInfoList) {
        this(couponInfoList, null);
    }

    public CouponListAdapter(List<CouponInfo> couponInfoList, OnActionClickListener actionClickListener) {
        this.mCouponInfoList = couponInfoList;
        this.mActionClickListener = actionClickListener;
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_card, parent, false);

        return new CouponViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CouponViewHolder holder, int position) {
        CouponInfo info = getCouponInfoList().get(position);
        holder.mCouponPic.setImageBitmap(FileUtil.decodeSampledBitmapFromFile(
                info.getFilePath(), BITMAP_WIDTH_SIZE, BITMAP_HEIGHT_SIZE));
        holder.mTitleView.setText(info.getTitle());
        holder.mDescriptionView.setText(info.getDescription());
        holder.mTagsView.setText(info.getCategoryToString());
        holder.mCreationDate.setText(info.getFormattedCreationDate());

        if (mActionClickListener != null) {
            if (holder.mAdvertiseButton != null) {
                holder.mAdvertiseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActionClickListener.onAdvertiseClick(holder.mAdvertiseButton, holder.getAdapterPosition());
                    }
                });
            }

            if (holder.mDeleteButton != null) {
                holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActionClickListener.onDeleteClick(holder.mDeleteButton, holder.getAdapterPosition());
                    }
                });
            }
        }
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
        protected AppCompatTextView mCreationDate;
        protected AppCompatButton mAdvertiseButton;
        protected AppCompatButton mDeleteButton;

        public CouponViewHolder(View itemView) {
            super(itemView);
            mCouponPic = (AppCompatImageView) itemView.findViewById(R.id.img_coupon_pic);
            mTitleView = (AppCompatTextView) itemView.findViewById(R.id.txt_title);
            mDescriptionView = (AppCompatTextView) itemView.findViewById(R.id.txt_description);
            mTagsView = (AppCompatTextView) itemView.findViewById(R.id.txt_tags);
            mCreationDate = (AppCompatTextView) itemView.findViewById(R.id.txt_creation_date);
            mAdvertiseButton = (AppCompatButton) itemView.findViewById(R.id.btn_advertise);
            mDeleteButton = (AppCompatButton) itemView.findViewById(R.id.btn_delete);
        }
    }
}
