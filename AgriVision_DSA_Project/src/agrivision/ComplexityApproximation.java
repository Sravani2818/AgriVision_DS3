package agrivision;

import java.util.*;

public class ComplexityApproximation {

    public static void run(Scanner sc) {
        System.out.println("\n=== CO5: NP-COMPLETENESS & APPROXIMATION ===");
        System.out.println("Use this module to analyze agricultural infrastructure and resource costs.");

        System.out.println("\n--- Part A: Vertex Cover approximation ---");
        int vertices = readInt(sc, "Number of infrastructure nodes (>= 1): ", 1, 500);
        int edgeCount = readInt(sc, "Number of connections/roads/pipelines: ", 0, 10000);
        int[][] edges = new int[edgeCount][2];

        for (int i = 0; i < edgeCount; i++) {
            System.out.printf("Connection %d%n", i + 1);
            int u = readInt(sc, "  First node ID (0-" + (vertices - 1) + "): ", 0, vertices - 1);
            int v = readInt(sc, "  Second node ID (0-" + (vertices - 1) + "): ", 0, vertices - 1);
            while (u == v) {
                System.out.println("  Self-loops are not accepted.");
                u = readInt(sc, "  First node ID: ", 0, vertices - 1);
                v = readInt(sc, "  Second node ID: ", 0, vertices - 1);
            }
            edges[i][0] = u;
            edges[i][1] = v;
        }

        long start = System.nanoTime();
        List<Integer> cover = vertexCover2Approx(vertices, edges);
        long elapsed = System.nanoTime() - start;
        System.out.println("\nApproximate Vertex Cover: " + cover);
        System.out.println("Selected node count: " + cover.size());
        System.out.printf("Vertex Cover execution time: %.4f ms%n", elapsed / 1_000_000.0);
        System.out.println("Meaning: every entered connection has at least one endpoint in this selected set.");
        System.out.println("Note: this standard 2-approximation is a valid cover, but may not be the smallest cover.");

        System.out.println("\n--- Part B: Subset Sum for farm input costs ---");
        int count = readInt(sc, "Number of cost values: ", 0, 10000);
        int[] values = new int[count];
        for (int i = 0; i < count; i++) {
            values[i] = readInt(sc, "Cost value " + (i + 1) + " (non-negative): ", 0, 1_000_000);
        }
        int target = readInt(sc, "Target budget/total: ", 0, 10_000_000);

        long subsetStart = System.nanoTime();
        boolean possible = subsetSum(values, target);
        long subsetElapsed = System.nanoTime() - subsetStart;
        System.out.println("Can selected costs total " + target + "? " + possible);
        System.out.printf("Subset Sum execution time: %.4f ms%n", subsetElapsed / 1_000_000.0);
        if (possible) {
            System.out.println("A subset of the entered values can reach the target.");
        } else {
            System.out.println("No subset of the entered values reaches the target.");
        }

        System.out.println("\n--- Part C: User-entered 3-SAT clauses (reduction illustration) ---");
        int clauses = readInt(sc, "Number of 3-literal clauses to describe: ", 1, 100);
        List<String[]> formula = new ArrayList<>();
        for (int i = 0; i < clauses; i++) {
            System.out.println("Clause " + (i + 1) + " (enter three literal labels, e.g. rain, NOT-drought, soil):");
            String[] literals = new String[3];
            for (int j = 0; j < 3; j++) {
                System.out.print("  Literal " + (j + 1) + ": ");
                literals[j] = sc.nextLine().trim();
            }
            formula.add(literals);
        }

        long reductionStart = System.nanoTime();
        System.out.println("\nEntered formula:");
        for (int i = 0; i < formula.size(); i++) {
            System.out.printf("Clause %d: (%s OR %s OR %s)%n",
                    i + 1, formula.get(i)[0], formula.get(i)[1], formula.get(i)[2]);
        }
        System.out.println("\nReduction concept: create one graph vertex for each literal occurrence.");
        System.out.println("Connect vertices from different clauses only when their literals are compatible.");
        System.out.println("A clique containing one compatible literal per clause corresponds to a satisfying choice.");
        System.out.println("This demonstrates the 3-SAT-to-CLIQUE reduction; it does not solve the formula.");
        long reductionElapsed = System.nanoTime() - reductionStart;
        System.out.printf("Reduction-demonstration time: %.4f ms%n", reductionElapsed / 1_000_000.0);

        System.out.println("\n--- Complexity terms for viva ---");
        System.out.println("P: problems solvable in polynomial time.");
        System.out.println("NP: proposed solutions verifiable in polynomial time.");
        System.out.println("NP-hard: at least as hard as every problem in NP.");
        System.out.println("NP-complete: both NP-hard and in NP.");
        System.out.println("FPT: fixed-parameter tractable algorithms.");
        System.out.println("PTAS/FPTAS: approximation schemes.");
    }

    public static List<Integer> vertexCover2Approx(int vertices, int[][] edges) {
        boolean[] selected = new boolean[vertices];
        boolean[] coveredEdge = new boolean[edges.length];

        for (int i = 0; i < edges.length; i++) {
            if (coveredEdge[i]) continue;
            int u = edges[i][0], v = edges[i][1];
            selected[u] = true;
            selected[v] = true;
            for (int j = 0; j < edges.length; j++) {
                if (edges[j][0] == u || edges[j][1] == u
                        || edges[j][0] == v || edges[j][1] == v) {
                    coveredEdge[j] = true;
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < vertices; i++) if (selected[i]) result.add(i);
        return result;
    }

    public static boolean subsetSum(int[] values, int target) {
        if (target < 0) return false;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int value : values) {
            if (value < 0) throw new IllegalArgumentException("Subset Sum values must be non-negative.");
            for (int sum = target; sum >= value; sum--) {
                if (dp[sum - value]) dp[sum] = true;
            }
        }
        return dp[target];
    }

    private static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Please enter an integer between " + min + " and " + max + ".");
        }
    }
}
