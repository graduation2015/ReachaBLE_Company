package jp.ac.it_college.std.ikemen.reachable.company.json;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;

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
                Log.d(TAG, "JSON file was created");
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
            Log.d(TAG, "File already exists");
        }

        return file;
    }

    /**
     * JSONファイルにJSONオブジェクトを追加/更新
     * @param info
     */
    public void putJsonObj(CouponInfo info) throws IOException, JSONException {
        JSONObject rootObj = getJsonRootObject();
        FileOutputStream outputStream = new FileOutputStream(getFile(), false);
        jsonDataWriter.writeJson(outputStream, rootObj, info);
        outputStream.close();
    }

}
