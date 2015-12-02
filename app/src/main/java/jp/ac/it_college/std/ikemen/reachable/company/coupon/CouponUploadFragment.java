package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.Constants;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.UploadObservers;
import jp.ac.it_college.std.ikemen.reachable.company.aws.AwsUtil;
import jp.ac.it_college.std.ikemen.reachable.company.aws.S3UploadManager;
import jp.ac.it_college.std.ikemen.reachable.company.dialog.ChoiceDialog;
import jp.ac.it_college.std.ikemen.reachable.company.dialog.MultipleCategoryChoiceDialog;
import jp.ac.it_college.std.ikemen.reachable.company.dialog.ProgressDialogFragment;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.json.JsonManager;
import jp.ac.it_college.std.ikemen.reachable.company.util.FileUtil;


public class CouponUploadFragment extends Fragment implements View.OnClickListener {

    /* 定数 */
    private static final int REQUEST_GALLERY = 0;

    /* Views */
    private ImageView mCouponPreview;
    private TextView mLblFileName;
    private ChoiceDialog mMultipleChoiceDialog;

    /* カテゴリ用 */
    private JsonManager mJsonManager;
    private List<String> mCategories = new ArrayList<>();

    /* 選択されたクーポンファイルのパス */
    private String mCouponFilePath;

    /* S3アップロード関連フィールド */
    private S3UploadManager mUploadManager;
    private ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_coupon_upload, container, false);
        findViews(contentView);

        //初期設定
        initSettings();
        return contentView;
    }

    /**
     * 各種初期設定
     */
    private void initSettings() {
        //JsonManagerを初期化
        mJsonManager = new JsonManager(getActivity());
        //SharedPreferencesにクーポンのパスがある場合プレビューにセット
        setPreviousCoupon();
    }

    /**
     * レイアウトからViewを取得する
     */
    private void findViews(View contentView) {
        contentView.findViewById(R.id.btn_coupon_select).setOnClickListener(this);
        contentView.findViewById(R.id.btn_coupon_upload).setOnClickListener(this);
        contentView.findViewById(R.id.btn_category_select).setOnClickListener(this);
        mCouponPreview = (ImageView) contentView.findViewById(R.id.img_coupon_preview);
        mLblFileName = (TextView) contentView.findViewById(R.id.lbl_file_name);
    }

    /**
     * SharedPreferencesにクーポン画像のファイルパスを保存
     */
    private void savePrefs() {
        //SharedPreferencesにファイルパスをセット
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(Constants.COUPON_FILE_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.COUPON_FILE_PATH, getCouponFilePath());
        editor.apply();
    }

    /**
     * カテゴリ選択ダイアログを表示する
     */
    private void showCategoryChoiceDialog() {
        getMultipleChoiceDialog().show(getFragmentManager(), "MultipleChoiceDialog");
    }

    /**
     * JSONファイルにJSONオブジェクトを追加/更新
     */
    private void putJsonInfo() {
        CouponInfo info = new CouponInfo(getCategories());
        getJsonManager().putJsonObj(info);
    }

    /**
     * 選択されたクーポンをS3にアップロードする
     */
    private void beginUpload() {
        //ファイルパスがnullの場合Toastを表示してメソッドを抜ける
        if (getCouponFilePath() == null) {
            Toast.makeText(getActivity(), "File has not been set.", Toast.LENGTH_SHORT).show();
            return;
        }

        //アップロードするファイルリスト
        List<File> files = Arrays.asList(new File(getCouponFilePath()), getJsonManager().getFile());
        //ファイルをアップロードする
        List<TransferObserver> observerList =
                getUploadManager().uploadList(getActivity(), getProgressDialog(), files);
        //UploadObserversを生成
        UploadObservers uploadObservers = new UploadObservers(observerList);

        //ProgressDialogの最大値にObserversの合計ファイルサイズをセット
        getProgressDialog().setMax((int) uploadObservers.getBytesTotal());
        //ProgressDialogの初期値をセット
        getProgressDialog().setProgress(0);
        //ProgressDialogを表示
        getProgressDialog().show();
    }

    /**
     * クーポンを選択する
     */
    private void couponSelect() {
        Intent intent = new Intent();

        //APILevelが19(version 4.4)以上の場合
        if (Build.VERSION.SDK_INT >= 19) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("*/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        } else {
            //APILevelが18(version 4.3)以下の場合
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("image/*");
        }

        startActivityForResult(intent, REQUEST_GALLERY);
    }

    /* ▼ Setter ▼ */

    /**
     * ダイアログで選択したカテゴリリストをセットする
     *
     * @param data
     */
    private void setCategories(Intent data) {
        List<String> checkedCategories =
                data.getStringArrayListExtra(MultipleCategoryChoiceDialog.CHECKED_ITEMS);
        getCategories().clear();
        getCategories().addAll(checkedCategories);

        //JSONに書き込む
        putJsonInfo();
    }

    /**
     * クーポンをセットする
     *
     * @param path
     */
    private void setCoupon(String path) {
        //ファイルパスをセット
        setFilePath(path);
        //ファイルネームをセット
        setFileName(path);
        //プレビューに画像をセット
        setCouponPreview(path);
    }

    /**
     * 選択されたクーポン画像をプレビューにセット
     *
     * @param path
     */
    private void setCouponPreview(String path) {
        getCouponPreview().setImageBitmap(BitmapFactory.decodeFile(path));
    }

    /**
     * SharedPreferencesに保存したクーポンのパスがある場合、プレビューにセットする
     */
    private void setPreviousCoupon() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(Constants.COUPON_FILE_PATH, Context.MODE_PRIVATE);

        String path = prefs.getString(Constants.COUPON_FILE_PATH, null);
        if (path != null && !path.isEmpty()) {
            //SharedPreferencesにクーポンのパスが存在し、空じゃない場合クーポンをセット
            setCoupon(path);
        }
    }

    private void setFileName(String path) {
        mLblFileName.setText(getString(R.string.lbl_file_name) + FileUtil.extractFileName(path));
    }

    /**
     * ファイルパスをセット
     *
     * @param path
     */
    private void setFilePath(String path) {
        //ファイルパスをセット
        this.mCouponFilePath = path;

        //SharedPreferencesにクーポン画像のファイルパスを保存
        savePrefs();
    }

    /* ▼ Getter ▼ */

    public String getCouponFilePath() {
        return mCouponFilePath;
    }

    public S3UploadManager getUploadManager() {
        if (mUploadManager == null) {
            mUploadManager = new S3UploadManager(AwsUtil.getTransferUtility(getActivity()));
        }
        return mUploadManager;
    }

    public List<String> getCategories() {
        return mCategories;
    }

    public JsonManager getJsonManager() {
        return mJsonManager;
    }

    public ChoiceDialog getMultipleChoiceDialog() {
        if (mMultipleChoiceDialog == null) {
            mMultipleChoiceDialog = ChoiceDialog.newInstance(this, new MultipleCategoryChoiceDialog());
        }
        return mMultipleChoiceDialog;
    }

    public ImageView getCouponPreview() {
        return mCouponPreview;
    }

    public ProgressDialog getProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(getString(R.string.dialog_title_coupon_upload));
            mProgressDialog.setMessage(getString(R.string.dialog_message_coupon_upload));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
        }
        return mProgressDialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                String path = FileUtil.getPath(getActivity(), data.getData());
                setCoupon(path);
            } catch (URISyntaxException e) {
                Toast.makeText(
                        getActivity(),
                        "Unable to get the file from the given URI.  See error log for details",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        if (requestCode == MultipleCategoryChoiceDialog.REQUEST_ITEMS) {
            switch (resultCode) {
                case DialogInterface.BUTTON_POSITIVE:
                    setCategories(data);
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_coupon_select:
                couponSelect();
                break;
            case R.id.btn_coupon_upload:
                beginUpload();
                break;
            case R.id.btn_category_select:
                showCategoryChoiceDialog();
                break;
        }
    }

}