package jp.ac.it_college.std.ikemen.reachable.company;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.json.JsonManager;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

public class CouponUploadActivity extends AppCompatActivity
        implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    /* Views */
    private Toolbar mToolbar;
    private ImageView mImageView;
    private TextInputLayout mTitleWrapper;
    private TextInputLayout mDescriptionWrapper;
    private TextInputLayout mTagsWrapper;

    /* coupon */
    private String mCouponPath;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_upload);

        initSettings();
    }

    private void initSettings() {
        setUpToolbar();
        setCouponPreview();
    }

    /**
     * 選択されたクーポン画像をプレビューにセット
     */
    private void setCouponPreview() {
        try {
            String path = FileUtil.getPath(this, getIntent().getData());
            setCouponPath(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        getImageView().setImageBitmap(BitmapFactory.decodeFile(getCouponPath()));
    }


    private void setUpToolbar() {
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        return mToolbar;
    }

    public ImageView getImageView() {
        if (mImageView == null) {
            mImageView = (ImageView) findViewById(R.id.img_coupon_preview);
        }
        return mImageView;
    }

    public String getCouponPath() {
        return mCouponPath;
    }

    public void setCouponPath(String couponPath) {
        this.mCouponPath = couponPath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //SENDメニューを表示
        getMenuInflater().inflate(R.menu.menu_coupon_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send:
                //sendボタン押下時の処理
                putCouponInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public TextInputLayout getTitleWrapper() {
        if (mTitleWrapper == null) {
            mTitleWrapper = (TextInputLayout) findViewById(R.id.textInputLayout_coupon_title);
        }
        return mTitleWrapper;
    }

    public TextInputLayout getDescriptionWrapper() {
        if (mDescriptionWrapper == null) {
            mDescriptionWrapper =
                    (TextInputLayout) findViewById(R.id.textInputLayout_coupon_description);
        }
        return mDescriptionWrapper;
    }

    public TextInputLayout getTagsWrapper() {
        if (mTagsWrapper == null) {
            mTagsWrapper = (TextInputLayout) findViewById(R.id.textInputLayout_coupon_tag);
        }
        return mTagsWrapper;
    }

    /**
     * jsonファイルにクーポンの情報をセット
     * @return jsonファイルに書き込みが完了したかどうかを返却
     */
    private boolean putCouponInfo() {
        //タイトルを取得
        String title = trim(getTitleWrapper().getEditText().getText().toString());
        //タイトルのバリデートチェック
        if (!validateTitle(title)) {
            getTitleWrapper().setError(getString(R.string.validate_title_error));
            return false;
        } else {
            getTitleWrapper().setError(null);
            getTitleWrapper().setErrorEnabled(false);
        }

        //クーポンの説明を取得
        String description = trim(getDescriptionWrapper().getEditText().getText().toString());

        //クーポンのタグを取得
        String tags = trim(getTagsWrapper().getEditText().getText().toString());
        //タグをリストに変換
        List<String> tagsList = Arrays.asList(tags.replaceAll("　", " ").split(" "));

        //CouponInfoインスタンスを生成
        CouponInfo couponInfo = new CouponInfo(title, description, tagsList);

        //クーポンの情報をjsonに書き込む
        JsonManager manager = new JsonManager(this);
        try {
            manager.putJsonObj(couponInfo);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 全角/半角スペースが前後にある文字列をトリムして返す
     * @param value
     * @return
     */
    private String trim(String value) {
        return value.replaceAll("^[\\s　]*", "").replaceAll("[\\s　]*$", "");
    }

    /**
     * クーポンタイトルをバリデーションする
     * @param title
     * @return
     */
    private boolean validateTitle(String title) {
        return !title.isEmpty();
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
        if (getProgressDialog().getProgress() >= getProgressDialog().getMax()) {
            Toast.makeText(
                    this, getString(R.string.coupon_upload_completed), Toast.LENGTH_SHORT).show();

            //ProgressDialogを破棄
            mProgressDialog = null;
        }

    }
}
