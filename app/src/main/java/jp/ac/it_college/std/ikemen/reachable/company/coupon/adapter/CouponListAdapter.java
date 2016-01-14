package jp.ac.it_college.std.ikemen.reachable.company.coupon.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap.BitmapCache;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap.BitmapWorkerTask;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

/**
 * RecyclerViewに表示するアイテムを管理するアダプタークラス
 */
public class CouponListAdapter extends SelectableAdapter<CouponListAdapter.CouponViewHolder>
        implements Filterable {

    private List<CouponInfo> mCouponInfoList;
    private BitmapCache mBitmapCache;

    public CouponListAdapter(List<CouponInfo> couponInfoList) {
        this.mCouponInfoList = new ArrayList<>(couponInfoList);
        this.mBitmapCache = new BitmapCache();
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
        //Bitmapの読み込みを非同期で行う
        loadBitmap(info.getFilePath(), holder.mCouponPic);

        holder.mTitleView.setText(info.getTitle());
        holder.mDescriptionView.setText(info.getDescription());
        holder.mTagsView.setText(info.getCategoryToString());
        holder.mCreationDate.setText(info.getFormattedCreationDate());
        ((Checkable) holder.mCardView).setChecked(isSelected(position));
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
            imageView.setImageResource(R.drawable.placeholder);
            new BitmapWorkerTask(imageView, mBitmapCache).execute(filePath);
        }

    }

    /**
     * RecyclerViewに表示するクーポンリストを切り替える
     * @param infoList RecyclerViewに表示するクーポンリスト
     */
    public void replaceList(List<CouponInfo> infoList) {
        mCouponInfoList = new ArrayList<>(infoList);
        notifyDataSetChanged();
    }

    /**
     * クーポンリストにクーポンを追加する
     * @param info 追加するクーポン
     */
    public void add(CouponInfo info) {
        //リストの先頭に追加
        getCouponInfoList().add(0, info);
        //追加したことを通知
        notifyItemInserted(0);
    }

    /**
     * クーポンリストからクーポンを削除する
     * @param position 削除するクーポンのリストにおけるポジション
     * @return 削除したクーポンを返す
     */
    public CouponInfo remove(int position) {
        //positionで指定されたクーポンを削除する
        CouponInfo removedCoupon = getCouponInfoList().remove(position);
        //削除したことを通知
        notifyItemRemoved(position);

        return removedCoupon;
    }

    @Override
    public int getItemCount() {
        return getCouponInfoList().size();
    }

    public List<CouponInfo> getCouponInfoList() {
        return mCouponInfoList;
    }

    @Override
    public Filter getFilter() {
        return new CouponFilter(this, getCouponInfoList());
    }

    protected class CouponViewHolder extends RecyclerView.ViewHolder {
        protected CardView mCardView;
        protected AppCompatImageView mCouponPic;
        protected AppCompatTextView mTitleView;
        protected AppCompatTextView mDescriptionView;
        protected AppCompatTextView mTagsView;
        protected AppCompatTextView mCreationDate;

        public CouponViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mCouponPic = (AppCompatImageView) itemView.findViewById(R.id.img_coupon_pic);
            mTitleView = (AppCompatTextView) itemView.findViewById(R.id.txt_title);
            mDescriptionView = (AppCompatTextView) itemView.findViewById(R.id.txt_description);
            mTagsView = (AppCompatTextView) itemView.findViewById(R.id.txt_tags);
            mCreationDate = (AppCompatTextView) itemView.findViewById(R.id.txt_creation_date);
        }
    }
}
