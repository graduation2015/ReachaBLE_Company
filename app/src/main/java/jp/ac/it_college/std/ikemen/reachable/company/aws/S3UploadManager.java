package jp.ac.it_college.std.ikemen.reachable.company.aws;


import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.Constants;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

public class S3UploadManager implements TransferListener {
    private final TransferUtility mTransferUtility;
    private final OnUploadListener mUploadListener;

    public S3UploadManager(TransferUtility transferUtility, OnUploadListener uploadListener) {
        this.mTransferUtility = transferUtility;
        this.mUploadListener = uploadListener;
    }

    /**
     * 渡されたファイルをS3バケットにアップロードする
     * @param file アップロードするファイル
     * @return アップロード中のイベントを返す
     */
    public TransferObserver upload(File file) {
        // "企業ID/企業ID.拡張子" の形式でファイルをアップロードする
        return getTransferUtility().upload(
                Constants.BUCKET_NAME,
                FileUtil.getKey(file),
                file);
    }

    /**
     * 渡されたファイルリストをS3バケットにアップロードする
     * @param files アップロードするファイルのリスト
     * @return アップロード中のイベントを返す
     */
    public List<TransferObserver> upload(List<File> files) {
        List<TransferObserver> observerList = new ArrayList<>();

        for (File file : files) {
            TransferObserver observer = upload(file);
            observer.setTransferListener(this);

            observerList.add(observer);
        }

        return observerList;
    }

    public TransferUtility getTransferUtility() {
        return mTransferUtility;
    }

    private boolean checkObserversState(TransferType type, TransferState state) {
        List<TransferObserver> observerList = getTransferUtility().getTransfersWithType(type);
        for (TransferObserver observer : observerList) {
            if (observer.getState() != state) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onStateChanged(int i, TransferState transferState) {
        if (mUploadListener != null) {
            switch (transferState) {
                case FAILED:
                    mUploadListener.onUploadFailed(i, transferState);
                    break;
                case CANCELED:
                    mUploadListener.onUploadCanceled(i, transferState);
                    break;
                case COMPLETED:
                    if (checkObserversState(TransferType.UPLOAD, TransferState.COMPLETED)) {
                        mUploadListener.onUploadCompleted();
                    }
                    break;
            }
        }
    }

    @Override
    public void onProgressChanged(int i, long l, long l1) {
        //Do nothing
    }

    @Override
    public void onError(int i, Exception e) {
        Log.e(S3UploadManager.class.getSimpleName(), e.toString(), e);
        e.printStackTrace();
    }

    /**
     * アップロード中のイベント用のリスナー
     */
    public interface OnUploadListener {
        void onUploadCompleted();
        void onUploadCanceled(int id, TransferState transferState);
        void onUploadFailed(int id, TransferState transferState);
    }
}
