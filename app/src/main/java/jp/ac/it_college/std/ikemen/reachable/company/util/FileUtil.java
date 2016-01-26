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
import java.net.URISyntaxException;

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
     * @param context
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;

        if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
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
                cursor = context.getContentResolver()
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
