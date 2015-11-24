package jp.ac.it_college.std.ikemen.aws;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

import jp.ac.it_college.std.ikemen.info.CompanyInfo;
import jp.ac.it_college.std.ikemen.Constants;

public class S3UploadManager {
    public static final String FOLDER_SUFFIX = "/";
    public static final String FILE_DELIMITER = ".";
    private final TransferUtility mTransferUtility;
    private static final String OBJECT_KEY =
            CompanyInfo.COMPANY_ID + FOLDER_SUFFIX + CompanyInfo.COMPANY_ID;
    private static final String JSON_FILE_EXTENSION = ".json";

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
                getKey(file),
                file);

        return observer;
    }

    /**
     * 渡されたファイルの拡張子を文字列で返す
     * @param file
     * @return
     */
    private String getFileExtension(File file) {
        int index = file.getName().lastIndexOf(FILE_DELIMITER);

        return file.getName().substring(index);
    }

    /**
     * ファイルの拡張子に応じたキー名を返す
     * @param file
     * @return
     */
    private String getKey(File file) {
        String fileExtension = getFileExtension(file);
        if (fileExtension.equals(JSON_FILE_EXTENSION)) {
            return OBJECT_KEY + JSON_FILE_EXTENSION;
        }

        return OBJECT_KEY;
    }
}
