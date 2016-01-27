package jp.ac.it_college.std.ikemen.reachable.company.info;

import java.io.Serializable;

public class CompanyInfo implements Serializable {
    private final String id;
    private final String name;
    private final String address;

    public static final String COMPANY_ID = "company01";
    public static final String COMPANY_NAME = "company_01";
    public static final String COMPANY_ADDRESS = "okinawa";

    public CompanyInfo(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
