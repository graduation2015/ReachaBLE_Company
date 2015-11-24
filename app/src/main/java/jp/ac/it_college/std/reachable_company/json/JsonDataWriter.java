package jp.ac.it_college.std.reachable_company.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jp.ac.it_college.std.reachable_company.CouponInfo;

public class JsonDataWriter {

    public static final int DEFAULT_INDENT_SPACE = 4;

    public void writeJson(OutputStream out, JSONObject rootObject, CouponInfo info) throws JSONException, IOException {
        rootObject.put(info.getKey(), writeData(info));

        out.write(rootObject.toString(DEFAULT_INDENT_SPACE).getBytes());
    }

    private JSONObject writeData(CouponInfo info) throws JSONException {
        JSONObject data = new JSONObject();
        data.put(CouponInfo.NAME, info.getName());
        data.put(CouponInfo.ADDRESS, info.getAddress());
        data.put(CouponInfo.CATEGORY, new JSONArray(info.getCategory()));

        return data;
    }

    /**
     * 外部ストレージに空のJSONファイルを新しく作成する
     */
    protected void initJsonObj(File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file, false);
        outputStream.write(new JSONObject().toString().getBytes());
        outputStream.close();
    }

}
