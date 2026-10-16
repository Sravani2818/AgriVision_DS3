package agrivision;

import java.math.BigInteger;
import java.util.*;

/** CO6: randomized algorithms, streaming sample, primality testing, and parallel primitives. */
public class RandomParallelAlgorithms {
    private RandomParallelAlgorithms() {}
    private static final Random RANDOM=new Random();

    // Randomized quicksort chooses a random pivot; expected time O(n log n), worst O(n^2).
    public static void randomizedQuickSort(int[] a){quick(a,0,a.length-1);}
    private static void quick(int[]a,int lo,int hi){if(lo>=hi)return;int p=lo+RANDOM.nextInt(hi-lo+1);swap(a,p,hi);int pivot=a[hi],i=lo;for(int j=lo;j<hi;j++)if(a[j]<=pivot)swap(a,i++,j);swap(a,i,hi);quick(a,lo,i-1);quick(a,i+1,hi);}
    private static void swap(int[]a,int i,int j){int t=a[i];a[i]=a[j];a[j]=t;}

    // Reservoir sampling keeps a uniform sample of k items from a stream of unknown length.
    public static List<Integer> reservoirSample(int[] stream,int k){if(k<0)throw new IllegalArgumentException("k must be non-negative");List<Integer> r=new ArrayList<>();for(int i=0;i<stream.length;i++){if(i<k)r.add(stream[i]);else if(k>0){int j=RANDOM.nextInt(i+1);if(j<k)r.set(j,stream[i]);}}return r;}

    // Miller-Rabin: a probabilistic primality test; false-positive probability falls with rounds.
    public static boolean isProbablyPrime(long n,int rounds){if(n<2)return false;for(int p:new int[]{2,3,5,7,11,13,17,19,23,29,31,37}){if(n==p)return true;if(n%p==0)return false;}long d=n-1;int s=0;while((d&1)==0){d>>=1;s++;}BigInteger N=BigInteger.valueOf(n),nm1=N.subtract(BigInteger.ONE);for(int i=0;i<Math.max(1,rounds);i++){long base=2+Math.floorMod(RANDOM.nextLong(),n-3);BigInteger x=BigInteger.valueOf(base).modPow(BigInteger.valueOf(d),N);if(x.equals(BigInteger.ONE)||x.equals(nm1))continue;boolean witness=true;for(int r=1;r<s;r++){x=x.multiply(x).mod(N);if(x.equals(nm1)){witness=false;break;}}if(witness)return false;}return true;}

    // Parallel-reduce intuition: combine chunks locally, then combine the partial sums.
    public static long parallelReduceSum(int[] a,int workers){if(workers<1)throw new IllegalArgumentException("workers >= 1");int chunks=Math.min(workers,Math.max(1,a.length));long[] partial=new long[chunks];Thread[] ts=new Thread[chunks];for(int c=0;c<chunks;c++){final int id=c,start=a.length*c/chunks,end=a.length*(c+1)/chunks;ts[c]=new Thread(()->{long sum=0;for(int i=start;i<end;i++)sum+=a[i];partial[id]=sum;});ts[c].start();}for(Thread t:ts)try{t.join();}catch(InterruptedException e){Thread.currentThread().interrupt();throw new IllegalStateException("Interrupted",e);}long total=0;for(long x:partial)total+=x;return total;}

    // Prefix scan: output[i] is the sum of input[0..i]; sequential reference for Blelloch-scan concept.
    public static long[] prefixScan(int[] a){long[] out=new long[a.length];long sum=0;for(int i=0;i<a.length;i++){sum+=a[i];out[i]=sum;}return out;}
}
