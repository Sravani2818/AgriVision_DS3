package agrivision;

import java.util.*;

public class SuffixStructures {

    /*
     * SUFFIX ARRAY BY DOUBLING
     * Logic:
     * 1. Start by ranking suffixes using their first character.
     * 2. In each round, rank suffixes by two pieces: rank[i] and rank[i+k].
     * 3. Sort suffix indices by these pairs.
     * 4. Double k until every suffix has a unique rank.
     *
     * This implementation uses comparison sorting in each doubling round.
     * Therefore its practical worst-case complexity is O(n log^2 n), with
     * O(n) auxiliary arrays. (A radix/counting implementation can reach O(n log n).)
     */
    public static int[] buildSuffixArray(String text) {
        int n = text.length();
        int[] sa = new int[n];
        int[] rank = new int[n];
        int[] tmp = new int[n];

        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = text.charAt(i);
        }

        for (int k = 1; k < n; k <<= 1) {
            final int kk = k;
            Integer[] boxed = new Integer[n];
            for (int i = 0; i < n; i++) boxed[i] = sa[i];

            Arrays.sort(boxed, (a, b) -> {
                if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
                int ra = a + kk < n ? rank[a + kk] : -1;
                int rb = b + kk < n ? rank[b + kk] : -1;
                return Integer.compare(ra, rb);
            });

            for (int i = 0; i < n; i++) sa[i] = boxed[i];

            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                int a = sa[i - 1], b = sa[i];
                boolean different = rank[a] != rank[b];
                int ra = a + k < n ? rank[a + k] : -1;
                int rb = b + k < n ? rank[b + k] : -1;
                if (ra != rb) different = true;
                tmp[b] = tmp[a] + (different ? 1 : 0);
            }
            System.arraycopy(tmp, 0, rank, 0, n);

            if (rank[sa[n - 1]] == n - 1) break;
        }
        return sa;
    }

    /*
     * KASAI / LCP
     * Logic:
     * 1. LCP[i] stores the common-prefix length of suffix sa[i] and sa[i-1].
     * 2. The inverse suffix array tells the rank of every starting position.
     * 3. Reuse the previous LCP value minus one when moving to the next suffix.
     *
     * Complexity: O(n) after the suffix array is available.
     */
    public static int[] kasaiLcp(String text, int[] sa) {
        int n = text.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];

        for (int i = 0; i < n; i++) rank[sa[i]] = i;

        int h = 0;
        for (int i = 0; i < n; i++) {
            int r = rank[i];
            if (r == 0) continue;

            int j = sa[r - 1];
            while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                h++;
            }
            lcp[r] = h;
            if (h > 0) h--;
        }
        return lcp;
    }

    /*
     * SEARCH USING A SUFFIX ARRAY
     * Logic:
     * 1. Binary-search the suffix array for the first suffix that is >= pattern.
     * 2. Binary-search for the first suffix that is > pattern-prefix range.
     * 3. Every suffix in that interval starts with the pattern, so its suffix index
     *    is an exact occurrence position.
     */
    public static List<Integer> search(String text, String pattern, int[] sa) {
        List<Integer> ans = new ArrayList<>();
        if (pattern.isEmpty()) return ans;

        String p = pattern.toLowerCase(Locale.ROOT);
        String lower = text.toLowerCase(Locale.ROOT);

        int left = lowerBound(lower, p, sa);
        for (int i = left; i < sa.length; i++) {
            int pos = sa[i];
            if (startsWithAt(lower, pos, p)) ans.add(pos);
            else break;
        }

        Collections.sort(ans);
        return ans;
    }

    private static int lowerBound(String text, String p, int[] sa) {
        int lo = 0, hi = sa.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (compareSuffixWithPattern(text, sa[mid], p) < 0) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }

    private static int compareSuffixWithPattern(String text, int pos, String p) {
        int n = Math.min(text.length() - pos, p.length());
        for (int i = 0; i < n; i++) {
            char a = text.charAt(pos + i), b = p.charAt(i);
            if (a != b) return Character.compare(a, b);
        }
        return Integer.compare(text.length() - pos, p.length());
    }

    private static boolean startsWithAt(String text, int pos, String p) {
        return pos + p.length() <= text.length() &&
                text.regionMatches(pos, p, 0, p.length());
    }
}
