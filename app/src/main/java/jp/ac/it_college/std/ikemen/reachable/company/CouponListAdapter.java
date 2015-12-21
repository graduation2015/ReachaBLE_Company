package jp.ac.it_college.std.ikemen.reachable.company;

import android.graphics.Bitmap;
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

    private static final int BITMAP_HEIGHT_SIZE = 200;
    private static final int BITMAP_WIDTH_SIZE = 200;

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
        holder.mCouponPic.setImageBitmap(decodeSampledBitmapFromFile(
                info.getFilePath(), BITMAP_WIDTH_SIZE, BITMAP_HEIGHT_SIZE));
        holder.mTitleView.setText(info.getTitle());
        holder.mDescriptionView.setText(info.getDescription());
        holder.mTagsView.setText(info.getCategoryToString());
        holder.mCreationDate.setText(info.getFormattedCreationDate());
    }

    @Override
    public int getItemCount() {
        return getCouponInfoList().size();
    }

    /**
     * 画像のサブサンプルサイズを計算して返す
     * @param options デコードした後のoptions
     * @param reqWidth 画像をセットするImageViewの横幅
     * @param reqHeight 画像をセットするImageViewの縦幅
     * @return 画像のサブサンプルサイズ
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 画像の元サイズ
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /**
     * 画像をリサイズしてBitmapで返す
     * @param filePath 読み込む画像のファイルパス
     * @param reqWidth 画像をセットするImageViewの横幅
     * @param reqHeight 画像をセットするImageViewの縦幅
     * @return サンプルサイズを計算しなおしたBitmap
     */
    private Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        // inJustDecodeBounds=true で画像のサイズをチェック
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // inSampleSize を計算
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // inSampleSize をセットしてデコード
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
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

        public CouponViewHolder(View itemView) {
            super(itemView);
            mCouponPic = (AppCompatImageView) itemView.findViewById(R.id.img_coupon_pic);
            mTitleView = (AppCompatTextView) itemView.findViewById(R.id.txt_title);
            mDescriptionView = (AppCompatTextView) itemView.findViewById(R.id.txt_description);
            mTagsView = (AppCompatTextView) itemView.findViewById(R.id.txt_tags);
            mCreationDate = (AppCompatTextView) itemView.findViewById(R.id.txt_creation_date);
        }
    }
}
