package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;

import com.amazonaws.com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.it_college.std.ikemen.reachable.company.CouponListAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.EmptySupportRecyclerView;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;

/**
 * クーポンリストを表示するFragmentのベースクラス
 */
public class BaseCouponFragment extends Fragment {

    /* Constants */
    public static final long COUPON_ANIM_DURATION = 800L;
    public static final String PREF_SAVED_COUPON_INFO_LIST = "pref_saved_coupon_info_list";
    public static final String PREF_ADVERTISE_COUPON_LIST = "pref_advertise_coupon_list";


    /* Coupon */
    private List<CouponInfo> mCouponInfoList;
    private List<CouponInfo> mSelectedCouponList;

    /* Adapter */
    private CouponListAdapter mCouponListAdapter;


    /**
     * クーポンリストをセットアップする
     */
    protected void setUpCouponListView(EmptySupportRecyclerView couponListView) {
        //LayoutManager設定
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        couponListView.setLayoutManager(layoutManager);

        //表示するアイテムのサイズを固定化
        couponListView.setHasFixedSize(true);

        //アダプターをセット
        couponListView.setAdapter(getCouponListAdapter());

        //クーポン追加/更新時のアニメーションをセット
        couponListView.setItemAnimator(new OvershootInRightAnimator());
        couponListView.getItemAnimator().setAddDuration(COUPON_ANIM_DURATION);
        couponListView.getItemAnimator().setChangeDuration(COUPON_ANIM_DURATION);
        couponListView.getItemAnimator().setMoveDuration(COUPON_ANIM_DURATION);
        couponListView.getItemAnimator().setRemoveDuration(COUPON_ANIM_DURATION);
    }

    /**
     * SharedPreferencesに保存されているクーポン情報を取得
     * @param key SharedPreferencesから取得する値のキー名
     * @return 保存されているクーポン情報のStringSet
     */
    private Set<String> getPrefCouponInfoSet(String key) {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                CouponInfo.PREF_INFO, Context.MODE_PRIVATE);
        return prefs.getStringSet(key, new HashSet<String>());
    }

    /**
     * SharedPreferencesに保存されているクーポンリストを返す
     * @return 指定されたキー名のクーポンリスト
     */
    protected List<CouponInfo> getCouponInfoList() {
        if (mCouponInfoList == null) {
            mCouponInfoList = getPrefInfoList(PREF_SAVED_COUPON_INFO_LIST);
        }

        return mCouponInfoList;
    }

    /**
     * 宣伝対象のクーポンリストを取得
     * @return SharedPreferencesに保存されている宣伝対象のクーポンリストを返す
     */
    protected List<CouponInfo> getAdvertiseCouponList() {
        if (mSelectedCouponList == null) {
            mSelectedCouponList = getPrefInfoList(PREF_ADVERTISE_COUPON_LIST);
        }

        return mSelectedCouponList;
    }

    /**
     * SharedPreferencesに保存されているクーポン情報からリストを生成して返す
     * @param key 取得するクーポンリストのキー名
     * @return keyで指定されたクーポンリストを返す
     */
    private List<CouponInfo> getPrefInfoList(String key) {
        List<String> prefList = new ArrayList<>(getPrefCouponInfoSet(key));
        List<CouponInfo> result = new ArrayList<>();

        Gson gson = new Gson();
        for (String info : prefList) {
            result.add(gson.fromJson(info, CouponInfo.class));
        }

        return result;
    }

    protected CouponListAdapter getCouponListAdapter() {
        return mCouponListAdapter;
    }

    protected void setCouponListAdapter(CouponListAdapter adapter) {
        this.mCouponListAdapter = adapter;
    }
}
