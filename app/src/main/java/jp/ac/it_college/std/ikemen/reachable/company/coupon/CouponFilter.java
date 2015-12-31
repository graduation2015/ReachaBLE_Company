package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.CouponListAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

/**
 * クーポンリストをフィルタリングするFilterクラス
 */
public class CouponFilter extends Filter {

    private final CouponListAdapter mAdapter;
    private final List<CouponInfo> mOriginalList;
    private final List<CouponInfo> mFilteredList;

    public CouponFilter(CouponListAdapter adapter, List<CouponInfo> originalList) {
        this.mAdapter = adapter;
        this.mOriginalList = new LinkedList<>(originalList);
        this.mFilteredList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        mFilteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            mFilteredList.addAll(mOriginalList);
        } else {
            filteringCoupon(mOriginalList, mFilteredList, constraint);
        }

        results.values = mFilteredList;
        results.count = mFilteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapter.getCouponInfoList().clear();
        mAdapter.getCouponInfoList().addAll((ArrayList<CouponInfo>) results.values);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * クーポンに入力されたキーワードが含まれるかチェック
     * @param info クーポンリストに含まれるCouponInfoクラス
     * @param keyword 検索するキーワード
     * @return クーポンにキーワードが含まれる場合はtrueを返す
     */
    private boolean isKeywordExists(CouponInfo info, CharSequence keyword) {
        final String filterPattern = keyword.toString().toLowerCase().trim();

        return info.toString().toLowerCase().trim().contains(filterPattern);
    }

    /**
     * クーポンのフィルタリングを実行し、フィルターリストに該当するクーポンを追加する
     * @param originalList フィルタリングするクーポンリスト
     * @param filteredList フィルタリングされたクーポンを追加するリスト
     * @param keyword 検索するキーワード
     */
    private void filteringCoupon(List<CouponInfo> originalList, List<CouponInfo> filteredList, CharSequence keyword) {
        for (final CouponInfo info : originalList) {
            if (isKeywordExists(info, keyword)) {
                filteredList.add(info);
            }
        }
    }
}
