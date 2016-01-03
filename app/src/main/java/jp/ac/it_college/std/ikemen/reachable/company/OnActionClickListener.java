package jp.ac.it_college.std.ikemen.reachable.company;

import android.view.View;

/**
 * クーポンの各アクションボタン押下時のイベントを通知するリスナークラス
 */
public interface OnActionClickListener {
    void onAdvertiseClick(View view, int position);
    void onDeleteClick(View view, int position);
}
