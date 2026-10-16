package agrivision;

import java.util.*;

public class StringAlgorithms {

    /*
     * NAIVE STRING SEARCH
     * Logic:
     * 1. Put the pattern at every possible starting position.
     * 2. Compare pattern characters one by one with the text.
     * 3. If every character matches, record that starting index.
     * 4. Shift the pattern by one character and continue.
     *
     * Complexity: O(n*m) worst case, O(1) extra space.
     */
    public static List<Integer> naiveSearch(String text, String pattern) {
        List<Integer> ans = new ArrayList<>();
        if (pattern.isEmpty() || pattern.length() > text.length()) return ans;

        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length() &&
                    Character.toLowerCase(text.charAt(i + j)) ==
                    Character.toLowerCase(pattern.charAt(j))) {
                j++;
            }
            if (j == pattern.length()) ans.add(i);
        }
        return ans;
    }

    /*
     * KMP (Knuth-Morris-Pratt)
     * Logic:
     * 1. Build LPS = Longest Proper Prefix which is also Suffix.
     * 2. When a mismatch occurs, LPS tells us how far the pattern can
     *    move without rechecking characters that are already known to match.
     * 3. Scan the text once and record every complete match.
     *
     * Complexity: O(n+m), O(m) space for the LPS table.
     */
    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> ans = new ArrayList<>();
        if (pattern.isEmpty()) return ans;

        String t = text.toLowerCase(Locale.ROOT);
        String p = pattern.toLowerCase(Locale.ROOT);
        int[] lps = buildLPS(p);

        int i = 0, j = 0;
        while (i < t.length()) {
            if (t.charAt(i) == p.charAt(j)) {
                i++; j++;
                if (j == p.length()) {
                    ans.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return ans;
    }

    // LPS logic: lps[i] stores the length of the longest proper prefix
    // of p[0..i] that is also a suffix of p[0..i].
    public static int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0;
        for (int i = 1; i < p.length();) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    /*
     * Z ALGORITHM
     * Logic:
     * 1. Create S = pattern + separator + text.
     * 2. Z[i] tells how many characters from S[i...] match the prefix of S.
     * 3. If Z[i] equals pattern length, a pattern occurrence starts at
     *    i - patternLength - 1 in the original text.
     * 4. The [L,R] Z-box avoids repeated comparisons.
     *
     * Complexity: O(n+m), O(n+m) space.
     */
    public static List<Integer> zSearch(String text, String pattern) {
        List<Integer> ans = new ArrayList<>();
        if (pattern.isEmpty()) return ans;

        String p = pattern.toLowerCase(Locale.ROOT);
        String t = text.toLowerCase(Locale.ROOT);
        String s = p + "\u0001" + t;
        int[] z = new int[s.length()];

        int l = 0, r = 0;
        for (int i = 1; i < s.length(); i++) {
            if (i <= r) z[i] = Math.min(r - i + 1, z[i - l]);

            while (i + z[i] < s.length() && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }

            if (z[i] == p.length()) {
                ans.add(i - p.length() - 1);
            }
        }
        return ans;
    }

    /*
     * RABIN-KARP
     * Logic:
     * 1. Compute a rolling hash for the pattern and the current text window.
     * 2. If hashes differ, the window cannot match.
     * 3. If hashes are equal, verify characters to eliminate hash-collision errors.
     * 4. Update the next window hash in O(1) using the rolling formula.
     *
     * Expected: O(n+m), Worst case: O(n*m) because of collisions.
     * Space: O(1) besides the result list.
     */
    public static List<Integer> rabinKarp(String text, String pattern) {
        List<Integer> ans = new ArrayList<>();
        if (pattern.isEmpty() || pattern.length() > text.length()) return ans;

        String t = text.toLowerCase(Locale.ROOT);
        String p = pattern.toLowerCase(Locale.ROOT);

        final long base = 257;
        final long mod = 1_000_000_007L;
        int m = p.length();

        long high = 1;
        for (int i = 1; i < m; i++) high = (high * base) % mod;

        long ph = 0, th = 0;
        for (int i = 0; i < m; i++) {
            ph = (ph * base + p.charAt(i)) % mod;
            th = (th * base + t.charAt(i)) % mod;
        }

        for (int i = 0; i <= t.length() - m; i++) {
            if (ph == th && t.regionMatches(i, p, 0, m)) ans.add(i);

            if (i < t.length() - m) {
                th = (th - t.charAt(i) * high) % mod;
                if (th < 0) th += mod;
                th = (th * base + t.charAt(i + m)) % mod;
            }
        }
        return ans;
    }

    /*
     * AHO-CORASICK
     * Logic:
     * 1. Insert all keywords into a Trie.
     * 2. Build failure links using BFS.
     * 3. A failure link tells us the longest suffix that is also a Trie prefix.
     * 4. Scan the text once; every reached output node reports a keyword match.
     *
     * Complexity: O(text length + total pattern length + number of matches)
     * after failure links are built.
     */
    public static Map<String, List<Integer>> ahoCorasick(String text, List<String> patterns) {
        class Node {
            int[] next = new int[256];
            int fail = 0;
            List<String> out = new ArrayList<>();
            Node() { Arrays.fill(next, -1); }
        }

        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node());

        for (String original : patterns) {
            String p = original.toLowerCase(Locale.ROOT);
            int v = 0;
            for (char ch : p.toCharArray()) {
                int c = ch < 256 ? ch : 0;
                if (nodes.get(v).next[c] == -1) {
                    nodes.get(v).next[c] = nodes.size();
                    nodes.add(new Node());
                }
                v = nodes.get(v).next[c];
            }
            nodes.get(v).out.add(original);
        }

        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int c = 0; c < 256; c++) {
            int u = nodes.get(0).next[c];
            if (u != -1) q.add(u);
            else nodes.get(0).next[c] = 0;
        }

        while (!q.isEmpty()) {
            int v = q.remove();
            int f = nodes.get(v).fail;
            // Merge inherited outputs so suffix-pattern matches are also reported.
            if (f != 0) nodes.get(v).out.addAll(nodes.get(f).out);

            for (int c = 0; c < 256; c++) {
                int u = nodes.get(v).next[c];
                if (u != -1) {
                    nodes.get(u).fail = nodes.get(f).next[c];
                    q.add(u);
                } else {
                    nodes.get(v).next[c] = nodes.get(f).next[c];
                }
            }
        }

        Map<String, List<Integer>> result = new LinkedHashMap<>();
        for (String p : patterns) result.put(p, new ArrayList<>());

        String t = text.toLowerCase(Locale.ROOT);
        int state = 0;
        for (int i = 0; i < t.length(); i++) {
            char ch = t.charAt(i);
            int c = ch < 256 ? ch : 0;
            state = nodes.get(state).next[c];

            for (String matched : nodes.get(state).out) {
                result.get(matched).add(i - matched.length() + 1);
            }
        }
        return result;
    }

    /*
     * TRIE WITH POSITION LISTS
     * Logic:
     * 1. Tokenize the agricultural text into words.
     * 2. Insert every word character-by-character into a Trie.
     * 3. At the terminal node, store every character position where that word starts.
     * 4. Searching a word follows only its characters, then returns exact positions.
     *
     * This is a Trie-based index: search is O(m) for a word of length m.
     */
    public static class PositionTrie {
        private static class Node {
            Map<Character, Node> child = new HashMap<>();
            List<Integer> positions = new ArrayList<>();
        }

        private final Node root = new Node();

        public void insert(String word, int position) {
            String w = word.toLowerCase(Locale.ROOT);
            Node cur = root;
            for (char c : w.toCharArray()) {
                cur = cur.child.computeIfAbsent(c, k -> new Node());
            }
            cur.positions.add(position);
        }

        public List<Integer> search(String word) {
            Node cur = root;
            for (char c : word.toLowerCase(Locale.ROOT).toCharArray()) {
                cur = cur.child.get(c);
                if (cur == null) return new ArrayList<>();
            }
            return new ArrayList<>(cur.positions);
        }

        public static PositionTrie build(String text) {
            PositionTrie trie = new PositionTrie();
            int i = 0;
            while (i < text.length()) {
                while (i < text.length() && !Character.isLetterOrDigit(text.charAt(i))) i++;
                int start = i;
                while (i < text.length() && Character.isLetterOrDigit(text.charAt(i))) i++;
                if (start < i) trie.insert(text.substring(start, i), start);
            }
            return trie;
        }
    }

    // Helper for timing a search algorithm.
    public static SearchResult timed(String algorithm, String text, String pattern,
                                     java.util.function.BiFunction<String, String, List<Integer>> fn) {
        long start = System.nanoTime();
        List<Integer> positions = fn.apply(text, pattern);
        long end = System.nanoTime();
        return new SearchResult(algorithm, pattern, positions, end - start);
    }
}
