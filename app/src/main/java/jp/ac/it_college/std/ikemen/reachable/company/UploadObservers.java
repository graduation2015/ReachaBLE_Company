package jp.ac.it_college.std.ikemen.reachable.company;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

import java.util.ArrayList;
import java.util.List;

public class UploadObservers {
    private List<TransferObserver> observers = new ArrayList<>();

    public UploadObservers(List<TransferObserver> observers) {
        this.observers = observers;
    }

    public List<TransferObserver> getObservers() {
        return observers;
    }

    /**
     * TransferObserverのファイルサイズの合計を返す
     * @return
     */
    public long getBytesTotal() {
        long total = 0;

        for (TransferObserver observer : getObservers()) {
            total += observer.getBytesTotal();
        }

        return total;
    }
}
