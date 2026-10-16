package agrivision;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomParallelAlgorithms {

    public static void run(Scanner sc) {
        System.out.println("\n=== CO6: RANDOMISED & PARALLEL ALGORITHMS ===");

        System.out.println("\n--- Part A: Randomized QuickSort on crop-yield data ---");
        int n = readInt(sc, "How many crop-yield values will you enter? ", 1, 200000);
        int[] yields = new int[n];
        for (int i = 0; i < n; i++) {
            yields[i] = readInt(sc, "Yield value " + (i + 1) + ": ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        int[] sorted = yields.clone();
        long quickStart = System.nanoTime();
        randomizedQuickSort(sorted, 0, sorted.length - 1);
        long quickElapsed = System.nanoTime() - quickStart;
        System.out.println("Original crop-yield values: " + Arrays.toString(yields));
        System.out.println("Sorted crop-yield values:   " + Arrays.toString(sorted));
        System.out.printf("Randomized QuickSort time: %.4f ms%n", quickElapsed / 1_000_000.0);

        System.out.println("\n--- Part B: Reservoir sampling of a sensor stream ---");
        int streamSize = readInt(sc, "Number of sensor readings in stream: ", 1, 1_000_000);
        int[] stream = new int[streamSize];
        for (int i = 0; i < streamSize; i++) {
            stream[i] = readInt(sc, "Sensor reading " + (i + 1) + ": ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        int k = readInt(sc, "Sample size k (1-" + streamSize + "): ", 1, streamSize);
        long sampleStart = System.nanoTime();
        int[] sample = reservoirSample(stream, k);
        long sampleElapsed = System.nanoTime() - sampleStart;
        System.out.println("Sensor stream: " + Arrays.toString(stream));
        System.out.println("Reservoir sample (k=" + k + "): " + Arrays.toString(sample));
        System.out.printf("Reservoir Sampling time: %.4f ms%n", sampleElapsed / 1_000_000.0);

        System.out.println("\n--- Part C: Miller-Rabin probable-prime test ---");
        System.out.print("Enter a positive integer to test: ");
        BigInteger number;
        while (true) {
            String input = sc.nextLine().trim();
            try {
                number = new BigInteger(input);
                if (number.signum() >= 0) break;
            } catch (NumberFormatException ignored) { }
            System.out.print("Please enter a non-negative integer: ");
        }
        int rounds = readInt(sc, "Miller-Rabin rounds (1-50): ", 1, 50);
        long primeStart = System.nanoTime();
        boolean prime = isProbablePrime(number, rounds);
        long primeElapsed = System.nanoTime() - primeStart;
        System.out.println(number + (prime ? " is probably prime." : " is composite."));
        System.out.printf("Miller-Rabin time: %.4f ms%n", primeElapsed / 1_000_000.0);

        System.out.println("\n--- Part D: Parallel reduction of sensor readings ---");
        int workers = readInt(sc, "Number of worker threads (1-" + Runtime.getRuntime().availableProcessors() + "): ",
                1, Math.max(1, Runtime.getRuntime().availableProcessors()));
        long reduceStart = System.nanoTime();
        long parallelSum = parallelReduceSum(stream, workers);
        long reduceElapsed = System.nanoTime() - reduceStart;
        long serialStart = System.nanoTime();
        long serialSum = 0;
        for (int value : stream) serialSum += value;
        long serialElapsed = System.nanoTime() - serialStart;

        System.out.println("Parallel sum: " + parallelSum);
        System.out.println("Sequential check sum: " + serialSum);
        System.out.printf("Parallel Reduce time (includes thread overhead): %.4f ms%n", reduceElapsed / 1_000_000.0);
        System.out.printf("Sequential sum time: %.4f ms%n", serialElapsed / 1_000_000.0);
        System.out.println("Sums match: " + (parallelSum == serialSum));

        System.out.println("\n--- Part E: Prefix scan of sensor readings ---");
        long scanStart = System.nanoTime();
        long[] prefix = prefixScan(stream);
        long scanElapsed = System.nanoTime() - scanStart;
        System.out.println("Prefix sums: " + Arrays.toString(prefix));
        System.out.printf("Prefix Scan reference implementation time: %.4f ms%n", scanElapsed / 1_000_000.0);

        System.out.println("\nComplexity summary:");
        System.out.println("Randomized QuickSort: expected O(n log n), worst O(n^2).");
        System.out.println("Reservoir Sampling: O(n) time and O(k) sample storage.");
        System.out.println("Miller-Rabin: depends on input bit length and number of rounds.");
        System.out.println("Parallel Reduce: O(n) total work; elapsed time depends on workers and overhead.");
        System.out.println("Prefix Scan shown here is a sequential reference implementation: O(n) time.");
    }

    public static void randomizedQuickSort(int[] a, int low, int high) {
        if (low >= high) return;
        int pivotIndex = ThreadLocalRandom.current().nextInt(low, high + 1);
        swap(a, pivotIndex, high);
        int p = partition(a, low, high);
        randomizedQuickSort(a, low, p - 1);
        randomizedQuickSort(a, p + 1, high);
    }

    private static int partition(int[] a, int low, int high) {
        int pivot = a[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (a[j] <= pivot) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }

    public static int[] reservoirSample(int[] stream, int k) {
        if (k < 1 || k > stream.length) throw new IllegalArgumentException("k must be between 1 and stream length.");
        int[] reservoir = Arrays.copyOf(stream, k);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = k; i < stream.length; i++) {
            int j = random.nextInt(i + 1);
            if (j < k) reservoir[j] = stream[i];
        }
        return reservoir;
    }

    public static boolean isProbablePrime(BigInteger n, int rounds) {
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (!n.testBit(0)) return false;

        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger d = nMinusOne;
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }

        Random random = new Random();
        BigInteger nMinusThree = n.subtract(BigInteger.valueOf(3));
        for (int i = 0; i < rounds; i++) {
            BigInteger a;
            if (nMinusThree.signum() <= 0) {
                a = BigInteger.TWO;
            } else {
                do {
                    a = new BigInteger(n.bitLength(), random);
                } while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(n.subtract(BigInteger.TWO)) > 0);
            }

            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) continue;

            boolean passed = false;
            for (int r = 1; r < s; r++) {
                x = x.multiply(x).mod(n);
                if (x.equals(nMinusOne)) {
                    passed = true;
                    break;
                }
            }
            if (!passed) return false;
        }
        return true;
    }

    public static long parallelReduceSum(int[] a, int workers) {
        if (a.length == 0) return 0;
        int actualWorkers = Math.max(1, Math.min(workers, a.length));
        ExecutorService pool = Executors.newFixedThreadPool(actualWorkers);
        List<Future<Long>> futures = new ArrayList<>();
        int chunk = (a.length + actualWorkers - 1) / actualWorkers;

        for (int w = 0; w < actualWorkers; w++) {
            final int start = w * chunk;
            final int end = Math.min(a.length, start + chunk);
            if (start >= end) break;
            futures.add(pool.submit(() -> {
                long sum = 0;
                for (int i = start; i < end; i++) sum += a[i];
                return sum;
            }));
        }

        long total = 0;
        try {
            for (Future<Long> f : futures) total += f.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Parallel reduction was interrupted.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Parallel reduction failed.", e.getCause());
        } finally {
            pool.shutdown();
        }
        return total;
    }

    public static long[] prefixScan(int[] a) {
        long[] prefix = new long[a.length];
        long running = 0;
        for (int i = 0; i < a.length; i++) {
            running += a[i];
            prefix[i] = running;
        }
        return prefix;
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
