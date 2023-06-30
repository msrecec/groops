package hr.tvz.groops.constants;

public class TimeoutConstants {
    public static final int MS = 1000;
    public static final int TINY_TIMEOUT = 5;

    public static final int SHORT_TIMEOUT = 10;
    public static final int MEDIUM_SHORT_TIMEOUT = 20;

    public static final int DEFAULT_TIMEOUT = 30;
    public static final int MEDIUM_TIMEOUT = 90;
    public static final int LONG_TIMEOUT = 180;
    public static final int VERY_LONG_TIMEOUT = 600;
    public static final int EXTREMELY_LONG_TIMEOUT = 1200;
    public static final String TINY_TIMEOUT_MS = "" + TINY_TIMEOUT * MS;

    public static final String SHORT_TIMEOUT_MS = "" + SHORT_TIMEOUT * MS;
    public static final String MEDIUM_SHORT_TIMEOUT_MS = "" + MEDIUM_SHORT_TIMEOUT * MS;
    public static final String DEFAULT_TIMEOUT_MS = "" + DEFAULT_TIMEOUT * MS;
    public static final String MEDIUM_TIMEOUT_MS = "" + MEDIUM_TIMEOUT * MS;
    public static final String LONG_TIMEOUT_MS = "" + LONG_TIMEOUT * MS;
}
