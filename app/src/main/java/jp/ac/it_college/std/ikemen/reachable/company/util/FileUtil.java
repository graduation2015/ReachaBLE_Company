package jp.ac.it_college.std.ikemen.reachable.company.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.net.URISyntaxException;

import jp.ac.it_college.std.ikemen.reachable.company.info.CompanyInfo;

public class FileUtil {
    public static final String FOLDER_SUFFIX = "/";
    public static final String FILE_DELIMITER = ".";
    private static final String OBJECT_KEY =
            CompanyInfo.COMPANY_ID + FOLDER_SUFFIX + CompanyInfo.COMPANY_ID;
    private static final String JSON_FILE_EXTENSION = ".json";

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
     * ファイルパスからファイル名のみを抜き取る
     * @param path
     * @return
     */
    public static String extractFileName(String path) {
        return path.substring(path.lastIndexOf(FOLDER_SUFFIX) + 1);
    }


}
