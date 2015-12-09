package jp.ac.it_college.std.ikemen.reachable.company;

import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.json.JsonManager;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

public class CouponUploadActivity extends AppCompatActivity implements View.OnClickListener {

    /* Views */
    private Toolbar mToolbar;
    private ImageView mImageView;
    private TextInputLayout mTitleWrapper;
    private TextInputLayout mDescriptionWrapper;
    private TextInputLayout mTagsWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_upload);

        initSettings();
    }

    private void initSettings() {
        setUpToolbar();
        setCouponPreview();

        findViewById(R.id.btn_test_put_json).setOnClickListener(this);
    }

    /**
     * 選択されたクーポン画像をプレビューにセット
     */
    private void setCouponPreview() {
        String path = null;
        try {
            path = FileUtil.getPath(this, getIntent().getData());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        getImageView().setImageBitmap(BitmapFactory.decodeFile(path));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //SENDメニューを表示
        getMenuInflater().inflate(R.menu.menu_coupon_upload, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_put_json:
                putCouponInfo();
                break;
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
     */
    private void putCouponInfo() {
        //タイトルを取得
        String title = trim(getTitleWrapper().getEditText().getText().toString());
        //タイトルのバリデートチェック
        if (!validateTitle(title)) {
            getTitleWrapper().setError(getString(R.string.validate_title_error));
            return;
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
        }
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
}
