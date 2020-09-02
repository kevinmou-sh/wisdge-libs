package com.wisdge.web.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadLocker {
    private List<Date> history = new ArrayList<>();
    private int minutes = 10;
    private int maxCount = 10;

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public boolean add() {
        Date now = new Date();
        while(removeExpired(now));
        if (history.size() >= maxCount)
            return false;
        return true;
    }

    private boolean removeExpired(Date now) {
        if (history.size() > 0 && history.get(0).getTime() < now.getTime() - 1000 * 60 * minutes) {
            history.remove(0);
            return true;
        }
        return false;
    }
}
