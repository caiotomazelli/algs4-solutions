import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Collections;

public class SAP {
    private final Digraph G;
    private int[] vLengths;
    private int[] wLengths;
    private int minLength;
    private int sap;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = new Digraph(G);
        vLengths = new int[G.V()];
        wLengths = new int[G.V()];
        minLength = Integer.MAX_VALUE;
        sap = -1;
    }

    private void resetRoots() {
        for (int i = 0; i < G.V(); i++) {
            vLengths[i] = -1;
            wLengths[i] = -1;
        }
        minLength = Integer.MAX_VALUE;
        sap = -1;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return length(Collections.singleton(v), Collections.singleton(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return ancestor(Collections.singleton(v), Collections.singleton(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        findSAP(v, w);
        return minLength;
    }

    private void checkValidity(Integer x) {
        if (x == null || x < 0 || x >= G.V()) {
            throw new IllegalArgumentException("Invalid argument: " + x);
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        findSAP(v, w);
        return sap;
    }

    private void findSAP(Iterable<Integer> v, Iterable<Integer> w) {
        resetRoots();
        Queue<Integer> vQueue = new Queue<>();
        Queue<Integer> wQueue = new Queue<>();
        int count = 0;
        for (Integer vElem: v) {
            checkValidity(vElem);
            vQueue.enqueue(vElem);
            vLengths[vElem] = 0;
        }
        for (Integer wElem: w) {
            checkValidity(wElem);
            if (vLengths[wElem] != -1) {
                minLength = 0;
                sap = wElem;
                return;
            }
            wQueue.enqueue(wElem);
            wLengths[wElem] = 0;
        }
        while (!vQueue.isEmpty() || !wQueue.isEmpty()) {
            if (count % 2 == 0) {
                if (!vQueue.isEmpty()) {
                    bfs(vQueue.dequeue(), vQueue, vLengths, wLengths);
                }
            }
            else {
                if (!wQueue.isEmpty()) {
                    bfs(wQueue.dequeue(), wQueue, wLengths, vLengths);
                }
            }
            count++;
        }
        if (sap == -1) {
            minLength = -1;
        }
    }

    private void bfs(int x, Queue<Integer> srcQueue, int[] srcLengths, int[] destLengths) {
        for (int y: G.adj(x)) {
            if (srcLengths[y] == -1) {
                srcQueue.enqueue(y);
                srcLengths[y] = srcLengths[x] + 1;
            }
            if (srcLengths[x] + 1 < srcLengths[y]) {
                srcLengths[y] = srcLengths[x] + 1;
            }
            if (srcLengths[y] >= minLength) {
                return;
            }
            if (destLengths[y] != -1) {
                int currentLength = destLengths[y] + srcLengths[y];
                if (currentLength < minLength) {
                    minLength = currentLength;
                    sap = y;
                }
            }
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
