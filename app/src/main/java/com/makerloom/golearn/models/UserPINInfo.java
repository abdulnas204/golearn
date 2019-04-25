package com.makerloom.golearn.models;

import java.util.Calendar;
import java.util.Date;

public class UserPINInfo {
    private Date unlockedTill;

    public UserPINInfo() {}
    public UserPINInfo(Date unlockedTill) {
        setUnlockedTill(unlockedTill);
    }

    public boolean isLocked () {
        return Calendar.getInstance().before(unlockedTill) || null == unlockedTill;
    }

    public Date getUnlockedTill() {
        return unlockedTill;
    }

    public void setUnlockedTill(Date unlockedTill) {
        this.unlockedTill = unlockedTill;
    }
}
