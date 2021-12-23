package ro.adriantosca.experimentalservicecache.cache;

import ro.adriantosca.experimentalservicecache.cache.Timer;

public class TestTimer implements Timer {

    private long time = 0;

    public void tick() {
        time ++;
    }

    @Override
    public long nanoTime() {
        return time;
    }
}
