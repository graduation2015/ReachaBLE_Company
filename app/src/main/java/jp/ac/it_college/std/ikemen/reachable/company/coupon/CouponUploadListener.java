package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

public class CouponUploadListener implements TransferListener {

    private ProgressDialog mProgressDialog;
    private Context mContext;
    private String mFileName;
    private long progress;
    private static final String TAG = "S3UploadListener";

    public CouponUploadListener(Context context, String fileName,
                                ProgressDialog progressDialog) {
        this.mContext = context;
        this.mFileName = fileName;
        this.mProgressDialog = progressDialog;
    }

    @Override
    public void onStateChanged(int i, TransferState transferState) {
        Log.d(TAG, "onStateChanged: " + transferState.name());

        switch (transferState) {
            case IN_PROGRESS:
                Log.d(TAG, "Upload in progress : " + getFileName());
                break;
            case COMPLETED:
                Toast.makeText(
                        getContext(), "Upload completed : " + getFileName(), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(
                        getContext(), "Upload failed : " + getFileName(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onProgressChanged(int i, long bytesCurrent, long bytesTotal) {
        Log.d(TAG, "onProgressChanged: " + getFileName() + " " + bytesCurrent + "/" + bytesTotal);
        progress = bytesCurrent + getProgressDialog().getProgress() - progress;
        getProgressDialog().setProgress((int) progress);
    }

    @Override
    public void onError(int i, Exception e) {
        Log.d(TAG, "Error during upload: " + i, e);
    }

    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    public Context getContext() {
        return mContext;
    }

    public String getFileName() {
        return mFileName;
    }

}
