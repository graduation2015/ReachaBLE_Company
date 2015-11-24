package jp.ac.it_college.std.reachable_company.drawer;

import android.app.Fragment;

import jp.ac.it_college.std.reachable_company.coupon.AdvertiseCouponFragment;
import jp.ac.it_college.std.reachable_company.coupon.CouponUploadFragment;

/**
 * SideMenuItemsのファクトリークラス
 */
public class SideMenuItemsFactory {

    /**
     * SideMenuItemsの列挙子に応じたフラグメントを返す
     * @param items
     * @return
     */
    public static Fragment getSideMenuItem(SideMenuItems items) {
        switch (items) {
            case ADVERTISE_COUPON:
                return new AdvertiseCouponFragment();
            case COUPON_UPLOAD:
            default:
                return new CouponUploadFragment();
        }
    }
}
