package jp.ac.it_college.std.ikemen.reachable.company;

public interface TransferStateListener {
    void onCompleted();
    void onFailed(String fileName);
}
