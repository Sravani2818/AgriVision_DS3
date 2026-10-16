package agrivision;

import java.util.*;

/** CO5: small, runnable demonstrations of NP-hard-style modelling and approximation. */
public class ComplexityApproximation {
    private ComplexityApproximation() {}

    // Vertex-cover 2-approximation: repeatedly choose an uncovered edge and both endpoints.
    public static Set<Integer> vertexCover2Approx(int vertices, int[][] edges) {
        Set<Integer> cover=new LinkedHashSet<>(); boolean[] used=new boolean[edges.length];
        for(int i=0;i<edges.length;i++) if(!used[i]) {int u=edges[i][0],v=edges[i][1];cover.add(u);cover.add(v);
            for(int j=i;j<edges.length;j++) if(edges[j][0]==u||edges[j][1]==u||edges[j][0]==v||edges[j][1]==v)used[j]=true;}
        return cover;
    }
    // Subset-sum dynamic programming answers whether some subset totals the target.
    public static boolean subsetSum(int[] values,int target){if(target<0)return false;boolean[] dp=new boolean[target+1];dp[0]=true;for(int x:values)for(int s=target;s>=x;s--)dp[s]|=dp[s-x];return dp[target];}

    // Reduction demo: each 3-SAT clause becomes a layer of literal vertices;
    // connect compatible literals in different clauses. A k-clique corresponds to choosing
    // one mutually non-contradictory literal from each of k clauses.
    public static List<String> threeSatToCliqueDemo() {
        return List.of(
          "Clause 1: (rain OR NOT-drought OR soil)",
          "Clause 2: (NOT-rain OR water OR crop)",
          "Create one graph vertex per literal occurrence.",
          "Connect vertices from different clauses unless their literals contradict.",
          "A clique of size = number of clauses represents a consistent choice of one literal per clause.",
          "This illustrates the reduction; it is not a full SAT solver.");
    }
}
