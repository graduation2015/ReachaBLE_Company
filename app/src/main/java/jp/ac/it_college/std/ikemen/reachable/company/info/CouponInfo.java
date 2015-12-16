package jp.ac.it_college.std.ikemen.reachable.company.info;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CouponInfo implements Serializable {
    private final CompanyInfo companyInfo;
    private final String filePath;
    private final String title;
    private final String description;
    private final List<String> category;
    private final Date creationDate;

    public static final String PREF_INFO = "pref_info";
    public static final String FILE_PATH = "file_path";
    public static final String COMPANY_NAME = "companyName";
    public static final String ADDRESS = "address";
    public static final String CATEGORY = "category";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    public static final String DATE_FORMAT_PATTERN = "yyyy年MM月dd日";


    public CouponInfo(String filePath, String title, String description, List<String> category) {
        this(new CompanyInfo(
                CompanyInfo.COMPANY_ID, CompanyInfo.COMPANY_NAME, CompanyInfo.COMPANY_ADDRESS),
                filePath, title, description, category);
    }

    public CouponInfo(CompanyInfo companyInfo, String filePath,
                      String title, String description, List<String> category) {
        this.companyInfo = companyInfo;
        this.filePath = filePath;
        this.title = title;
        this.description = description;
        this.category = category;
        this.creationDate = new Date(System.currentTimeMillis());
    }

    public String getKey() {
        return getCompanyInfo().getId();
    }

    public String getCompanyName() {
        return getCompanyInfo().getName();
    }

    public String getAddress() {
        return getCompanyInfo().getAddress();
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

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * フォーマットされた作成日を返す
     * @return yyyy年MM月dd日 形式の文字列
     */
    public String getFormattedCreationDate() {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.JAPAN).format(getCreationDate());
    }
}
