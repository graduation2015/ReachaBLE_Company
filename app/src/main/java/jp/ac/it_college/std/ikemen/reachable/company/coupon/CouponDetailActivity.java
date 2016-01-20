package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.aws.AwsUtil;
import jp.ac.it_college.std.ikemen.reachable.company.aws.S3UploadManager;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap.BitmapTransform;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.json.JsonManager;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;
import jp.ac.it_college.std.ikemen.reachable.company.util.Utils;

/**
 * クーポンの詳細情報を表示するActivityクラス
 */
public class CouponDetailActivity extends AppCompatActivity
        implements View.OnClickListener, S3UploadManager.OnUploadListener, FABProgressListener {

    /* Constants */
    public static final String SELECTED_ITEM = "selected:item";
    public static final String SELECTED_ITEM_POSITION = "selected:position";
    public static final int RESULT_DELETE = 0x201;
    public static final int RESULT_UPLOADED = 0x202;

    /* Views */
    private ImageView mHeaderImageView;
    private Toolbar mToolbar;
    private TextView mCreationDateView;
    private TextView mDescriptionView;
    private TextView mCategoryView;
    private FABProgressCircle mProgressCircle;
    private CoordinatorLayout mCoordinatorLayout;

    /* Coupon */
    private CouponInfo mSelectedItem;

    /* Json */
    private JsonManager mJsonManager;


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

        //JsonManagerのインスタンスを生成
        mJsonManager = new JsonManager(this);

        //FABProgressCircleのOnclickListenerを設定する
        getProgressCircle().setOnClickListener(this);
        //FABProgressCircleのattachListenerを登録
        getProgressCircle().attachListener(this);
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
        getCategoryView().setText(info.getCategoryToString().length() == 0
                ? getString(R.string.no_category)
                : info.getCategoryToString());

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
        getMenuInflater().inflate(R.menu.contextual_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //アクションバーの戻るボタン押下時の処理
                finishAfterTransition();
                return true;
            case R.id.menu_delete:
                //削除ボタン押下時の処理
                deleteCoupon();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * クーポンを削除する
     */
    private void deleteCoupon() {
        Intent data = new Intent().putExtra(SELECTED_ITEM_POSITION, getSelectedItemPosition());
        setResult(RESULT_DELETE, data);
        finishAfterTransition();
    }

    /**
     * クーポンアップロード完了をリザルトにセットしてクーポン選択画面に戻る
     */
    private void completeCouponUpload() {
        setResult(RESULT_UPLOADED);
        finishAfterTransition();
    }

    /**
     * S3バケットにクーポンをアップロードする
     */
    private void uploadCoupon() {
        //選択されたクーポンをJSONファイルに書き込む
        if (putCouponToJson(getSelectedItem())) {
            //書き込みが成功した場合クーポンファイルをS3にアップロードする
            File couponFile = new File(getSelectedItem().getFilePath());
            List<File> fileList = Arrays.asList(couponFile, getJsonManager().getFile());
            beginUpload(fileList);
        } else {
            //JSONへの書き込みが失敗した時の処理
            Toast.makeText(this, R.string.failed_to_write_coupon, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 選択されたクーポンをS3にアップロードする
     * @param files アップロードするファイルのリスト
     */
    private void beginUpload(List<File> files) {
        //FABProgressCircleを表示する
        getProgressCircle().show();

        //アップロードを実行しObserverListを取得
        new S3UploadManager(AwsUtil.getTransferUtility(this), this).upload(files);
    }

    /**
     * クーポンの情報をjsonに書き込む
     * @param info 書き込むクーポン
     * @return 書き込みが成功した場合はtrueを返す
     */
    private boolean putCouponToJson(CouponInfo info) {
        try {
            getJsonManager().putJsonObj(info);
        } catch (IOException | JSONException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

    public int getSelectedItemPosition() {
        return getIntent().getIntExtra(SELECTED_ITEM_POSITION, -1);
    }

    public JsonManager getJsonManager() {
        return mJsonManager;
    }

    public FABProgressCircle getProgressCircle() {
        if (mProgressCircle == null) {
            mProgressCircle = (FABProgressCircle) findViewById(R.id.fab_progress_circle);
        }
        return mProgressCircle;
    }

    public CoordinatorLayout getCoordinatorLayout() {
        if (mCoordinatorLayout == null) {
            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        }
        return mCoordinatorLayout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_progress_circle:
                //ProgressCircleが表示中の場合は処理を実行しない
                if (!getProgressCircle().isShown()) {
                    //UploadFAB押下時の処理
                    uploadCoupon();
                }
                break;
        }
    }

    /* アップロード完了後に呼ばれる */
    @Override
    public void onUploadCompleted() {
        //FABProgressの終了アニメーションを再生する
        getProgressCircle().beginFinalAnimation();
        //宣伝用にクーポンを保存する
        Utils.saveCouponInstance(
                this, Arrays.asList(getSelectedItem()), BaseCouponFragment.PREF_ADVERTISE_COUPON_LIST);
    }

    /* アップロードキャンセル時に呼ばれる */
    @Override
    public void onUploadCanceled(int id, TransferState transferState) {
        //FABProgressCircleを非表示にする
        getProgressCircle().hide();

        Snackbar.make(getCoordinatorLayout(), R.string.coupon_upload_canceled, Snackbar.LENGTH_SHORT)
                .show();
    }

    /* アップロード失敗時に呼ばれる */
    @Override
    public void onUploadFailed(int id, TransferState transferState) {
        getProgressCircle().hide();

        Snackbar.make(getCoordinatorLayout(), R.string.coupon_upload_failed, Snackbar.LENGTH_SHORT)
                .show();
    }

    /* クーポンアップロード完了後、FABProgressCircleのアニメーション終了時に呼ばれる */
    @Override
    public void onFABProgressAnimationEnd() {
        //アップロード完了を通知するSnackBarを表示する
        Snackbar.make(getCoordinatorLayout(), R.string.coupon_upload_completed,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.advertise, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completeCouponUpload();
                    }
                })
                .show();
    }
}
