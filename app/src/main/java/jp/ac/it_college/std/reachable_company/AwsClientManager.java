package jp.ac.it_college.std.reachable_company;

import android.content.Context;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

public class AwsClientManager {

    private AWSCredentials mCredentials;
    private AmazonS3Client mS3Client;
    private TransferUtility mTransferUtility;

    public AwsClientManager(AWSCredentials credentials) {
        this.mCredentials = credentials;
    }

    private AmazonS3Client makeS3Client() {
        mS3Client = new AmazonS3Client(mCredentials);
        mS3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
        return mS3Client;
    }

    public AmazonS3Client getS3Client() {
        if (mS3Client == null) {
            mS3Client = makeS3Client();
        }

        return mS3Client;
    }

    private TransferUtility makeTransferUtility(Context context) {
        //Download and Upload用のUtilityセットアップ
        mTransferUtility = new TransferUtility(getS3Client(), context);
        return mTransferUtility;
    }

    public TransferUtility getTransferUtility(Context context) {
        if (mTransferUtility == null) {
            mTransferUtility = makeTransferUtility(context);
        }

        return mTransferUtility;
    }

    public AWSCredentials getCredentials() {
        return mCredentials;
    }
}
