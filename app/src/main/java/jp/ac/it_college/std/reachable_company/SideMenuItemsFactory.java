package jp.ac.it_college.std.reachable_company;

import android.app.Fragment;

/**
 * SideMenuItemsのファクトリークラス
 */
public class SideMenuItemsFactory {

    /**
     * SideMenuItemsの列挙子に応じたフラグメントを返す
     * @param items
     * @return
     */
    public static Fragment getPageItem(SideMenuItems items) {
        switch (items) {
            case ADVERTISE_COUPON:
                return new AdvertiseCouponFragment();
            case COUPON_UPLOAD:
            default:
                return new CouponUploadFragment();
        }
    }
}
