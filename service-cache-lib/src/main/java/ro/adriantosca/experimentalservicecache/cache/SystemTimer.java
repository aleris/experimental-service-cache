package ro.adriantosca.experimentalservicecache.cache;

public class SystemTimer implements Timer {
    public static final SystemTimer INSTANCE = new SystemTimer();

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
