package jp.ac.it_college.std.ikemen.reachable.company.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 共通の処理をまとめたユーティリティクラス
 */
public class Utils {

    /**
     * ソフトウェアキーボードを非表示にする
     * @param activity ソフトウェアキーボードが表示されているActivity
     */
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
