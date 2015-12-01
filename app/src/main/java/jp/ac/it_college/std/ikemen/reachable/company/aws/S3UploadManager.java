package jp.ac.it_college.std.ikemen.reachable.company.aws;

import android.app.ProgressDialog;
import android.content.Context;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.Constants;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.CouponUploadListener;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;

public class S3UploadManager {
    private final TransferUtility mTransferUtility;

    public S3UploadManager(TransferUtility transferUtility) {
        this.mTransferUtility = transferUtility;
    }

    /**
     * 渡されたファイルをS3バケットにアップロードする
     * @param file
     * @return
     */
    private TransferObserver upload(File file) {
        // "企業ID/企業ID.拡張子" の形式でファイルをアップロードする

        TransferObserver observer = getTransferUtility().upload(
                Constants.BUCKET_NAME,
                FileUtil.getKey(file),
                file);

        return observer;
    }

    /**
     * 複数のファイルをS3バケットにアップロードする
     * @param files
     * @return
     */
    public List<TransferObserver> uploadList(
            Context context, ProgressDialog progressDialog, List<File> files) {
        List<TransferObserver> observers = new ArrayList<>();

        for (File file : files) {
            TransferObserver observer = upload(file);
            observer.setTransferListener(
                    new CouponUploadListener(context, file.getName(), progressDialog));

            observers.add(observer);
        }

        return observers;
    }

    public TransferUtility getTransferUtility() {
        return mTransferUtility;
    }
}
