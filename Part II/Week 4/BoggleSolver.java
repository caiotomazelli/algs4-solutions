import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

public class BoggleSolver {
    private final TST<Integer> trieST;
    private SET<String> validWords;

    private final class Tuple {
        private final int i;
        private final int j;
        public Tuple(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trieST = new TST<>();
        for (String word: dictionary) {
            trieST.put(word, value(word));
        }
        validWords = new SET<>();
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        validWords = new SET<>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                dfs(i, j, "", board, new Bag<>());
            }
        }
        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (trieST.contains(word)) {
            return trieST.get(word);
        }
        return 0;
    }

    private void dfs(int i, int j, String word, BoggleBoard board, Bag<Tuple> visited) {
        visited.add(new Tuple(i, j));
        char letter = board.getLetter(i, j);
        boolean[][] neighborMarked;
        StringBuilder newWordBuilder = new StringBuilder().append(word);
        if (letter == 'Q') {
            newWordBuilder.append("QU");
        } else {
            newWordBuilder.append(letter);
        }
        String newWord = newWordBuilder.toString();
        if (trieST.keysWithPrefix(newWord).iterator().hasNext()) {
            for (Tuple tuple: neighbors(i, j, visited, board)) {
                Bag<Tuple> newVisited = new Bag<>();
                visited.forEach(newVisited::add);
                dfs(tuple.i, tuple.j, newWord, board, newVisited);
            }
        }
        if (trieST.contains(newWord) && value(newWord) > 0) {
            validWords.add(newWord);
        }
    }

    private Bag<Tuple> neighbors(int i, int j, Bag<Tuple> visited, BoggleBoard board) {
        Bag<Tuple> neighbors = new Bag<Tuple>();
        Tuple[] candidates = {
                new Tuple(i - 1, j),
                new Tuple(i - 1, j - 1),
                new Tuple(i - 1, j + 1),
                new Tuple(i, j + 1),
                new Tuple(i, j - 1),
                new Tuple(i + 1, j),
                new Tuple(i + 1, j - 1),
                new Tuple(i + 1, j + 1)
        };
        for (Tuple c: candidates) {
            if (c.i >= 0 &&
                    c.i < board.rows() &&
                    c.j >= 0 &&
                    c.j < board.cols() &&
                    !isVisited(visited, c.i, c.j)) {
                neighbors.add(c);
            }
        }
        return neighbors;
    }

    private boolean isVisited(Bag<Tuple> visited, int i, int j) {
        for (Tuple v: visited) {
            if (v.i == i && v.j == j) {
                return true;
            }
        }
        return false;
    }

    private int value(String word) {
        if (word.length() < 3) {
            return 0;
        }
        else if (word.length() < 5) {
            return 1;
        }
        else if (word.length() == 5) {
            return 2;
        }
        else if (word.length() == 6) {
            return 3;
        }
        else if (word.length() == 7) {
            return 5;
        }
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}