package jp.ac.it_college.std.ikemen.reachable.company.info;

import java.util.List;

public class CouponInfo {
    private final String key;
    private final String companyName;
    private final String address;
    private final String title;
    private final String description;
    private final List<String> category;

    public static final String COMPANY_NAME = "companyName";
    public static final String ADDRESS = "address";
    public static final String CATEGORY = "category";
    public static final String TITLE = "category";
    public static final String DESCRIPTION = "category";


    public CouponInfo(List<String> category) {
        this(CompanyInfo.COMPANY_ID, CompanyInfo.COMPANY_NAME,
                CompanyInfo.COMPANY_ADDRESS, null, null, category);
    }

    public CouponInfo(String title, String description, List<String> category) {
        this(CompanyInfo.COMPANY_ID, CompanyInfo.COMPANY_NAME,
                CompanyInfo.COMPANY_ADDRESS, title, description, category);
    }

    public CouponInfo(String key, String companyName, String address,
                      String title, String description, List<String> category) {
        this.key = key;
        this.companyName = companyName;
        this.address = address;
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
