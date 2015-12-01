package jp.ac.it_college.std.ikemen.reachable.company.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * ProgressDialogを表示するDialogFragmentクラス
 */
public class ProgressDialogFragment extends DialogFragment {

    private ProgressDialog progressDialog;
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    public static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (progressDialog != null) {
            return progressDialog;
        }

        String title = getArguments().getString(TITLE);
        String message = getArguments().getString(MESSAGE);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        setCancelable(false);
        return progressDialog;
    }

}
