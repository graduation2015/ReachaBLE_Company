package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.it_college.std.ikemen.reachable.company.CouponListAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.CreateCouponActivity;
import jp.ac.it_college.std.ikemen.reachable.company.EmptySupportRecyclerView;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.RecyclerItemClickListener;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;


public class CouponPreviewFragment extends Fragment
        implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {

    /* Constants */
    private static final int REQUEST_GALLERY = 0;
    public static final int CREATE_COUPON = 0x002;
    public static final String COUPON_INFO_LIST = "coupon_info_list";
    public static final long ANIM_DURATION = 800L;

    /* Views */
    private View mContentView;
    private FloatingActionButton mFab;
    private EmptySupportRecyclerView mCouponListView;
    private TextView mEmptyView;

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
        //LayoutManager設定
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getCouponListView().setLayoutManager(layoutManager);

        //表示するアイテムのサイズを固定化
        getCouponListView().setHasFixedSize(true);

        //アダプターをセット
        getCouponListView().setAdapter(getCouponListAdapter());

        //クーポン追加/更新時のアニメーションをセット
        getCouponListView().setItemAnimator(new SlideInDownAnimator());
        getCouponListView().getItemAnimator().setAddDuration(ANIM_DURATION);
        getCouponListView().getItemAnimator().setChangeDuration(ANIM_DURATION);
        getCouponListView().getItemAnimator().setMoveDuration(ANIM_DURATION);
        getCouponListView().getItemAnimator().setRemoveDuration(ANIM_DURATION);

        //クーポンアイテムクリック時のリスナーをセット
        getCouponListView().addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), this));
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

            List<String> list = new ArrayList<>(getPrefInfoSet());
            Gson gson = new Gson();

            for (String info : list) {
                mCouponInfoList.add(gson.fromJson(info, CouponInfo.class));
            }
        }

        return mCouponInfoList;
    }

    /**
     * SharedPreferencesに保存されているクーポンリストを取得
     * @return 保存されているクーポンリスト
     */
    private Set<String> getPrefInfoSet() {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                CouponInfo.PREF_INFO, Context.MODE_PRIVATE);
        return prefs.getStringSet(COUPON_INFO_LIST, new HashSet<String>());
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

    public TextView getEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = (TextView) getContentView().findViewById(R.id.txt_empty_view);
        }
        return mEmptyView;
    }

    public EmptySupportRecyclerView getCouponListView() {
        if (mCouponListView == null) {
            mCouponListView = (EmptySupportRecyclerView) getContentView().findViewById(R.id.coupon_list);
            //リストが空の際に表示するViewをセット
            mCouponListView.setEmptyView(getEmptyView());
        }
        return mCouponListView;
    }

    /**
     * クーポンをギャラリーから選択する
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
        getCouponInfoList().clear();
        getCouponListAdapter().notifyItemRemoved(0);

        //クーポンリストに追加
        getCouponInfoList().add(info);
        //変更を通知
        getCouponListAdapter().notifyItemInserted(0);
        saveCouponInstance(getCouponInfoList());
    }

    /**
     * SharedPreferencesにクーポンのインスタンスを保存
     * @param infoList 保存するクーポン情報リスト
     */
    private void saveCouponInstance(List<CouponInfo> infoList) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                CouponInfo.PREF_INFO, Context.MODE_PRIVATE).edit();
        Set<String> instances = new HashSet<>();

        for (CouponInfo info : infoList) {
            instances.add(gson.toJson(info));
        }

        editor.putStringSet(COUPON_INFO_LIST, instances);
        editor.apply();
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
                //FABボタン押下時の処理
                couponSelect();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        //クーポンリストのアイテムクリック時の処理
        Toast.makeText(getActivity(), "position = " + position, Toast.LENGTH_SHORT).show();
        getCouponInfoList().remove(position);

        getCouponListAdapter().notifyItemRemoved(position);
        saveCouponInstance(getCouponInfoList());
    }
}
