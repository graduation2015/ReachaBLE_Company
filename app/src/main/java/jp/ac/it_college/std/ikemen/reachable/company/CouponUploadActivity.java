package jp.ac.it_college.std.ikemen.reachable.company;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mTagsView;

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
                putInfo();
                break;
        }
    }

    public TextView getTitleView() {
        if (mTitleView == null) {
            mTitleView = (TextView) findViewById(R.id.editText_coupon_title);
        }
        return mTitleView;
    }

    public TextView getDescriptionView() {
        if (mDescriptionView == null) {
            mDescriptionView = (TextView) findViewById(R.id.editText_coupon_description);
        }
        return mDescriptionView;
    }

    public TextView getTagsView() {
        if (mTagsView == null) {
            mTagsView = (TextView) findViewById(R.id.editText_coupon_tag);
        }
        return mTagsView;
    }

    private void putInfo() {
        String title = getTitleView().getText().toString();
        String description = getDescriptionView().getText().toString();
        String tags = getTagsView().getText().toString();
        List<String> tagsList = Arrays.asList(tags.replaceAll("　", " ").split(" "));

        CouponInfo couponInfo = new CouponInfo(title, description, tagsList);
        JsonManager manager = new JsonManager(this);
        manager.putJsonObj(couponInfo);
    }

    private boolean validateTitle(String title) {
        return title.length() > 0;
    }
}
