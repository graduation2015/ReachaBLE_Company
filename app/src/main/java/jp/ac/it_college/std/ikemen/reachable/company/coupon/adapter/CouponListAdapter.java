package jp.ac.it_college.std.ikemen.reachable.company.coupon.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap.BitmapTransform;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

/**
 * RecyclerViewに表示するアイテムを管理するアダプタークラス
 */
public class CouponListAdapter extends SelectableAdapter<CouponListAdapter.CouponViewHolder>
        implements Filterable {

    private List<CouponInfo> mCouponInfoList;
    private Context mContext;

    public CouponListAdapter(Context context, List<CouponInfo> couponInfoList) {
        this.mContext = context;
        this.mCouponInfoList = new ArrayList<>(couponInfoList);
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
        holder.mCreationDate.setText(info.getFormattedCreationDate());
        ((Checkable) holder.mCardView).setChecked(isSelected(position));
        holder.mTagsView.setText(info.getCategoryToString().length() == 0
                ? getContext().getString(R.string.no_category) : info.getCategoryToString());
    }

    /**
     * Bitmapの読み込みを行う
     * @param path 読み込む画像のファイルパス
     * @param imageView 読み込んだ画像を表示するImageView
     */
    private void loadBitmap(String path, ImageView imageView) {
        Picasso.with(getContext())
                .load(new File(path))
                .transform(new BitmapTransform(
                        FileUtil.IMG_THUMBNAIL_WIDTH, FileUtil.IMG_THUMBNAIL_HEIGHT))
                .placeholder(R.drawable.placeholder)
                .into(imageView);
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
    public void add(int location, CouponInfo info) {
        int newLocation = location < getItemCount() ? location : getItemCount();
        //リストにクーポンを追加
        getCouponInfoList().add(newLocation, info);

        //追加したことを通知
        notifyItemInserted(newLocation);
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
        notifyDataSetChanged();

        return removedCoupon;
    }

    @Override
    public int getItemCount() {
        return getCouponInfoList().size();
    }

    public List<CouponInfo> getCouponInfoList() {
        return mCouponInfoList;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public Filter getFilter() {
        return new CouponFilter(this, getCouponInfoList());
    }

    protected class CouponViewHolder extends RecyclerView.ViewHolder {
        protected CardView mCardView;
        protected ImageView mCouponPic;
        protected TextView mTitleView;
        protected TextView mTagsView;
        protected TextView mCreationDate;

        public CouponViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mCouponPic = (ImageView) itemView.findViewById(R.id.img_coupon_pic);
            mTitleView = (TextView) itemView.findViewById(R.id.txt_title);
            mTagsView = (TextView) itemView.findViewById(R.id.txt_tags);
            mCreationDate = (TextView) itemView.findViewById(R.id.txt_creation_date);
        }
    }
}
