package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap.BitmapTransform;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

/**
 * クーポンの詳細情報を表示するActivityクラス
 */
public class CouponDetailActivity extends AppCompatActivity {

    /* Constants */
    public static final String SELECTED_ITEM = "selected:item";

    /* Views */
    private ImageView mHeaderImageView;
    private Toolbar mToolbar;
    private TextView mCreationDateView;
    private TextView mDescriptionView;
    private TextView mCategoryView;

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
        loadItem(getSelectedItem());
        setUpActionBar(getToolbar());
    }

    /**
     * Actionbarの初期設定を実行する
     * @param toolbar Actionbarにセットするツールバー
     */
    private void setUpActionBar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getSelectedItem().getTitle());
        }
    }

    /**
     * クーポン情報を読み込む
     */
    private void loadItem(CouponInfo info) {
        //作成日をセット
        getCreationDateView().setText(info.getFormattedCreationDate());
        //説明をセット
        getDescriptionView().setText(info.getDescription());
        //カテゴリーをセット
        getCategoryView().setText(info.getCategoryToString());

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
                .transform(new BitmapTransform(
                        FileUtil.IMG_THUMBNAIL_WIDTH, FileUtil.IMG_THUMBNAIL_HEIGHT))
                .noFade()
                .into(getHeaderImageView());
    }

    /**
     * フルサイズの画像を読み込む
     */
    private void loadFullSizeImage() {
        Picasso.with(getHeaderImageView().getContext())
                .load(new File(getSelectedItem().getFilePath()))
                .transform(new BitmapTransform(
                        FileUtil.IMG_FULL_SIZE_WIDTH, FileUtil.IMG_FULL_SIZE_HEIGHT))
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coupon_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //アクションバーの戻るボタン押下時の処理
                finishAfterTransition();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    public Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        return mToolbar;
    }

    public TextView getCreationDateView() {
        if (mCreationDateView == null) {
            mCreationDateView = (TextView) findViewById(R.id.txt_creation_date);
        }
        return mCreationDateView;
    }

    public TextView getDescriptionView() {
        if (mDescriptionView == null) {
            mDescriptionView = (TextView) findViewById(R.id.txt_description);
        }
        return mDescriptionView;
    }

    public TextView getCategoryView() {
        if (mCategoryView == null) {
            mCategoryView = (TextView) findViewById(R.id.txt_tags);
        }
        return mCategoryView;
    }
}
