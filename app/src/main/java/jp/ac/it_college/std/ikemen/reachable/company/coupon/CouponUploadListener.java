package jp.ac.it_college.std.ikemen.reachable.company.coupon;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

public class CouponUploadListener implements TransferListener {

    private ProgressDialog mProgressDialog;
    private String mFileName;
    private static final String TAG = "S3UploadListener";
    private static final long DISMISS_DELAY = 1000L;
    private long beforeProgress = 0;

    public CouponUploadListener(String fileName, ProgressDialog progressDialog) {
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
                Log.d(TAG, "Upload completed : " + getFileName());
                break;
            default:
                Log.d(TAG, "Upload failed : " + getFileName());
                //TODO:アップロードが中断された場合の処理を実装する
                getProgressDialog().cancel();
                break;
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        Log.d(TAG, "onProgressChanged: id = "
                + id + " " + getFileName() + " " + bytesCurrent + "/" + bytesTotal);

        Log.d(TAG, "onProgressChanged: id = " + id + " beforeProgress = " + beforeProgress);

        //ProgressDialogにアップロードの進捗をセット
        long progress = getProgressDialog().getProgress() + bytesCurrent - beforeProgress;
        beforeProgress = bytesCurrent;
        getProgressDialog().setProgress((int) progress);

        Log.d(TAG, "onProgressChanged: id = " + id + " progress = " + progress);

        //進捗が100%になったらProgressDialogを非表示にする
        if (getProgressDialog().getProgress() >= getProgressDialog().getMax()) {
            delayDismiss(getProgressDialog(), DISMISS_DELAY);
        }
    }

    @Override
    public void onError(int i, Exception e) {
        Log.d(TAG, "Error during upload: " + i, e);
    }

    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    public String getFileName() {
        return mFileName;
    }

    /**
     * ディレイをかけた後にProgressDialogを非表示にする
     * @param progressDialog
     */
    private void delayDismiss(final ProgressDialog progressDialog, long delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, delayMillis);
    }

}
