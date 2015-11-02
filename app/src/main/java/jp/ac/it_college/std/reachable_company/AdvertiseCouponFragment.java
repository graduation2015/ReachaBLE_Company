package jp.ac.it_college.std.reachable_company;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class AdvertiseCouponFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    private ToggleButton mToggleAdvertise;
    private ImageView mCouponPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_advertise_coupon, container, false);

        findViews(contentView);

        setCouponPreview();

        return contentView;
    }

    private void findViews(View contentView) {
        mToggleAdvertise = (ToggleButton) contentView.findViewById(R.id.toggle_advertise);
        mToggleAdvertise.setOnCheckedChangeListener(this);

        mCouponPreview = (ImageView) contentView.findViewById(R.id.img_coupon_preview);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.toggle_advertise:
                switchAdvertise(isChecked);
                break;
        }
    }

    /**
     * 宣伝のON/OFFが切り替わった際に処理を分岐させる
     * @param isAdvertise
     */
    private void switchAdvertise(boolean isAdvertise) {
        if (isAdvertise) {
            startAdvertise();
        } else {
            stopAdvertise();
        }
    }

    private void startAdvertise() {
        Toast.makeText(getActivity(), "ON", Toast.LENGTH_SHORT).show();
    }

    private void stopAdvertise() {
        Toast.makeText(getActivity(), "OFF", Toast.LENGTH_SHORT).show();
    }

    private void setCouponPreview() {
        mCouponPreview.setImageBitmap(BitmapFactory.decodeFile(getCouponPath()));
    }

    private String getCouponPath() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(Constants.COUPON_FILE_PATH, Context.MODE_PRIVATE);
        return prefs.getString(Constants.COUPON_FILE_PATH, null);
    }
}
