package id.universenetwork.sfa_loader.utils;

public final class TookTimeUtils {
    private final long time;

    public TookTimeUtils() {
        this.time = System.currentTimeMillis();
    }

    public String getTime() {
        return String.valueOf(System.currentTimeMillis() - time);
    }
}