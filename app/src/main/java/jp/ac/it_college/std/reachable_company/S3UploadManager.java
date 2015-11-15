package jp.ac.it_college.std.reachable_company;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

public class S3UploadManager {
    public static final String FOLDER_SUFFIX = "/";
    private final TransferUtility mTransferUtility;

    public S3UploadManager(TransferUtility transferUtility) {
        this.mTransferUtility = transferUtility;
    }

    public TransferObserver upload(File file) {
        TransferObserver observer = mTransferUtility.upload(
                Constants.BUCKET_NAME,
                CompanyInfo.COMPANY_ID + FOLDER_SUFFIX + file.getName(),
                file);

        return observer;
    }

}
