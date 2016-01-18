package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.aws.AwsUtil;
import jp.ac.it_college.std.ikemen.reachable.company.aws.S3UploadManager;
import jp.ac.it_college.std.ikemen.reachable.company.aws.UploadObservers;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.bitmap.BitmapTransform;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.json.JsonManager;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

/**
 * クーポンの詳細情報を表示するActivityクラス
 */
public class CouponDetailActivity extends AppCompatActivity
        implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

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

    /* Coupon */
    private CouponInfo mSelectedItem;
    private ProgressDialog mProgressDialog;

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
            case R.id.menu_coupon_upload:
                //Advertiseボタン押下時の処理
                uploadCoupon();
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
     * クーポンアップロード完了をリザルトをセットしてクーポン選択画面に戻る
     */
    private void completeCouponUpload() {
        Intent data = new Intent().putExtra(SELECTED_ITEM, getSelectedItem());
        setResult(RESULT_UPLOADED, data);
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
        //アップロードを実行しObserverListを取得
        List<TransferObserver> observerList = new S3UploadManager(this,
                AwsUtil.getTransferUtility(this), files).execute(getProgressDialog());
        //UploadObserversを生成
        UploadObservers uploadObservers = new UploadObservers(observerList);

        //ProgressDialogの最大値にアップロードするファイルの合計サイズをセット
        getProgressDialog().setMax((int) uploadObservers.getBytesTotal());
        //ProgressDialogを表示
        getProgressDialog().show();
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

    /**
     * ProgressDialogを生成して返す
     * @return progressDialog
     */
    public ProgressDialog getProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle(getString(R.string.dialog_title_coupon_upload));
            mProgressDialog.setMessage(getString(R.string.dialog_message_coupon_upload));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.setOnCancelListener(this);
            mProgressDialog.setOnDismissListener(this);
        }
        return mProgressDialog;
    }

    /**
     * ProgressDialogが中止されたタイミングで呼ばれる
     * @param dialog
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        Toast.makeText(
                this, getString(R.string.coupon_upload_failed), Toast.LENGTH_SHORT).show();
    }

    /**
     * ProgressDialogが破棄されたタイミングで呼ばれる
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        //クーポンアップロード完了後の処理
        if (getProgressDialog().getProgress() >= getProgressDialog().getMax()) {
            //ProgressDialogを破棄
            mProgressDialog = null;

            //リザルトをセットしてクーポン選択画面に戻る
            completeCouponUpload();
        }
    }
}
