package jp.ac.it_college.std.ikemen.reachable.company.aws;


import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.Constants;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

/**
 * S3バケットへのアップロード周りの処理をまとめたクラス
 */
public class S3UploadManager implements TransferListener {
    private final TransferUtility mTransferUtility;
    private final OnUploadListener mUploadListener;
    private List<TransferObserver> mObserverList;

    public S3UploadManager(TransferUtility transferUtility, OnUploadListener uploadListener) {
        this.mTransferUtility = transferUtility;
        this.mUploadListener = uploadListener;
        this.mObserverList = new ArrayList<>();
    }

    /**
     * 渡されたファイルをS3バケットにアップロードする
     * @param file アップロードするファイル
     * @return アップロード中のイベントを返す
     */
    private TransferObserver upload(File file) {
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
        for (File file : files) {
            TransferObserver observer = upload(file);
            observer.setTransferListener(this);

            getObserverList().add(observer);
        }

        return mObserverList;
    }

    public TransferUtility getTransferUtility() {
        return mTransferUtility;
    }

    public List<TransferObserver> getObserverList() {
        return mObserverList;
    }

    private boolean checkObserversState(TransferState state) {
        for (TransferObserver observer : getObserverList()) {
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
                    if (checkObserversState(TransferState.FAILED)) {
                        mUploadListener.onUploadFailed(i, transferState);
                    }
                    break;
                case CANCELED:
                    if (checkObserversState(TransferState.CANCELED)) {
                        mUploadListener.onUploadCanceled(i, transferState);
                    }
                    break;
                case COMPLETED:
                    if (checkObserversState(TransferState.COMPLETED)) {
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
