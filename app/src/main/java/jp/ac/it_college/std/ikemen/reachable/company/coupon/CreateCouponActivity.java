package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;
import jp.ac.it_college.std.ikemen.reachable.company.util.Utils;

public class CreateCouponActivity extends AppCompatActivity {

    /* Constants */
    public static final String CREATED_COUPON_DATA = "created_coupon_data";

    /* Views */
    private Toolbar mToolbar;
    private ImageView mImageView;
    private TextInputLayout mTitleWrapper;
    private TextInputLayout mDescriptionWrapper;
    private TextInputLayout mTagsWrapper;

    /* coupon */
    private CouponInfo mCouponInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coupon);

        initSettings();
    }

    private void initSettings() {
        setUpToolbar();
        loadCouponImage();
    }

    /**
     * 選択されたクーポン画像を読み込む
     */
    private void loadCouponImage() {
        Picasso.with(this)
                .load(getIntent().getDataString())
                .into(getImageView());
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

    public CouponInfo getCouponInfo() {
        return mCouponInfo;
    }

    public void setCouponInfo(CouponInfo couponInfo) {
        this.mCouponInfo = couponInfo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ツールバーメニューを表示
        getMenuInflater().inflate(R.menu.create_coupon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_accept:
                //acceptボタン押下時の処理
                createCoupon();
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
     * クーポンの情報をセット
     * @return クーポン情報がセットできた場合にtrueを返す
     */
    private boolean putCouponInfo() {
        //タイトルを取得
        String title = trim(getTitleWrapper().getEditText().getText().toString());
        //タイトルのバリデートチェック
        if (!validateTitle(title)) {
            getTitleWrapper().setError(getString(R.string.error_validate_title));
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

        //CouponInfoインスタンスをセット
        setCouponInfo(new CouponInfo(FileUtil.getPath(this, Uri.parse(getIntent().getDataString())),
                title, description, tagsList));

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
     * クーポンの情報をリザルトにセットし、Activityを破棄
     */
    private void completeCreate() {
        setResult(RESULT_OK, new Intent().putExtra(CREATED_COUPON_DATA, getCouponInfo()));
        finish();
    }

    /**
     * クーポンを作成する
     */
    private void createCoupon() {
        //キーボードを非表示にする
        Utils.hideKeyboard(this);

        //クーポン情報をセットする
        if (putCouponInfo()) {
            //正常にセットできた場合、クーポン作成を終了する
            completeCreate();
        }
    }

}
