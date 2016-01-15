package jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap.BitmapCache;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

/**
 * Bitmapに画像をセットする処理を非同期で実行するAsyncTaskクラス
 */
@Deprecated
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private static final int BITMAP_HEIGHT_SIZE = 200;
    private static final int BITMAP_WIDTH_SIZE = 200;

    private final WeakReference<ImageView> mImageViewWeakReference;
    private BitmapCache mBitmapCache;

    public BitmapWorkerTask(ImageView imageView, BitmapCache bitmapCache) {
        this.mImageViewWeakReference = new WeakReference<>(imageView);
        this.mBitmapCache = bitmapCache;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String filePath = params[0];
        final Bitmap bitmap = FileUtil.decodeSampledBitmapFromFile(
                filePath, BITMAP_WIDTH_SIZE, BITMAP_HEIGHT_SIZE);
        mBitmapCache.addBitmapToMemoryCache(filePath, bitmap);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        final ImageView imageView = mImageViewWeakReference.get();
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
