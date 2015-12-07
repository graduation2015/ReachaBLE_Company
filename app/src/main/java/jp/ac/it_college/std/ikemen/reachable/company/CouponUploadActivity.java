package jp.ac.it_college.std.ikemen.reachable.company;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;

import java.net.URISyntaxException;

import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

public class CouponUploadActivity extends AppCompatActivity {

    /* Views */
    private Toolbar mToolbar;
    private ImageView mImageView;

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
}
