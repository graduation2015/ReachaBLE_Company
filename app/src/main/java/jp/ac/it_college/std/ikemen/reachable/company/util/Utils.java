package jp.ac.it_college.std.ikemen.reachable.company.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.amazonaws.com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

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

    /**
     * SharedPreferencesにクーポンのインスタンスを保存
     * @param infoList 保存するクーポン情報リスト
     * @param key      SharedPreferencesに保存する際のキー名
     */
    public static void saveCouponInstance(Context context, List<CouponInfo> infoList, String key) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = context.getSharedPreferences(
                CouponInfo.PREF_INFO, Context.MODE_PRIVATE).edit();
        Set<String> instances = new HashSet<>();

        for (CouponInfo info : infoList) {
            instances.add(gson.toJson(info));
        }

        editor.putStringSet(key, instances);
        editor.apply();
    }

}
