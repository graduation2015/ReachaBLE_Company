package jp.ac.it_college.std.ikemen.reachable.company.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

import jp.ac.it_college.std.ikemen.reachable.company.info.CompanyInfo;

public class FileUtil {
    /* JSON */
    public static final String FOLDER_SUFFIX = "/";
    public static final String FILE_DELIMITER = ".";
    private static final String OBJECT_KEY =
            CompanyInfo.COMPANY_ID + FOLDER_SUFFIX + CompanyInfo.COMPANY_ID;
    private static final String JSON_FILE_EXTENSION = ".json";

    /* IMAGE */
    public static final int IMG_THUMBNAIL_WIDTH = 1024;
    public static final int IMG_THUMBNAIL_HEIGHT = 576;
    public static final int IMG_FULL_SIZE_WIDTH = 2048;
    public static final int IMG_FULL_SIZE_HEIGHT = 1152;

    /**
     * ファイルの絶対パスを取得
     * @param context ContentResolver取得用のContext
     * @param uri ファイルのパス情報が入ったUri
     * @return ファイルのドキュメントタイプに応じて絶対パスを返す
     */
    public static String getPath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);

            if (isExternalStorageDocument(uri)) {
                return getExternalStorageDocUri(docId);
            } else if (isDownloadsDocument(uri)) {
                uri = getDownloadsDocUri(docId);
            } else if (isMediaDocument(uri)) {
                return getMediaDocUri(context, docId);
            }
        }

        return getRealPathFromUri(context, uri, null, null);
    }


    /**
     * ContentResolverを使用してUriからファイルの絶対パスを取得する
     * @param context ContentResolver取得用のContext
     * @param uri ファイルのパス情報が入ったUri
     * @param selection 行フィルタ。nullを渡すと指定されたURIのすべての行を返します。
     * @param selectionArgs selectionで指定した行をこの値で書き換える
     * @return ファイルの絶対パスを返す
     */
    private static String getRealPathFromUri(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
                cursor.close();
            }

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        return path;
    }

    /**
     * メディアドキュメントファイルの絶対パスを取得する
     * @param context ContentResolver取得用のContext
     * @param docId DocumentsContract.getDocumentId(Uri documentUri)で取得できるID
     * @return メディアドキュメントファイルの絶対パスを返す
     */
    private static String getMediaDocUri(Context context, String docId) {
        final String[] split = docId.split(":");
        final String type = split[0];
        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{split[1]};

        return getRealPathFromUri(context, getContentUriFromType(type), selection, selectionArgs);
    }

    /**
     * 内部ストレージドキュメントファイルの絶対パスを取得する
     * @param docId DocumentsContract.getDocumentId(Uri documentUri)で取得できるID
     * @return 内部ストレージドキュメントファイルの絶対パスを返す
     */
    private static String getExternalStorageDocUri(String docId) {
        final String[] split = docId.split(":");

        return Environment.getExternalStorageDirectory() + FOLDER_SUFFIX + split[1];
    }

    /**
     * ダウンロードドキュメントファイルの絶対パスを取得する
     * @param docId DocumentsContract.getDocumentId(Uri documentUri)で取得できるID
     * @return ダウンロードドキュメントファイルの絶対パスを返す
     */
    private static Uri getDownloadsDocUri(String docId) {
        return ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
    }

    /**
     * ファイルタイプに応じたCONTENT_URIを取得する
     * @param type ファイルタイプ
     * @return ファイルタイプに応じたCONTENT_URIを返す
     */
    private static Uri getContentUriFromType(String type) {
        Uri uri = null;
        if ("image".equals(type)) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        return uri;
    }

    /**
     * ストレージフォルダのファイルか判定
     *
     * @param uri
     * @return
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * ダウンロードフォルダのファイルか判定
     *
     * @param uri
     * @return
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * メディアフォルダのファイルか判定
     *
     * @param uri
     * @return
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 渡されたファイルの拡張子を文字列で返す
     * @param file
     * @return
     */
    public static String getFileExtension(File file) {
        int index = file.getName().lastIndexOf(FILE_DELIMITER);

        return file.getName().substring(index);
    }

    /**
     * ファイルの拡張子に応じたキー名を返す
     * @param file
     * @return
     */
    public static String getKey(File file) {
        String fileExtension = getFileExtension(file);
        if (fileExtension.equals(JSON_FILE_EXTENSION)) {
            return OBJECT_KEY + JSON_FILE_EXTENSION;
        }

        return OBJECT_KEY;
    }

    /**
     * 画像のサブサンプルサイズを計算して返す
     * @param options デコードした後のoptions
     * @param reqWidth 画像をセットするImageViewの横幅
     * @param reqHeight 画像をセットするImageViewの縦幅
     * @return 画像のサブサンプルサイズ
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 画像の元サイズ
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /**
     * 画像をリサイズしてBitmapで返す
     * @param filePath 読み込む画像のファイルパス
     * @param reqWidth 画像をセットするImageViewの横幅
     * @param reqHeight 画像をセットするImageViewの縦幅
     * @return サンプルサイズを計算しなおしたBitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        // inJustDecodeBounds=true で画像のサイズをチェック
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // inSampleSize を計算
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // inSampleSize をセットしてデコード
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

}
