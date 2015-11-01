package jp.ac.it_college.std.reachable_company;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.net.URISyntaxException;


public class CouponUploadFragment extends Fragment implements View.OnClickListener {

    /** ギャラリー用の定数 */
    private static final int REQUEST_GALLERY = 0;

    /** Views */
    private Button couponSelectButton;
    private Button couponUploadButton;
    private ImageView couponPreview;
    private TextView lblFileName;

    /** 選択されたファイルのパス */
    private String mFilePath;

    /** S3アップロード関連フィールド */
    private TransferUtility mTransferUtility;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_coupon_upload, container, false);
        findViews(contentView);

        //TransferUtilityを設定
        setUpTransferUtility();
        return contentView;
    }

    /**
     * TransferUtilityを取得
     */
    private void setUpTransferUtility() {
        mTransferUtility =
                ((MainActivity) getActivity()).getClientManager().getTransferUtility(getActivity());
    }

    /**
     * レイアウトからViewを取得する
     */
    private void findViews(View contentView) {
        couponSelectButton = (Button) contentView.findViewById(R.id.btn_coupon_select);
        couponUploadButton = (Button) contentView.findViewById(R.id.btn_coupon_upload);
        couponPreview = (ImageView) contentView.findViewById(R.id.img_coupon_preview);
        lblFileName = (TextView) contentView.findViewById(R.id.lbl_file_name);

        couponSelectButton.setOnClickListener(this);
        couponUploadButton.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            setFile(data);
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
        }
    }

    /**
     * 選択されたクーポンをS3にアップロードする
     */
    private void beginUpload() {
        //ファイルパスがnullの場合Toastを表示してメソッドを抜ける
        if (mFilePath == null) {
            Toast.makeText(getActivity(), "File has not been set.", Toast.LENGTH_SHORT).show();
            return;
        }

        //ファイルをアップロードする
        File file = new File(mFilePath);
        TransferObserver observer = mTransferUtility.upload(
                Constants.BUCKET_NAME,
                file.getName(),
                file);

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
     * クーポンのファイルパスをセット
     * @param data
     */
    private void setFile(Intent data) {
        try {
            //ファイルパスをセット
            mFilePath = getPath(data.getData());
            //ファイルネームをセット
            setFileName(data.getData());
            //プレビューに画像をセット
            setCouponPreview(mFilePath);
        } catch (URISyntaxException e) {
            Toast.makeText(
                    getActivity(),
                    "Unable to get the file from the given URI.  See error log for details",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 選択されたクーポン画像をプレビューにセット
     * @param path
     */
    private void setCouponPreview(String path) {
        couponPreview.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    /**
     * ファイルネームをラベルにセット
     */
    private void setFileName(Uri uri) {
        lblFileName.setText(getString(R.string.lbl_file_name) + getFileName(uri));
    }

    /**
     * ファイル名を取得
     * @param uri
     * @return
     */
    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor =
                getActivity().getContentResolver().query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileName = cursor.getString(nameIdx);
        }

        return fileName;
    }

    /**
     * ファイルの絶対パスを取得
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;

        if (needToCheckUri && DocumentsContract.isDocumentUri(getActivity(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");

                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);

                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };

            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * ストレージフォルダのファイルか判定
     * @param uri
     * @return
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * ダウンロードフォルダのファイルか判定
     * @param uri
     * @return
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * メディアフォルダのファイルか判定
     * @param uri
     * @return
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
