import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns)  {
        String outCandidate;
        int maxDistance = 0;
        int currentSum;
        int outcast = -1;
        for (int i = 0; i < nouns.length; i++) {
            currentSum = 0;
            outCandidate = nouns[i].split(" ")[0];
            for (int j = 0; j < nouns.length; j++) {
                if (j != i) {
                    String sap = wordnet.sap(outCandidate, nouns[j]);
                    currentSum += wordnet.distance(outCandidate, sap);
                }
            }
            if (currentSum > maxDistance) {
                maxDistance = currentSum;
                outcast = i;
            }
        }
        return nouns[outcast];
    }
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}