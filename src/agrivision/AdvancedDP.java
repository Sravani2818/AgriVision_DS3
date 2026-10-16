package agrivision;

import java.util.*;

public class AdvancedDP {

    /*
     * LEVENSHTEIN / WAGNER-FISCHER EDIT DISTANCE
     * Logic:
     * dp[i][j] = minimum edits needed to convert first i characters of A
     * into first j characters of B.
     * Three possibilities:
     *   delete  -> dp[i-1][j] + 1
     *   insert  -> dp[i][j-1] + 1
     *   replace -> dp[i-1][j-1] + cost
     * Base cases represent converting to/from an empty string.
     *
     * Complexity: O(n*m) time and O(n*m) space for the full table.
     */
    public static int editDistance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = Character.toLowerCase(a.charAt(i - 1)) ==
                           Character.toLowerCase(b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[n][m];
    }

    /*
     * DAMERAU-LEVENSHTEIN
     * Logic:
     * Adds one more operation to ordinary edit distance: transposition
     * of two adjacent characters. Example: "ab" -> "ba".
     * The DP table chooses the minimum among insertion, deletion, substitution,
     * and adjacent transposition.
     *
     * Complexity: O(n*m) time and O(n*m) space.
     */
    public static int damerauLevenshtein(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] d = new int[n + 2][m + 2];
        int inf = n + m;
        d[0][0] = inf;

        for (int i = 0; i <= n; i++) {
            d[i + 1][0] = inf;
            d[i + 1][1] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j + 1] = inf;
            d[1][j + 1] = j;
        }

        Map<Character, Integer> last = new HashMap<>();
        for (int i = 1; i <= n; i++) {
            int db = 0;
            for (int j = 1; j <= m; j++) {
                int i1 = last.getOrDefault(b.charAt(j - 1), 0);
                int j1 = db;

                int cost = Character.toLowerCase(a.charAt(i - 1)) ==
                           Character.toLowerCase(b.charAt(j - 1)) ? 0 : 1;
                if (cost == 0) db = j;

                d[i + 1][j + 1] = Math.min(
                    Math.min(d[i][j] + cost, d[i + 1][j] + 1),
                    Math.min(d[i][j + 1] + 1,
                             d[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1))
                );
            }
            last.put(a.charAt(i - 1), i);
        }
        return d[n + 1][m + 1];
    }

    /*
     * NEEDLEMAN-WUNSCH GLOBAL ALIGNMENT
     * Logic:
     * 1. Build a DP matrix for the complete sequences.
     * 2. Match = +1, mismatch = -1, gap = -1.
     * 3. Each cell chooses the maximum of diagonal, up, and left.
     * 4. Trace back from the bottom-right cell to reconstruct a global alignment.
     *
     * Complexity: O(n*m) time and O(n*m) space.
     */
    public static AlignmentResult needlemanWunsch(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        final int MATCH = 1, MISMATCH = -1, GAP = -1;

        for (int i = 1; i <= n; i++) dp[i][0] = i * GAP;
        for (int j = 1; j <= m; j++) dp[0][j] = j * GAP;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int diag = dp[i - 1][j - 1] +
                        (Character.toLowerCase(a.charAt(i - 1)) ==
                         Character.toLowerCase(b.charAt(j - 1)) ? MATCH : MISMATCH);
                int up = dp[i - 1][j] + GAP;
                int left = dp[i][j - 1] + GAP;
                dp[i][j] = Math.max(diag, Math.max(up, left));
            }
        }

        StringBuilder aa = new StringBuilder();
        StringBuilder bb = new StringBuilder();
        int i = n, j = m;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0) {
                int score = Character.toLowerCase(a.charAt(i - 1)) ==
                            Character.toLowerCase(b.charAt(j - 1)) ? MATCH : MISMATCH;
                if (dp[i][j] == dp[i - 1][j - 1] + score) {
                    aa.append(a.charAt(i - 1));
                    bb.append(b.charAt(j - 1));
                    i--; j--; continue;
                }
            }
            if (i > 0 && dp[i][j] == dp[i - 1][j] + GAP) {
                aa.append(a.charAt(i - 1));
                bb.append('-');
                i--;
            } else {
                aa.append('-');
                bb.append(b.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(dp[n][m], aa.reverse().toString(), bb.reverse().toString());
    }

    /*
     * SMITH-WATERMAN LOCAL ALIGNMENT
     * Logic:
     * 1. Build a DP matrix like global alignment, but include 0.
     * 2. 0 means "start a new local alignment here".
     * 3. Take the maximum among 0, diagonal, up, and left.
     * 4. Start traceback at the highest-scoring cell and stop when 0 is reached.
     *
     * Complexity: O(n*m) time and O(n*m) space.
     */
    public static AlignmentResult smithWaterman(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        final int MATCH = 2, MISMATCH = -1, GAP = -1;

        int best = 0, bestI = 0, bestJ = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int diag = dp[i - 1][j - 1] +
                        (Character.toLowerCase(a.charAt(i - 1)) ==
                         Character.toLowerCase(b.charAt(j - 1)) ? MATCH : MISMATCH);
                int up = dp[i - 1][j] + GAP;
                int left = dp[i][j - 1] + GAP;
                dp[i][j] = Math.max(0, Math.max(diag, Math.max(up, left)));

                if (dp[i][j] > best) {
                    best = dp[i][j];
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        StringBuilder aa = new StringBuilder();
        StringBuilder bb = new StringBuilder();
        int i = bestI, j = bestJ;

        while (i > 0 && j > 0 && dp[i][j] != 0) {
            int score = Character.toLowerCase(a.charAt(i - 1)) ==
                        Character.toLowerCase(b.charAt(j - 1)) ? MATCH : MISMATCH;

            if (dp[i][j] == dp[i - 1][j - 1] + score) {
                aa.append(a.charAt(i - 1));
                bb.append(b.charAt(j - 1));
                i--; j--;
            } else if (dp[i][j] == dp[i - 1][j] + GAP) {
                aa.append(a.charAt(i - 1));
                bb.append('-');
                i--;
            } else {
                aa.append('-');
                bb.append(b.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(best, aa.reverse().toString(), bb.reverse().toString());
    }

    /*
     * BITMASK DP – MINIMUM COST RESOURCE ASSIGNMENT
     * Agriculture interpretation:
     * rows = field/resource tasks, columns = available resources.
     * cost[i][j] = cost of assigning resource j to task i.
     *
     * Logic:
     * mask represents which resources have already been used.
     * dp[mask] stores the minimum cost for assigning the first popcount(mask) tasks.
     * Try every unused resource, create nextMask, and relax its cost.
     *
     * Complexity: O(n * 2^n), O(2^n) space.
     * This is intentionally used with a small number of resources because
     * bitmask DP is exponential by nature.
     */
    public static AssignmentResult bitmaskAssignment(int[][] cost) {
        int n = cost.length;
        if (n == 0 || cost[0].length != n) throw new IllegalArgumentException("Matrix must be n x n.");
        int total = 1 << n;
        int INF = Integer.MAX_VALUE / 4;
        int[] dp = new int[total];
        int[] parent = new int[total];
        int[] chosen = new int[total];
        Arrays.fill(dp, INF);
        Arrays.fill(parent, -1);
        Arrays.fill(chosen, -1);
        dp[0] = 0;

        for (int mask = 0; mask < total; mask++) {
            int task = Integer.bitCount(mask);
            if (task >= n || dp[mask] == INF) continue;

            for (int resource = 0; resource < n; resource++) {
                if ((mask & (1 << resource)) == 0) {
                    int next = mask | (1 << resource);
                    int newCost = dp[mask] + cost[task][resource];
                    if (newCost < dp[next]) {
                        dp[next] = newCost;
                        parent[next] = mask;
                        chosen[next] = resource;
                    }
                }
            }
        }

        int mask = total - 1;
        int[] assignment = new int[n];
        for (int task = n - 1; task >= 0; task--) {
            assignment[task] = chosen[mask];
            mask = parent[mask];
        }
        return new AssignmentResult(dp[total - 1], assignment);
    }

    /*
     * MATRIX CHAIN MULTIPLICATION
     * Agriculture interpretation:
     * Imagine analytics matrices are multiplied in sequence. The order of
     * multiplication changes the number of scalar operations.
     *
     * Logic:
     * dp[i][j] = minimum scalar multiplications needed for matrices i..j.
     * Try every split k between i and j and choose the cheapest split.
     *
     * Complexity: O(n^3) time and O(n^2) space.
     */
    public static MatrixChainResult matrixChain(int[] dims) {
        int n = dims.length - 1;
        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];

        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j]
                            + (long) dims[i] * dims[k + 1] * dims[j + 1];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }
        return new MatrixChainResult(dp[0][n - 1], split);
    }

    public static class AlignmentResult {
        public final int score;
        public final String alignedA;
        public final String alignedB;

        public AlignmentResult(int score, String alignedA, String alignedB) {
            this.score = score;
            this.alignedA = alignedA;
            this.alignedB = alignedB;
        }
    }

    public static class AssignmentResult {
        public final int minCost;
        public final int[] assignment;

        public AssignmentResult(int minCost, int[] assignment) {
            this.minCost = minCost;
            this.assignment = assignment;
        }
    }

    public static class MatrixChainResult {
        public final long minMultiplications;
        public final int[][] split;

        public MatrixChainResult(long minMultiplications, int[][] split) {
            this.minMultiplications = minMultiplications;
            this.split = split;
        }
    }
}
