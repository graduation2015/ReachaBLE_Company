package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import jp.ac.it_college.std.ikemen.reachable.company.CreateCouponActivity;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;


public class CouponPreviewFragment extends Fragment implements View.OnClickListener {

    /* Constants */
    private static final int REQUEST_GALLERY = 0;
    public static final int CREATE_COUPON = 0x002;

    /* Views */
    private View mContentView;
    private FloatingActionButton mFab;
    private RecyclerView mCouponList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_coupon_preview, container, false);

        //初期設定
        initSettings();
        return mContentView;
    }

    /**
     * 各種初期設定
     */
    private void initSettings() {
        //SharedPreferencesにクーポンの情報がある場合プレビューにセット
        setCoupon();

        //FABのOnClickListenerをセット
        getFab().setOnClickListener(this);
    }

    public View getContentView() {
        return mContentView;
    }

    public FloatingActionButton getFab() {
        if (mFab == null) {
            mFab = (FloatingActionButton) getContentView().findViewById(R.id.fab);
        }
        return mFab;
    }

    public RecyclerView getCouponList() {
        if (mCouponList == null) {
            mCouponList = (RecyclerView) getContentView().findViewById(R.id.coupon_list);
        }
        return mCouponList;
    }

    /**
     * クーポンを選択する
     */
    private void couponSelect() {
        Intent intent = new Intent();

        //APILevelが19(version 4.4)以上の場合
        if (Build.VERSION.SDK_INT >= 19) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("*/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        } else {
            //APILevelが18(version 4.3)以下の場合
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("image/*");
        }

        startActivityForResult(intent, REQUEST_GALLERY);
    }


    /**
     * SharedPreferencesに保存されているクーポンの情報をセット
     */
    private void setCoupon() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(CouponInfo.PREF_INFO, Context.MODE_PRIVATE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                Intent intent = new Intent(getActivity(), CreateCouponActivity.class);
                intent.setData(data.getData());
                startActivityForResult(intent, CREATE_COUPON);
            }

            if (requestCode == CREATE_COUPON) {
                Toast.makeText(getActivity(), "created", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                couponSelect();
                break;
        }
    }
}
