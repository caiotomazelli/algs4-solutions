import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;
import java.util.HashMap;

public class WordNet {
    private final HashMap<String, Bag<Integer>> synsetMap = new HashMap<>();
    private final ArrayList<String> synsetList = new ArrayList<>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        In synsetsInput = new In(synsets);
        In hypernymsInput = new In(hypernyms);

        while (synsetsInput.hasNextLine()) {
            String line = synsetsInput.readLine();
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            String[] nouns = fields[1].split(" ");
            for (String noun: nouns) {
                if (synsetMap.containsKey(noun)) {
                    synsetMap.get(noun).add(id);
                } else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(id);
                    synsetMap.put(noun, bag);
                }
            }
            synsetList.add(fields[1]);
        }

        Digraph hypernymDigraph = new Digraph(synsetList.size());
        DirectedCycle directedCycle = new DirectedCycle(hypernymDigraph);

        if (directedCycle.hasCycle()) {
            throw new IllegalArgumentException("Digraph is not a DAG");
        }

        while (hypernymsInput.hasNextLine()) {
            String line = hypernymsInput.readLine();
            String[] fields = line.split(",");
            int v = Integer.parseInt(fields[0]);
            if (fields.length > 1) {
                String[] hypernymIds = fields[1].split(",");
                for (String hypernymId: hypernymIds) {
                    int w = Integer.parseInt(hypernymId);
                    hypernymDigraph.addEdge(v, w);
                }
            }
            else {
                hypernymDigraph.addEdge(v, v);
            }
        }
        sap = new SAP(hypernymDigraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("null word passed as argument");
        }
        return synsetMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        return sap.length(synsetMap.get(nounA), synsetMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        int commonAncestor = sap.ancestor(synsetMap.get(nounA), synsetMap.get(nounB));
        return synsetList.get(commonAncestor).split(" ")[0];
    }


    public static void main(String[] args) {
        // do unit testing of this class
    }
}
