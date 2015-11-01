package jp.ac.it_college.std.reachable_company;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

public class CouponUploadListener implements TransferListener {

    private ProgressDialogFragment mDialogFragment;
    private Activity mActivity;
    private static final String TAG = "S3UploadListener";

    /**
     * コンストラクタ
     * @param activity
     */
    public CouponUploadListener(Activity activity) {
        this.mActivity = activity;
        setUpDialogFragment();
    }

    /**
     * ProgressDialogFragmentを設定
     */
    private void setUpDialogFragment() {
        mDialogFragment = ProgressDialogFragment.newInstance(
                mActivity.getString(R.string.dialog_title_coupon_upload),
                mActivity.getString(R.string.dialog_message_coupon_upload));
    }

    public void onStateChanged(int i, TransferState transferState) {
        Log.d(TAG, "onStateChanged: " + String.valueOf(i));

        switch (transferState) {
            case IN_PROGRESS:
                // アップロード開始時にダイアログ表示
                mDialogFragment.show(mActivity.getFragmentManager(), TAG);
                break;
            case COMPLETED:
                // アップロード終了時にダイアログ非表示
                mDialogFragment.dismiss();

                //Toast表示
                Toast.makeText(mActivity, "Upload completed.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mActivity, "Upload failed.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onProgressChanged(int i, long l, long l1) {
        Log.d(TAG, "onProgressChanged:");
    }

    @Override
    public void onError(int i, Exception e) {
        Log.d(TAG, "Error during upload: " + i, e);
    }
}
