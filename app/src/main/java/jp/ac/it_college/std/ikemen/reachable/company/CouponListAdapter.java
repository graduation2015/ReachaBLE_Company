package jp.ac.it_college.std.ikemen.reachable.company;

import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.CouponViewHolder> {

    private List<CouponInfo> mCouponInfoList;
    private List<CouponInfo> mFilteredCouponList;
    private OnActionClickListener mActionClickListener;
    private int mLayoutResource;
    private BitmapCache mBitmapCache;

    public CouponListAdapter(List<CouponInfo> couponInfoList) {
        this(couponInfoList, R.layout.coupon_card_advertise, null);
    }

    public CouponListAdapter(List<CouponInfo> couponInfoList,
                             int layoutResource, OnActionClickListener actionClickListener) {
        this.mCouponInfoList = couponInfoList;
        this.mLayoutResource = layoutResource;
        this.mActionClickListener = actionClickListener;
        this.mBitmapCache = new BitmapCache();
        this.mFilteredCouponList = mCouponInfoList;
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutResource, parent, false);

        return new CouponViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        CouponInfo info = getCouponInfoList().get(position);
        //Bitmapの読み込みを非同期で行う
        loadBitmap(info.getFilePath(), holder.mCouponPic);

        holder.mTitleView.setText(info.getTitle());
        holder.mDescriptionView.setText(info.getDescription());
        holder.mTagsView.setText(info.getCategoryToString());
        holder.mCreationDate.setText(info.getFormattedCreationDate());

        if (holder.mAdvertiseButton != null) {
            holder.mAdvertiseButton.setOnClickListener(
                    new OnActionClickHandler(holder, mActionClickListener));
        }

        if (holder.mDeleteButton != null) {
            holder.mDeleteButton.setOnClickListener(
                    new OnActionClickHandler(holder, mActionClickListener));
        }

    }

    /**
     * Bitmapの読み込みをAsyncTaskクラスで実行する
     * @param filePath 画像のファイルパス
     * @param imageView 画像をセットするImageView
     */
    private void loadBitmap(String filePath, ImageView imageView) {
        final Bitmap bitmap = mBitmapCache.getBitmapFromMemCache(filePath);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            new BitmapWorkerTask(imageView, mBitmapCache).execute(filePath);
        }

    }

    /**
     * RecyclerViewに表示するクーポンリストを切り替える
     * @param infoList RecyclerViewに表示するクーポンリスト
     */
    public void replaceList(List<CouponInfo> infoList) {
        mCouponInfoList = new ArrayList<>();
        mCouponInfoList.addAll(infoList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return getCouponInfoList().size();
    }

    public List<CouponInfo> getCouponInfoList() {
        return mCouponInfoList;
    }

    public List<CouponInfo> getFilteredCouponList() {
        return mFilteredCouponList;
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
