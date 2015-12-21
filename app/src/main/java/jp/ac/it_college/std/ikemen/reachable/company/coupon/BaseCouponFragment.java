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
    public static final String PREF_COUPON_INFO_LIST = "coupon_info_list";

    /* Coupon */
    private List<CouponInfo> mCouponInfoList;

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
     * SharedPreferencesに保存されているクーポンリストを取得
     * @return 保存されているクーポンリスト
     */
    protected Set<String> getPrefCouponInfoSet() {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                CouponInfo.PREF_INFO, Context.MODE_PRIVATE);
        return prefs.getStringSet(PREF_COUPON_INFO_LIST, new HashSet<String>());
    }

    protected List<CouponInfo> getCouponInfoList() {
        if (mCouponInfoList == null) {
            mCouponInfoList = new ArrayList<>();

            List<String> list = new ArrayList<>(getPrefCouponInfoSet());
            Gson gson = new Gson();

            for (String info : list) {
                mCouponInfoList.add(gson.fromJson(info, CouponInfo.class));
            }
        }

        return mCouponInfoList;
    }

    protected CouponListAdapter getCouponListAdapter() {
        if (mCouponListAdapter == null) {
            mCouponListAdapter = new CouponListAdapter(getCouponInfoList());
        }
        return mCouponListAdapter;
    }

    protected void setCouponListAdapter(CouponListAdapter adapter) {
        if (adapter != null) {
            this.mCouponListAdapter = adapter;
        }
    }
}
