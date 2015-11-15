package jp.ac.it_college.std.reachable_company;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class CouponUploadFragment extends Fragment implements View.OnClickListener {

    /* ギャラリー用の定数 */
    private static final int REQUEST_GALLERY = 0;

    /* Views */
    private ImageView couponPreview;
    private TextView lblFileName;
    private ChoiceDialog multipleChoiceDialog;

    /* カテゴリ用 */
    private JsonManager jsonManager;
    private List<String> categories = new ArrayList<>();

    /* 選択されたファイルのパス */
    private String mFilePath;

    /* S3アップロード関連フィールド */
    private TransferUtility mTransferUtility;
    private S3UploadManager mUploadManager;


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
        //ダイアログを取得
        multipleChoiceDialog = ChoiceDialog.newInstance(this, new MultipleCategoryChoiceDialog());

        //TransferUtilityを取得
        mTransferUtility =
                ((MainActivity) getActivity()).getClientManager().getTransferUtility(getActivity());

        //S3UploadManagerのインスタンス生成
        mUploadManager = new S3UploadManager(mTransferUtility);

        //JsonManagerを取得
        jsonManager = new JsonManager(getActivity());

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
        couponPreview = (ImageView) contentView.findViewById(R.id.img_coupon_preview);
        lblFileName = (TextView) contentView.findViewById(R.id.lbl_file_name);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                String path = FileUtility.getPath(getActivity(), data.getData());
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

    /**
     * カテゴリ選択ダイアログを表示する
     */
    private void showCategoryChoiceDialog() {
        multipleChoiceDialog.show(getFragmentManager(), "multipleChoiceDialog");
    }

    /**
     * ダイアログで選択したカテゴリリストをセットする
     *
     * @param data
     */
    private void setCategories(Intent data) {
        List<String> checkedCategories =
                data.getStringArrayListExtra(MultipleCategoryChoiceDialog.CHECKED_ITEMS);
        categories.clear();
        categories.addAll(checkedCategories);

        //JSONに書き込む
        putJsonInfo();
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
        if (getFilePath() == null) {
            Toast.makeText(getActivity(), "File has not been set.", Toast.LENGTH_SHORT).show();
            return;
        }

        //ファイルをアップロードする
        File file = new File(getFilePath());
        TransferObserver observer = getUploadManager().upload(file);

        //TransferListenerをセット
        observer.setTransferListener(new CouponUploadListener(getActivity()));
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
        setCouponPreview(getFilePath());
    }

    /**
     * 選択されたクーポン画像をプレビューにセット
     *
     * @param path
     */
    private void setCouponPreview(String path) {
        couponPreview.setImageBitmap(BitmapFactory.decodeFile(path));
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
            setFilePath(path);
            setFileName(path);
            setCouponPreview(path);
        }
    }

    private void setFileName(String path) {
        lblFileName.setText(getString(R.string.lbl_file_name) + extractFileName(path));
    }

    private String extractFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * ファイルパスをセット
     *
     * @param path
     */
    private void setFilePath(String path) {
        //ファイルパスをセット
        this.mFilePath = path;

        //SharedPreferencesにファイルパスをセット
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(Constants.COUPON_FILE_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.COUPON_FILE_PATH, mFilePath);
        editor.apply();
    }

    public String getFilePath() {
        return mFilePath;
    }

    public S3UploadManager getUploadManager() {
        return mUploadManager;
    }

    public List<String> getCategories() {
        return categories;
    }

    public JsonManager getJsonManager() {
        return jsonManager;
    }
}
