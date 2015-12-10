package jp.ac.it_college.std.ikemen.reachable.company.info;

import java.io.Serializable;
import java.util.List;

public class CouponInfo implements Serializable {
    private final String key;
    private final String companyName;
    private final String address;
    private final String filePath;
    private final String title;
    private final String description;
    private final List<String> category;

    public static final String PREF_INFO = "pref_info";
    public static final String FILE_PATH = "file_path";
    public static final String COMPANY_NAME = "companyName";
    public static final String ADDRESS = "address";
    public static final String CATEGORY = "category";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";


    public CouponInfo(String filePath, String title, String description, List<String> category) {
        this(CompanyInfo.COMPANY_ID, CompanyInfo.COMPANY_NAME,
                CompanyInfo.COMPANY_ADDRESS, filePath, title, description, category);
    }

    public CouponInfo(String key, String companyName, String address, String filePath,
                      String title, String description, List<String> category) {
        this.key = key;
        this.companyName = companyName;
        this.address = address;
        this.filePath = filePath;
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getCategory() {
        return category;
    }

    /**
     * カテゴリーリストをカンマ区切りの文字列にする
     * @return
     */
    public String getCategoryToString() {
        StringBuilder sb = new StringBuilder();
        String separator = ",";

        for (String str : getCategory()) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(str);
        }

        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFilePath() {
        return filePath;
    }
}
