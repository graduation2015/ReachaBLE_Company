package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.CouponListAdapter;
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
    private RecyclerView mCouponListView;

    /* Adapter */
    private CouponListAdapter mCouponListAdapter;

    /* Coupon */
    private List<CouponInfo> mCouponInfoList;

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
        //クーポンリストをセットアップ
        setUpCouponListView();

        //FABのOnClickListenerをセット
        getFab().setOnClickListener(this);
    }

    /**
     * クーポンリストをセットアップする
     */
    private void setUpCouponListView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        getCouponListView().setHasFixedSize(true);
        getCouponListView().setLayoutManager(layoutManager);

        getCouponListView().setAdapter(getCouponListAdapter());
    }

    public CouponListAdapter getCouponListAdapter() {
        if (mCouponListAdapter == null) {
            mCouponListAdapter = new CouponListAdapter(getCouponInfoList());
        }
        return mCouponListAdapter;
    }

    private List<CouponInfo> getCouponInfoList() {
        if (mCouponInfoList == null) {
            mCouponInfoList = new ArrayList<>();
        }

        return mCouponInfoList;
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

    public RecyclerView getCouponListView() {
        if (mCouponListView == null) {
            mCouponListView = (RecyclerView) getContentView().findViewById(R.id.coupon_list);
        }
        return mCouponListView;
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
     * クーポンリストにクーポンを追加する
     * @param info 作成されたクーポン
     */
    private void addCoupon(CouponInfo info) {
        //クーポンリストに追加
        getCouponInfoList().add(info);
        //変更を通知
        getCouponListAdapter().notifyDataSetChanged();
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
                //作成されたクーポンの情報を取得
                CouponInfo couponInfo = (CouponInfo) data.getSerializableExtra(
                        CreateCouponActivity.CREATED_COUPON_DATA);
                //クーポンをクーポンリストに追加
                addCoupon(couponInfo);
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
