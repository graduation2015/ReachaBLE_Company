package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.transition.Transition;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

/**
 * クーポンの詳細情報を表示するActivityクラス
 */
public class CouponDetailActivity extends Activity {

    /* Constants */
    public static final String SELECTED_ITEM = "selected:item";
    public static final String THUMBNAIL_WIDTH = "thumbnail:width";
    public static final String THUMBNAIL_HEIGHT = "thumbnail:height";

    /* Views */
    private ImageView mHeaderImageView;
    private int mThumbnailWidth;
    private int mThumbnailHeight;

    /* Coupon */
    private CouponInfo mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        initSettings();
    }

    /**
     * 初期設定を実行
     */
    private void initSettings() {
        mThumbnailWidth = getIntent().getIntExtra(THUMBNAIL_WIDTH, 0);
        mThumbnailHeight = getIntent().getIntExtra(THUMBNAIL_HEIGHT, 0);
        loadItem();
    }

    /**
     * クーポン情報を読み込む
     */
    private void loadItem() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            loadThumbnail();
        } else {
            loadFullSizeImage();
        }
    }

    /**
     * サムネイル画像を読み込む
     */
    private void loadThumbnail() {
        Picasso.with(getHeaderImageView().getContext())
                .load(new File(getSelectedItem().getFilePath()))
                .resize(mThumbnailWidth, mThumbnailHeight)
                .noFade()
                .into(getHeaderImageView());
    }

    /**
     * フルサイズの画像を読み込む
     */
    private void loadFullSizeImage() {
        Picasso.with(getHeaderImageView().getContext())
                .load(new File(getSelectedItem().getFilePath()))
                .noFade()
                .noPlaceholder()
                .into(getHeaderImageView());
    }

    /**
     * TransitionListerを登録する
     * @return 正常に処理が完了した場合はtrueを返す
     */
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    loadFullSizeImage();

                    transition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });

            return true;
        }

        return false;
    }

    public ImageView getHeaderImageView() {
        if (mHeaderImageView == null) {
            mHeaderImageView = (ImageView) findViewById(R.id.img_coupon_pic);
        }
        return mHeaderImageView;
    }

    public CouponInfo getSelectedItem() {
        if (mSelectedItem == null) {
            mSelectedItem = (CouponInfo) getIntent().getSerializableExtra(SELECTED_ITEM);
        }
        return mSelectedItem;
    }
}
