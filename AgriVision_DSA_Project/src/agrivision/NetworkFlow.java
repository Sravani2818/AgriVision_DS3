package agrivision;

import java.util.*;

public class NetworkFlow {

    private static final Scanner FALLBACK_SCANNER = new Scanner(System.in);

    private static class FlowResult {
        int maxFlow;
        int[][] residual;
        long elapsedNanos;

        FlowResult(int maxFlow, int[][] residual, long elapsedNanos) {
            this.maxFlow = maxFlow;
            this.residual = residual;
            this.elapsedNanos = elapsedNanos;
        }
    }

    public static void run(Scanner sc) {
        System.out.println("\n=== CO4: NETWORK FLOW — AGRICULTURAL WATER DISTRIBUTION ===");
        System.out.println("Model each pipeline/irrigation channel as a directed edge.");
        System.out.println("Capacity is the maximum water units that can pass through that channel.");
        int n = readInt(sc, "Number of nodes (farms, reservoirs, junctions; >= 2): ", 2, 200);
        int m = readInt(sc, "Number of directed pipelines: ", 1, 10000);

        int[][] capacity = new int[n][n];
        System.out.println("\nUse node IDs 0 through " + (n - 1) + ".");
        for (int i = 0; i < m; i++) {
            System.out.printf("Pipeline %d%n", i + 1);
            int u = readInt(sc, "  From node: ", 0, n - 1);
            int v = readInt(sc, "  To node: ", 0, n - 1);
            while (u == v) {
                System.out.println("  A pipeline must connect two different nodes.");
                u = readInt(sc, "  From node: ", 0, n - 1);
                v = readInt(sc, "  To node: ", 0, n - 1);
            }
            int cap = readInt(sc, "  Capacity (water units): ", 0, Integer.MAX_VALUE);
            capacity[u][v] = safeAdd(capacity[u][v], cap);
        }

        int source = readInt(sc, "Source node (reservoir): ", 0, n - 1);
        int sink = readInt(sc, "Sink node (target farm): ", 0, n - 1);
        while (source == sink) {
            System.out.println("Source and sink must be different.");
            source = readInt(sc, "Source node (reservoir): ", 0, n - 1);
            sink = readInt(sc, "Sink node (target farm): ", 0, n - 1);
        }

        System.out.println("\n--- Results ---");
        FlowResult ff = fordFulkersonResult(capacity, source, sink);
        FlowResult ek = edmondsKarpResult(capacity, source, sink);
        FlowResult dinic = dinicResult(capacity, source, sink);

        printResult("Ford-Fulkerson (DFS augmenting paths)", ff);
        printResult("Edmonds-Karp (BFS augmenting paths)", ek);
        printResult("Dinic (level graph + blocking flow)", dinic);

        boolean[] side = reachableInResidual(ek.residual, source);
        System.out.print("Minimum-cut source-side nodes: ");
        boolean first = true;
        for (int i = 0; i < side.length; i++) {
            if (side[i]) {
                if (!first) System.out.print(", ");
                System.out.print(i);
                first = false;
            }
        }
        if (first) System.out.print("(none)");
        System.out.println();

        int cutCapacity = 0;
        for (int u = 0; u < n; u++) {
            if (side[u]) {
                for (int v = 0; v < n; v++) {
                    if (!side[v]) cutCapacity = safeAdd(cutCapacity, capacity[u][v]);
                }
            }
        }
        System.out.println("Minimum-cut capacity: " + cutCapacity + " water units");
        System.out.println("Interpretation: this is the maximum possible water flow from the selected reservoir to the selected farm.");
        System.out.println("The values above come from the nodes, pipelines and capacities you entered.");

        System.out.println("\n--- Optional: Farm-to-resource matching ---");
        System.out.print("Run matching demo? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            int farms = readInt(sc, "Number of farms: ", 1, 200);
            int resources = readInt(sc, "Number of available resources: ", 1, 200);
            boolean[][] edges = new boolean[farms][resources];
            System.out.println("For each farm, enter the number of resources it can use, then their 0-based IDs.");
            for (int f = 0; f < farms; f++) {
                int count = readInt(sc, "Farm " + f + " compatible-resource count: ", 0, resources);
                for (int j = 0; j < count; j++) {
                    int r = readInt(sc, "  Resource ID: ", 0, resources - 1);
                    edges[f][r] = true;
                }
            }
            long start = System.nanoTime();
            int matched = bipartiteMatching(edges);
            long elapsed = System.nanoTime() - start;
            System.out.println("Maximum farm-resource matching: " + matched);
            System.out.printf("Matching execution time: %.4f ms%n", elapsed / 1_000_000.0);
        }
    }

    private static void printResult(String label, FlowResult r) {
        System.out.printf("%-43s flow = %-8d time = %.4f ms%n",
                label, r.maxFlow, r.elapsedNanos / 1_000_000.0);
    }

    public static int fordFulkerson(int[][] capacity, int source, int sink) {
        return fordFulkersonResult(capacity, source, sink).maxFlow;
    }

    private static FlowResult fordFulkersonResult(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[][] residual = copy(capacity);
        int flow = 0;
        int[] parent = new int[n];
        long start = System.nanoTime();

        while (dfsPath(residual, source, sink, parent, new boolean[n])) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                pathFlow = Math.min(pathFlow, residual[parent[v]][v]);
            }
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] = safeAdd(residual[v][u], pathFlow);
            }
            flow = safeAdd(flow, pathFlow);
        }
        return new FlowResult(flow, residual, System.nanoTime() - start);
    }

    public static int edmondsKarp(int[][] capacity, int source, int sink) {
        return edmondsKarpResult(capacity, source, sink).maxFlow;
    }

    private static FlowResult edmondsKarpResult(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[][] residual = copy(capacity);
        int flow = 0;
        int[] parent = new int[n];
        long start = System.nanoTime();

        while (bfsPath(residual, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                pathFlow = Math.min(pathFlow, residual[parent[v]][v]);
            }
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] = safeAdd(residual[v][u], pathFlow);
            }
            flow = safeAdd(flow, pathFlow);
        }
        return new FlowResult(flow, residual, System.nanoTime() - start);
    }

    public static int dinic(int[][] capacity, int source, int sink) {
        return dinicResult(capacity, source, sink).maxFlow;
    }

    private static FlowResult dinicResult(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[][] residual = copy(capacity);
        int flow = 0;
        int[] level = new int[n];
        long start = System.nanoTime();

        while (buildLevels(residual, source, sink, level)) {
            int[] next = new int[n];
            int pushed;
            while ((pushed = sendFlow(residual, level, next, source, sink, Integer.MAX_VALUE)) > 0) {
                flow = safeAdd(flow, pushed);
            }
        }
        return new FlowResult(flow, residual, System.nanoTime() - start);
    }

    private static boolean dfsPath(int[][] residual, int u, int sink, int[] parent, boolean[] seen) {
        if (u == sink) return true;
        seen[u] = true;
        for (int v = 0; v < residual.length; v++) {
            if (!seen[v] && residual[u][v] > 0) {
                parent[v] = u;
                if (dfsPath(residual, v, sink, parent, seen)) return true;
            }
        }
        return false;
    }

    private static boolean bfsPath(int[][] residual, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        boolean[] seen = new boolean[residual.length];
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);
        seen[source] = true;

        while (!q.isEmpty()) {
            int u = q.remove();
            for (int v = 0; v < residual.length; v++) {
                if (!seen[v] && residual[u][v] > 0) {
                    seen[v] = true;
                    parent[v] = u;
                    if (v == sink) return true;
                    q.add(v);
                }
            }
        }
        return false;
    }

    private static boolean buildLevels(int[][] residual, int source, int sink, int[] level) {
        Arrays.fill(level, -1);
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);
        level[source] = 0;

        while (!q.isEmpty()) {
            int u = q.remove();
            for (int v = 0; v < residual.length; v++) {
                if (residual[u][v] > 0 && level[v] < 0) {
                    level[v] = level[u] + 1;
                    q.add(v);
                }
            }
        }
        return level[sink] >= 0;
    }

    private static int sendFlow(int[][] residual, int[] level, int[] next,
                                int u, int sink, int pushed) {
        if (u == sink) return pushed;
        for (; next[u] < residual.length; next[u]++) {
            int v = next[u];
            if (residual[u][v] > 0 && level[v] == level[u] + 1) {
                int amount = sendFlow(residual, level, next, v, sink,
                        Math.min(pushed, residual[u][v]));
                if (amount > 0) {
                    residual[u][v] -= amount;
                    residual[v][u] = safeAdd(residual[v][u], amount);
                    return amount;
                }
            }
        }
        return 0;
    }

    private static boolean[] reachableInResidual(int[][] residual, int source) {
        boolean[] seen = new boolean[residual.length];
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);
        seen[source] = true;
        while (!q.isEmpty()) {
            int u = q.remove();
            for (int v = 0; v < residual.length; v++) {
                if (!seen[v] && residual[u][v] > 0) {
                    seen[v] = true;
                    q.add(v);
                }
            }
        }
        return seen;
    }

    public static int bipartiteMatching(boolean[][] edges) {
        int farms = edges.length;
        int resources = farms == 0 ? 0 : edges[0].length;
        int[] resourceOwner = new int[resources];
        Arrays.fill(resourceOwner, -1);
        int matching = 0;
        for (int farm = 0; farm < farms; farm++) {
            if (matchFarm(farm, edges, resourceOwner, new boolean[resources])) matching++;
        }
        return matching;
    }

    private static boolean matchFarm(int farm, boolean[][] edges, int[] owner, boolean[] seen) {
        for (int r = 0; r < owner.length; r++) {
            if (edges[farm][r] && !seen[r]) {
                seen[r] = true;
                if (owner[r] == -1 || matchFarm(owner[r], edges, owner, seen)) {
                    owner[r] = farm;
                    return true;
                }
            }
        }
        return false;
    }

    private static int[][] copy(int[][] matrix) {
        int[][] out = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) out[i] = matrix[i].clone();
        return out;
    }

    private static int safeAdd(int a, int b) {
        long value = (long) a + b;
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
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
