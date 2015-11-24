package jp.ac.it_college.std.reachable_company.json;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jp.ac.it_college.std.reachable_company.CouponInfo;

public class JsonManager {

    private static final String FILE_NAME = "info.json";
    public static final String TAG = "JsonManager";

    private File file;
    private JsonDataWriter jsonDataWriter;
    private JsonDataReader jsonDataReader;

    public JsonManager(Context context) {
        File dir = createExternalStorageDir(context, Environment.DIRECTORY_DOCUMENTS);
        file = new File(dir, FILE_NAME);

        jsonDataWriter = new JsonDataWriter();
        jsonDataReader = new JsonDataReader();

        // jsonファイルがない場合作る
        if (!file.exists()) {
            try {
                jsonDataWriter.initJsonObj(file);
                Toast.makeText(context, "JSON file was created", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile() {
        return file;
    }

    /**
     * JSONオブジェクトを返す
     *
     * @return
     */
    public JSONObject getJsonRootObject() {
        JSONObject jsonObject = null;

        try {
            InputStream is = new FileInputStream(getFile());
            jsonObject = new JSONObject(jsonDataReader.getJsonStr(is));
        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 外部ストレージにディレクトリを作成する
     *
     * @param context
     * @param dirType Environmentのディレクトリータイプ
     * @return
     */
    private File createExternalStorageDir(Context context, String dirType) {
        File file = new File(context.getExternalFilesDir(dirType).getPath());

        if (!file.mkdirs()) {
            Toast.makeText(context, "File already exists", Toast.LENGTH_SHORT).show();
        }

        return file;
    }

    /**
     * 読み書き可能な外部ストレージをチェック
     *
     * @return
     */
    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 少なくとも読み取りは可能な外部ストレージか、チェック
     *
     * @return
     */
    private boolean isExternalStorageReadable() {
        return isExternalStorageWritable()
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }


    /**
     * JSONファイルにJSONオブジェクトを追加/更新
     * @param info
     */
    public void putJsonObj(CouponInfo info) {
        JSONObject rootObj = getJsonRootObject();
        try {
            FileOutputStream outputStream = new FileOutputStream(getFile(), false);
            jsonDataWriter.writeJson(outputStream, rootObj, info);
            outputStream.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

}
