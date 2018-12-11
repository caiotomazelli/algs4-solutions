import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Collections;

public class SAPBackup {
    private final Digraph G;
    private int[] srcRoots;
    private int[] destRoots;
    private int[] srcLengths;
    private int[] destLengths;
    private int currentLength;

    // constructor takes a digraph (not necessarily a DAG)
    public SAPBackup(Digraph G) {
        this.G = new Digraph(G);
        srcRoots = new int[G.V()];
        destRoots = new int[G.V()];
        srcLengths = new int[G.V()];
        destLengths = new int[G.V()];
        currentLength = -1;
    }

    private void resetRoots() {
        for (int i = 0; i < G.V(); i++) {
            srcRoots[i] = -1;
            destRoots[i] = -1;
            srcLengths[i] = 0;
            destLengths[i] = 0;
        }
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
        ancestor(v, w);
        return currentLength;
    }

    private void checkValidity(Integer x) {
        if (x == null || x < 0 || x >= G.V()) {
            throw new IllegalArgumentException("Invalid argument: " + x);
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            return -1;
        }
        resetRoots();
        Queue<Integer> q = new Queue<>();
        int currentAncestor;
        boolean isSrc;
        for (Integer vElem : v) {
            checkValidity(vElem);
            q.enqueue(vElem);
            srcRoots[vElem] = vElem;
        }
        for (Integer wElem : w) {
            checkValidity(wElem);
            if (srcRoots[wElem] != -1) {
                currentLength = 0;
                return wElem;
            }
            q.enqueue(wElem);
            destRoots[wElem] = wElem;
        }
        currentLength = -1;
        currentAncestor = -1;
        while (!q.isEmpty()) {
            int x = q.dequeue();
            isSrc = srcRoots[x] != -1 && destRoots[x] == -1;
            for (int y: G.adj(x)) {
                if (isSrc) {
                    if (destRoots[y] != -1) {
                        int length = destLengths[y] + srcLengths[x] + 1;
                        if (currentLength == -1 || length < currentLength) {
                            currentLength = length;
                            currentAncestor = y;
                        }
                    }
                    if (srcRoots[y] == -1) {
                        srcRoots[y] = srcRoots[x];
                        srcLengths[y] = srcLengths[x] + 1;
                    }
                    q.enqueue(y);
                }
                else {
                    if (srcRoots[y] != -1) {
                        int length = srcLengths[y] + destLengths[x] + 1;
                        if (currentLength == -1 || length < currentLength) {
                            currentLength = length;
                            currentAncestor = y;
                        }
                    }
                    if (destRoots[y] == -1) {
                        destRoots[y] = destRoots[x];
                        destLengths[y] = destLengths[x] + 1;
                    }
                    q.enqueue(y);
                }
            }
        }
        return currentAncestor;
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
