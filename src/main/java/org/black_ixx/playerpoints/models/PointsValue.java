package org.black_ixx.playerpoints.models;

import java.util.concurrent.TimeUnit;
import org.black_ixx.playerpoints.manager.ConfigurationManager.Setting;

public class PointsValue {

    private int value;
    private long lastAccessed;

    public PointsValue(int value) {
        this.value = value;
        this.lastAccessed = System.currentTimeMillis();
    }

    public int getValue() {
        this.lastAccessed = System.currentTimeMillis();
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        this.lastAccessed = System.currentTimeMillis();
    }

    public boolean isStale() {
        return this.lastAccessed + TimeUnit.SECONDS.toMillis(Setting.CACHE_DURATION.getInt()) < System.currentTimeMillis();
    }

}
