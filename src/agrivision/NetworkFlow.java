package agrivision;

import java.util.*;

/** CO4: Network-flow algorithms for irrigation and resource allocation examples. */
public class NetworkFlow {
    private NetworkFlow() {}

    // Edmonds-Karp: BFS finds the shortest augmenting path in the residual graph.
    public static int edmondsKarp(int[][] capacity, int source, int sink) {
        int n = capacity.length, maxFlow = 0;
        int[][] residual = copy(capacity);
        int[] parent = new int[n];
        while (bfs(residual, source, sink, parent)) {
            int flow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) flow = Math.min(flow, residual[parent[v]][v]);
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v]; residual[u][v] -= flow; residual[v][u] += flow;
            }
            maxFlow += flow;
        }
        return maxFlow;
    }

    // Ford-Fulkerson with DFS: useful reference version; integer capacities ensure integer augmentations.
    public static int fordFulkerson(int[][] capacity, int source, int sink) {
        int n = capacity.length, total = 0;
        int[][] residual = copy(capacity);
        while (true) {
            boolean[] seen = new boolean[n];
            int pushed = dfs(residual, source, sink, Integer.MAX_VALUE, seen);
            if (pushed == 0) return total;
            total += pushed;
        }
    }

    private static int dfs(int[][] r, int u, int sink, int available, boolean[] seen) {
        if (u == sink) return available;
        seen[u] = true;
        for (int v = 0; v < r.length; v++) if (!seen[v] && r[u][v] > 0) {
            int pushed = dfs(r, v, sink, Math.min(available, r[u][v]), seen);
            if (pushed > 0) { r[u][v] -= pushed; r[v][u] += pushed; return pushed; }
        }
        return 0;
    }

    // Dinic: BFS creates levels; DFS sends flow only along level-increasing edges.
    public static int dinic(int[][] capacity, int source, int sink) {
        int n = capacity.length, total = 0;
        int[][] r = copy(capacity); int[] level = new int[n], next = new int[n];
        while (buildLevels(r, source, sink, level)) {
            Arrays.fill(next, 0); int pushed;
            while ((pushed = send(r, source, sink, Integer.MAX_VALUE, level, next)) > 0) total += pushed;
        }
        return total;
    }
    private static boolean buildLevels(int[][] r, int s, int t, int[] level) {
        Arrays.fill(level, -1); level[s] = 0; Queue<Integer> q = new ArrayDeque<>(); q.add(s);
        while (!q.isEmpty()) { int u = q.remove(); for (int v=0;v<r.length;v++) if (r[u][v]>0 && level[v]<0) {level[v]=level[u]+1;q.add(v);} }
        return level[t] >= 0;
    }
    private static int send(int[][] r,int u,int t,int f,int[] level,int[] next) {
        if(u==t) return f;
        for(;next[u]<r.length;next[u]++){int v=next[u]; if(r[u][v]>0 && level[v]==level[u]+1){int x=send(r,v,t,Math.min(f,r[u][v]),level,next);if(x>0){r[u][v]-=x;r[v][u]+=x;return x;}}}
        return 0;
    }
    // Residual reachability after max flow identifies the source side of a minimum cut.
    public static boolean[] minCutSourceSide(int[][] capacity, int source, int sink) {
        int[][] r=copy(capacity); int[] parent=new int[r.length];
        while(bfs(r,source,sink,parent)){int f=Integer.MAX_VALUE;for(int v=sink;v!=source;v=parent[v])f=Math.min(f,r[parent[v]][v]);for(int v=sink;v!=source;v=parent[v]){int u=parent[v];r[u][v]-=f;r[v][u]+=f;}}
        boolean[] seen=new boolean[r.length]; Queue<Integer> q=new ArrayDeque<>();q.add(source);seen[source]=true;
        while(!q.isEmpty()){int u=q.remove();for(int v=0;v<r.length;v++)if(r[u][v]>0&&!seen[v]){seen[v]=true;q.add(v);}} return seen;
    }
    // Bipartite matching: each left node can match at most one right node.
    public static int bipartiteMatching(boolean[][] edges) {
        int left=edges.length, right=left==0?0:edges[0].length; int[] matchRight=new int[right];Arrays.fill(matchRight,-1);int count=0;
        for(int u=0;u<left;u++)if(augment(u,edges,matchRight,new boolean[right]))count++; return count;
    }
    private static boolean augment(int u,boolean[][] e,int[] mr,boolean[] seen){for(int v=0;v<mr.length;v++)if(e[u][v]&&!seen[v]){seen[v]=true;if(mr[v]<0||augment(mr[v],e,mr,seen)){mr[v]=u;return true;}}return false;}
    private static boolean bfs(int[][] r,int s,int t,int[] p){Arrays.fill(p,-1);p[s]=s;Queue<Integer>q=new ArrayDeque<>();q.add(s);while(!q.isEmpty()){int u=q.remove();for(int v=0;v<r.length;v++)if(p[v]<0&&r[u][v]>0){p[v]=u;q.add(v);}}return p[t]>=0;}
    private static int[][] copy(int[][] a){int[][] b=new int[a.length][];for(int i=0;i<a.length;i++)b[i]=a[i].clone();return b;}
}
