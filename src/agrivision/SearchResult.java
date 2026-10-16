package agrivision;

import java.util.*;

public class SearchResult {
    public final String algorithm;
    public final String pattern;
    public final List<Integer> positions;
    public final long timeNanos;

    public SearchResult(String algorithm, String pattern, List<Integer> positions, long timeNanos) {
        this.algorithm = algorithm;
        this.pattern = pattern;
        this.positions = positions;
        this.timeNanos = timeNanos;
    }

    public double timeMillis() {
        return timeNanos / 1_000_000.0;
    }
}
