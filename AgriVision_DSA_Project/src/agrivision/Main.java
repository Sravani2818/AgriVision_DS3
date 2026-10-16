package agrivision;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static String corpus;
    private static List<CorpusRecord> records;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            corpus = DataLoader.loadCombinedCorpus();
            records = DataLoader.loadRecords();
        } catch (IOException e) {
            System.err.println("DATA LOADING ERROR: " + e.getMessage());
            System.err.println("Run the program from the AgriVision_DSA_Project folder.");
            return;
        }

        while (true) {
            DisplayUtils.title("AGRIVISION - SMART AGRICULTURE ANALYTICS PLATFORM");
            System.out.println("Loaded Records : " + records.size());
            System.out.println("Corpus Length  : " + corpus.length() + " characters");
            System.out.println();

            System.out.println("1. View Agricultural Data");
            System.out.println("2. CO1 - Problem Understanding & Data Management");
            System.out.println("3. CO2 - String Algorithms");
            System.out.println("4. CO2 - Suffix Structures");
            System.out.println("5. CO3 - Advanced Dynamic Programming");
            System.out.println("6. Complexity");
            System.out.println("7. CO4 - Network Flow");
            System.out.println("8. CO5 - NP-Completeness & Approximation");
            System.out.println("9. CO6 - Randomised & Parallel Algorithms");
            System.out.println("10. Exit");
            System.out.print("\nEnter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> dataMenu(sc);
                case "2" -> co1Menu(sc);
                case "3" -> stringMenu(sc);
                case "4" -> suffixMenu(sc);
                case "5" -> dpMenu(sc);
                case "6" -> guideMenu(sc);
                case "7" -> NetworkFlow.run(sc);
                case "8" -> ComplexityApproximation.run(sc);
                case "9" -> RandomParallelAlgorithms.run(sc);
                case "10" -> {
                    System.out.println("\nThank you for using AgriVision.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter 1-10.");
            }
        }
    }

    private static void dataMenu(Scanner sc) {
        while (true) {
            DisplayUtils.title("1. AGRICULTURAL DATA");
            System.out.println("1. Dataset Overview");
            System.out.println("2. View Records");
            System.out.println("3. Search Records by Crop / State / Query Type");
            System.out.println("4. Back");
            System.out.print("\nChoice: ");

            String c = sc.nextLine().trim();

            if (c.equals("1")) {
                DisplayUtils.title("DATASET OVERVIEW");
                System.out.println("Corpus source : KCC-derived agricultural text corpus");
                System.out.println("Records       : " + records.size());
                System.out.println("Text length   : " + corpus.length() + " characters");
                System.out.println("File          : data/AgriVision_Corpus/all_agriculture_text.txt");
                System.out.println("Record files  : data/AgriVision_Corpus/records/*.txt");

                Map<String, Long> sectors = records.stream()
                        .collect(Collectors.groupingBy(r -> r.sector, TreeMap::new, Collectors.counting()));
                System.out.println("\nSector distribution:");
                sectors.forEach((k, v) -> System.out.printf("  %-25s %d%n", k, v));

                Map<String, Long> queryTypes = records.stream()
                        .collect(Collectors.groupingBy(r -> r.queryType, TreeMap::new, Collectors.counting()));
                System.out.println("\nQuery-type distribution:");
                queryTypes.forEach((k, v) -> System.out.printf("  %-30s %d%n", k, v));

                DisplayUtils.pause(sc);
            } else if (c.equals("2")) {
                viewRecords(sc);
            } else if (c.equals("3")) {
                System.out.print("Enter crop/state/query type keyword: ");
                String key = sc.nextLine().trim().toLowerCase(Locale.ROOT);

                List<CorpusRecord> found = records.stream()
                        .filter(r -> (r.crop + " " + r.state + " " + r.queryType + " "
                                + r.district + " " + r.sector).toLowerCase(Locale.ROOT).contains(key))
                        .limit(30)
                        .collect(Collectors.toList());

                System.out.println("\nMatching records: " + found.size());
                for (CorpusRecord r : found) {
                    System.out.printf("#%04d | %-20s | %-20s | %-28s%n",
                            r.recordNo, trim(r.crop, 20), trim(r.state, 20), trim(r.queryType, 28));
                }
                DisplayUtils.pause(sc);
            } else if (c.equals("4")) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewRecords(Scanner sc) {
        int page = 0;
        final int pageSize = 5;

        while (true) {
            int from = page * pageSize;
            if (from >= records.size()) {
                page = 0;
                from = 0;
            }

            int to = Math.min(from + pageSize, records.size());
            DisplayUtils.title("AGRICULTURAL RECORDS " + (from + 1) + " - " + to);

            for (int i = from; i < to; i++) {
                CorpusRecord r = records.get(i);
                System.out.printf("""
                        Record #%04d

                        Source ID : %s

                        Crop      : %s

                        State     : %s

                        District  : %s

                        Sector    : %s

                        Query Type: %s

                        Season    : %s

                        Farmer    : %s

                        Advisory  : %s

                        """,
                        r.recordNo, blank(r.sourceId), blank(r.crop), blank(r.state),
                        blank(r.district), blank(r.sector), blank(r.queryType),
                        blank(r.season), trim(r.question, 180), trim(r.answer, 180));
            }

            System.out.println("[N] Next page  [P] Previous page  [B] Back");
            String c = sc.nextLine().trim().toUpperCase(Locale.ROOT);

            if (c.equals("N")) {
                page++;
            } else if (c.equals("P")) {
                page = Math.max(0, page - 1);
            } else if (c.equals("B")) {
                return;
            }
        }
    }

    private static void co1Menu(Scanner sc) {
        DisplayUtils.title("CO1 - PROBLEM UNDERSTANDING & REQUIREMENT MAPPING");
        System.out.println("""
                Problem:
                AgriVision manages agricultural text containing crop information,
                farmer queries, disease/plant-protection information and advisories.

                Input:
                UTF-8 .txt agricultural corpus generated from the uploaded KCC records.

                Processing:
                1. Load text records.
                2. Organize metadata.
                3. Retrieve agricultural records.
                4. Prepare text for DSA-based searching and analysis.

                DSA relevance:
                - Text representation       -> string processing
                - Document retrieval         -> indexing/search
                - Disease keyword detection  -> multi-pattern matching
                - Text comparison            -> dynamic programming

                Review statement:
                "AgriVision converts real agricultural advisory records into a searchable
                text corpus so that DSA algorithms can solve retrieval, indexing and
                text-comparison problems on agricultural data."
                """);
        DisplayUtils.pause(sc);
    }

    private static void stringMenu(Scanner sc) {
        while (true) {
            DisplayUtils.title("CO2 - STRING ALGORITHMS");
            System.out.println("1. Naive Pattern Search");
            System.out.println("2. KMP");
            System.out.println("3. Z Algorithm");
            System.out.println("4. Rabin-Karp");
            System.out.println("5. Aho-Corasick");
            System.out.println("6. Trie with Exact Word Positions");
            System.out.println("7. Compare Naive / KMP / Z / Rabin-Karp");
            System.out.println("8. Back");
            System.out.print("\nChoice: ");

            String c = sc.nextLine().trim();

            switch (c) {
                case "1" -> singleSearch(sc, "Naive", StringAlgorithms::naiveSearch);
                case "2" -> singleSearch(sc, "KMP", StringAlgorithms::kmpSearch);
                case "3" -> singleSearch(sc, "Z Algorithm", StringAlgorithms::zSearch);
                case "4" -> singleSearch(sc, "Rabin-Karp", StringAlgorithms::rabinKarp);
                case "5" -> ahoMenu(sc);
                case "6" -> trieMenu(sc);
                case "7" -> compareSearch(sc);
                case "8" -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void singleSearch(Scanner sc, String name,
            java.util.function.BiFunction<String, String, List<Integer>> algorithm) {
        System.out.print("Enter pattern to search: ");
        String pattern = sc.nextLine();

        if (pattern.isEmpty()) {
            System.out.println("Pattern cannot be empty.");
            return;
        }

        SearchResult result = StringAlgorithms.timed(name, corpus, pattern, algorithm);
        DisplayUtils.title(name.toUpperCase(Locale.ROOT) + " SEARCH");
        System.out.println("Corpus          : all_agriculture_text.txt");
        System.out.println("Pattern         : " + pattern);
        System.out.printf("Execution Time  : %.4f ms%n", result.timeMillis());
        DisplayUtils.printPositions(corpus, result.positions);

        if (!result.positions.isEmpty()) {
            System.out.println("\nSample context:");
            int count = Math.min(3, result.positions.size());
            for (int i = 0; i < count; i++) {
                int p = result.positions.get(i);
                System.out.println("Position " + p + " -> " + DisplayUtils.snippet(corpus, p, 70));
            }
        }
        DisplayUtils.pause(sc);
    }

    private static void ahoMenu(Scanner sc) {
        System.out.print("Enter disease/agriculture keywords separated by commas: ");
        String[] raw = sc.nextLine().split(",");

        List<String> patterns = Arrays.stream(raw)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (patterns.isEmpty()) {
            return;
        }

        long start = System.nanoTime();
        Map<String, List<Integer>> result = StringAlgorithms.ahoCorasick(corpus, patterns);
        long end = System.nanoTime();

        DisplayUtils.title("AHO-CORASICK MULTI-PATTERN SEARCH");
        System.out.printf("Execution Time: %.4f ms%n", (end - start) / 1_000_000.0);

        for (String p : patterns) {
            List<Integer> pos = result.get(p);
            System.out.println("\nPattern: " + p);
            DisplayUtils.printPositions(corpus, pos);
        }
        DisplayUtils.pause(sc);
    }

    private static void trieMenu(Scanner sc) {
        System.out.print("Enter exact word to search: ");
        String word = sc.nextLine().trim();

        if (word.isEmpty()) {
            return;
        }

        long start = System.nanoTime();
        StringAlgorithms.PositionTrie trie = StringAlgorithms.PositionTrie.build(corpus);
        List<Integer> pos = trie.search(word);
        long end = System.nanoTime();

        DisplayUtils.title("TRIE EXACT-WORD SEARCH");
        System.out.println("Word            : " + word);
        System.out.printf("Build + Search  : %.4f ms%n", (end - start) / 1_000_000.0);
        DisplayUtils.printPositions(corpus, pos);
        DisplayUtils.pause(sc);
    }

    private static void compareSearch(Scanner sc) {
        System.out.print("Enter pattern: ");
        String p = sc.nextLine();

        if (p.isEmpty()) {
            return;
        }

        SearchResult a = StringAlgorithms.timed("Naive", corpus, p, StringAlgorithms::naiveSearch);
        SearchResult b = StringAlgorithms.timed("KMP", corpus, p, StringAlgorithms::kmpSearch);
        SearchResult z = StringAlgorithms.timed("Z", corpus, p, StringAlgorithms::zSearch);
        SearchResult r = StringAlgorithms.timed("Rabin-Karp", corpus, p, StringAlgorithms::rabinKarp);

        DisplayUtils.title("STRING ALGORITHM COMPARISON");
        System.out.printf("%-18s %-12s %-18s%n", "Algorithm", "Matches", "Time (ms)");
        System.out.println("-".repeat(52));

        for (SearchResult x : List.of(a, b, z, r)) {
            System.out.printf("%-18s %-12d %-18.4f%n",
                    x.algorithm, x.positions.size(), x.timeMillis());
        }

        System.out.println("\nAll four algorithms must report the same occurrence count.");
        DisplayUtils.pause(sc);
    }

    private static void suffixMenu(Scanner sc) {
        DisplayUtils.title("CO2 - SUFFIX ARRAY / LCP (KASAI)");
        System.out.println("1. Build suffix array for a selected record");
        System.out.println("2. Search pattern using suffix array");
        System.out.println("3. Show LCP values for a selected record");
        System.out.println("4. Back");
        System.out.print("\nChoice: ");

        String c = sc.nextLine().trim();
        if (c.equals("4")) {
            return;
        }
        if (!c.equals("1") && !c.equals("2") && !c.equals("3")) {
            System.out.println("Invalid choice.");
            return;
        }

        CorpusRecord r = chooseRecord(sc);
        if (r == null) {
            return;
        }

        String text = r.question + "\n" + r.answer;
        if (text.length() > 20000) {
            text = text.substring(0, 20000);
        }

        System.out.println("\nBuilding suffix array for record #" + r.recordNo + "...");
        long start = System.nanoTime();
        int[] sa = SuffixStructures.buildSuffixArray(text);
        long buildTime = System.nanoTime() - start;

        if (c.equals("1")) {
            DisplayUtils.title("SUFFIX ARRAY");
            System.out.println("Record : #" + r.recordNo + " | Crop: " + r.crop);
            System.out.println("Text length: " + text.length());
            System.out.printf("Build time: %.4f ms%n", buildTime / 1_000_000.0);
            System.out.println("\nFirst 20 suffix positions:");

            for (int i = 0; i < Math.min(20, sa.length); i++) {
                System.out.printf("%2d -> %d : %s%n", i, sa[i],
                        text.substring(sa[i], Math.min(text.length(), sa[i] + 60))
                                .replace("\n", " "));
            }
        } else if (c.equals("2")) {
            System.out.print("Pattern: ");
            String p = sc.nextLine();

            long s = System.nanoTime();
            List<Integer> positions = SuffixStructures.search(text, p, sa);
            long t = System.nanoTime() - s;

            DisplayUtils.title("SUFFIX ARRAY PATTERN SEARCH");
            System.out.println("Pattern: " + p);
            System.out.printf("Search time: %.4f ms%n", t / 1_000_000.0);
            DisplayUtils.printPositions(text, positions);
        } else if (c.equals("3")) {
            int[] lcp = SuffixStructures.kasaiLcp(text, sa);
            DisplayUtils.title("LCP / KASAI");
            System.out.println("First 20 LCP entries:");

            for (int i = 0; i < Math.min(20, lcp.length); i++) {
                System.out.printf("Rank %2d | Suffix position %4d | LCP = %d%n",
                        i, sa[i], lcp[i]);
            }
        }
        DisplayUtils.pause(sc);
    }

    private static CorpusRecord chooseRecord(Scanner sc) {
        System.out.print("Enter record number (1-" + records.size() + "): ");

        try {
            int n = Integer.parseInt(sc.nextLine().trim());
            if (n < 1 || n > records.size()) {
                System.out.println("Invalid record number.");
                return null;
            }
            return records.get(n - 1);
        } catch (NumberFormatException e) {
            System.out.println("Enter a numeric record number.");
            return null;
        }
    }

    private static void dpMenu(Scanner sc) {
        while (true) {
            DisplayUtils.title("CO3 - ADVANCED DYNAMIC PROGRAMMING");
            System.out.println("1. Levenshtein / Wagner-Fischer Edit Distance");
            System.out.println("2. Damerau-Levenshtein Distance");
            System.out.println("3. Needleman-Wunsch Global Alignment");
            System.out.println("4. Smith-Waterman Local Alignment");
            System.out.println("5. Bitmask DP - Resource Assignment");
            System.out.println("6. Matrix Chain Multiplication");
            System.out.println("7. Back");
            System.out.print("\nChoice: ");

            String c = sc.nextLine().trim();

            switch (c) {
                case "1" -> editDistance(sc);
                case "2" -> damerau(sc);
                case "3" -> alignment(sc, true);
                case "4" -> alignment(sc, false);
                case "5" -> bitmaskDemo(sc);
                case "6" -> matrixChainDemo(sc);
                case "7" -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static String[] getTwoStrings(Scanner sc) {
        System.out.print("String / word 1: ");
        String a = sc.nextLine();
        System.out.print("String / word 2: ");
        String b = sc.nextLine();
        return new String[]{a, b};
    }

    private static void editDistance(Scanner sc) {
        String[] x = getTwoStrings(sc);
        int d = AdvancedDP.editDistance(x[0], x[1]);

        DisplayUtils.title("LEVENSHTEIN / WAGNER-FISCHER");
        System.out.println("A: " + x[0]);
        System.out.println("B: " + x[1]);
        System.out.println("Minimum edit operations: " + d);
        System.out.println("Operations allowed: insertion, deletion, substitution.");
        DisplayUtils.pause(sc);
    }

    private static void damerau(Scanner sc) {
        String[] x = getTwoStrings(sc);
        int d = AdvancedDP.damerauLevenshtein(x[0], x[1]);

        DisplayUtils.title("DAMERAU-LEVENSHTEIN");
        System.out.println("A: " + x[0]);
        System.out.println("B: " + x[1]);
        System.out.println("Minimum operations: " + d);
        System.out.println("Includes adjacent-character transposition.");
        DisplayUtils.pause(sc);
    }

    private static void alignment(Scanner sc, boolean global) {
        String[] x = getTwoStrings(sc);
        AdvancedDP.AlignmentResult r = global
                ? AdvancedDP.needlemanWunsch(x[0], x[1])
                : AdvancedDP.smithWaterman(x[0], x[1]);

        DisplayUtils.title(global ? "NEEDLEMAN-WUNSCH GLOBAL ALIGNMENT"
                                  : "SMITH-WATERMAN LOCAL ALIGNMENT");
        System.out.println("Score    : " + r.score);
        System.out.println("Sequence A:");
        System.out.println(r.alignedA);
        System.out.println("Sequence B:");
        System.out.println(r.alignedB);
        DisplayUtils.pause(sc);
    }

    private static void bitmaskDemo(Scanner sc) {
        // Four tasks and four resources keep 2^n manageable for a live viva demo.
        int[][] cost = {
                {9, 2, 7, 8},
                {6, 4, 3, 7},
                {5, 8, 1, 8},
                {7, 6, 9, 4}
        };

        AdvancedDP.AssignmentResult r = AdvancedDP.bitmaskAssignment(cost);
        DisplayUtils.title("BITMASK DP - AGRICULTURAL RESOURCE ASSIGNMENT");
        System.out.println("Rows = agricultural tasks");
        System.out.println("Columns = available resources");
        System.out.println("\nCost matrix:");

        for (int[] row : cost) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("\nOptimal assignment:");
        for (int task = 0; task < r.assignment.length; task++) {
            System.out.printf("Task %d -> Resource %d (cost %d)%n",
                    task + 1, r.assignment[task], cost[task][r.assignment[task]]);
        }

        System.out.println("Minimum total cost: " + r.minCost);
        DisplayUtils.pause(sc);
    }

    private static void matrixChainDemo(Scanner sc) {
        // A1(10x30), A2(30x5), A3(5x60), A4(60x10)
        int[] dims = {10, 30, 5, 60, 10};
        AdvancedDP.MatrixChainResult r = AdvancedDP.matrixChain(dims);

        DisplayUtils.title("MATRIX CHAIN MULTIPLICATION");
        System.out.println("Matrices:");

        for (int i = 0; i < dims.length - 1; i++) {
            System.out.printf("A%d = %dx%d%n", i + 1, dims[i], dims[i + 1]);
        }

        System.out.println("\nMinimum scalar multiplications: " + r.minMultiplications);
        System.out.println("DP chooses the best split k for every matrix interval.");
        DisplayUtils.pause(sc);
    }

    private static void guideMenu(Scanner sc) {
        DisplayUtils.title("COMPLEXITY & VIVA GUIDE");
        System.out.printf("""
                NAIVE SEARCH      : O(n*m) time, O(1) extra space
                KMP               : O(n+m) time, O(m) space
                Z ALGORITHM       : O(n+m) time, O(n+m) space
                RABIN-KARP        : Expected O(n+m), worst O(n*m)
                AHO-CORASICK      : O(n + total pattern length + matches)
                TRIE SEARCH       : O(m) after index construction
                SUFFIX ARRAY      : O(n log^2 n) in this implementation
                KASAI LCP         : O(n) after suffix array
                EDIT DISTANCE     : O(n*m) time, O(n*m) space
                NEEDLEMAN-WUNSCH  : O(n*m) time, O(n*m) space
                SMITH-WATERMAN    : O(n*m) time, O(n*m) space
                BITMASK DP        : O(n*2^n) time, O(2^n) space
                MATRIX CHAIN      : O(n^3) time, O(n^2) space

                CO4 - NETWORK FLOW:
                Edmonds-Karp uses BFS to find augmenting paths.
                Dinic uses level graphs and blocking flows.
                Min-cut identifies a bottleneck separating source and sink.

                CO5 - NP-COMPLETENESS & APPROXIMATION:
                P: solvable in polynomial time.
                NP: proposed solutions can be verified in polynomial time.
                NP-hard: at least as hard as every problem in NP.
                NP-complete: both NP-hard and in NP.
                A 2-approximation for Vertex Cover returns a cover no larger
                than twice the size of an optimal cover.

                CO6 - RANDOMISED & PARALLEL ALGORITHMS:
                Randomized algorithms use random choices during execution.
                Reservoir sampling samples a stream using limited memory.
                Parallel reduction combines partial results from multiple workers.
                Prefix scan computes cumulative results.

                
                "The .txt corpus is the input. String algorithms solve exact pattern
                retrieval and indexing, while dynamic programming solves minimum-edit
                comparison, sequence alignment, resource assignment and operation-order
                optimization. Network flow, approximation, randomization and parallel
                processing extend AgriVision to resource planning and large data analysis."
                """);
        DisplayUtils.pause(sc);
    }

    private static String trim(String s, int max) {
        if (s == null) {
            return "";
        }
        s = s.replaceAll("\\s+", " ").trim();
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private static String blank(String s) {
        return (s == null || s.isBlank()) ? "N/A" : s;
    }
}
