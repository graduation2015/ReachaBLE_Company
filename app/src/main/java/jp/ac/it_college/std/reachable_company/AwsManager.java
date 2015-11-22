package jp.ac.it_college.std.reachable_company;

import android.content.Context;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.util.concurrent.ExecutionException;

public class AwsManager {
    private static AwsManager sInstance;
    private AWSCredentials mCredentials;
    private AmazonS3Client mS3Client;
    private TransferUtility mTransferUtility;
    private Context mContext;

    public static AwsManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AwsManager(context);
        }
        return sInstance;
    }

    private AwsManager(Context context) {
        this.mContext = context;
    }

    public AWSCredentials getCredentials() {
        if (mCredentials == null) {
            try {
                //Cognitoを通してCredentialsを取得
                mCredentials = new CognitoAsyncTask(getContext()).execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return mCredentials;
    }

    public AmazonS3Client getS3Client() {
        if (mS3Client == null) {
            mS3Client = new AmazonS3Client(getCredentials());
            mS3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
        }
        return mS3Client;
    }

    public TransferUtility getTransferUtility() {
        if (mTransferUtility == null) {
            mTransferUtility = new TransferUtility(getS3Client(), getContext());
        }
        return mTransferUtility;
    }

    public Context getContext() {
        return mContext;
    }
}
