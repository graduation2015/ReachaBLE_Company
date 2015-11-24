package jp.ac.it_college.std.reachable_company.aws;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import jp.ac.it_college.std.reachable_company.Constants;
import jp.ac.it_college.std.reachable_company.R;

/**
 * Cognito経由でCredentialsを取得するAsyncTaskクラス
 */
public class CognitoAsyncTask extends AsyncTask<Void, Void, AWSCredentials> {
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public CognitoAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        getProgressDialog().show();
    }

    @Override
    protected AWSCredentials doInBackground(Void... voids) {
        CognitoCachingCredentialsProvider provider = new CognitoCachingCredentialsProvider(
                getContext(),
                Constants.IDENTITY_POOL_ID,
                Regions.AP_NORTHEAST_1
        );

        return provider.getCredentials();
    }

    @Override
    protected void onPostExecute(AWSCredentials awsCredentials) {
        getProgressDialog().dismiss();
    }

    public Context getContext() {
        return mContext;
    }

    public ProgressDialog getProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = createProgressDialog();
        }
        return mProgressDialog;
    }

    /**
     * ProgressDialogを生成して返す
     * @return
     */
    private ProgressDialog createProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.dialog_title_credentials);
        progressDialog.setMessage(getContext().getString(R.string.dialog_message_credentials));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        return progressDialog;
    }
}
