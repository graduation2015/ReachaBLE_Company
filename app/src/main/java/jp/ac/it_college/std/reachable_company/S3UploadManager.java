package jp.ac.it_college.std.reachable_company;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

public class S3UploadManager {
    public static final String FOLDER_SUFFIX = "/";
    public static final String FILE_DELIMITER = ".";
    private final TransferUtility mTransferUtility;

    public S3UploadManager(TransferUtility transferUtility) {
        this.mTransferUtility = transferUtility;
    }

    /**
     * 渡されたファイルをS3バケットにアップロードする
     * @param file
     * @return
     */
    public TransferObserver upload(File file) {
        // "企業ID/企業ID.拡張子" の形式でファイルをアップロードする

        TransferObserver observer = mTransferUtility.upload(
                Constants.BUCKET_NAME,
                CompanyInfo.COMPANY_ID + FOLDER_SUFFIX +
                        CompanyInfo.COMPANY_ID + getExtension(file),
                file);

        return observer;
    }

    /**
     * 渡されたファイルの拡張子を文字列で返す
     * @param file
     * @return
     */
    private String getExtension(File file) {
        int index = file.getName().lastIndexOf(FILE_DELIMITER);

        return file.getName().substring(index);
    }
}
