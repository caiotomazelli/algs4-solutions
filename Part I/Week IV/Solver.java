import edu.princeton.cs.algs4.MinPQ;

import java.util.Comparator;
import java.util.Stack;

public class Solver {
    private Stack<Board> solutionSteps = new Stack<>();
    private int moves = 0;
    private boolean isSolvable;

    public Solver(Board initial) { // find a solution to the initial board (using the A* algorithm)
        MinPQ<Node> queue = new MinPQ<>(new AStarPriorityFunction());
        MinPQ<Node> twinQueue = new MinPQ<>(new AStarPriorityFunction());
        Node searchNode = new Node(initial, null, moves);
        Node twinSearchNode = new Node(initial.twin(), null, moves);
        while (!searchNode.board.isGoal() && !twinSearchNode.board.isGoal()) {
            addNeighbors(queue, searchNode);
            addNeighbors(twinQueue, twinSearchNode);
            searchNode = queue.delMin();
            twinSearchNode = twinQueue.delMin();
        }
        if (searchNode.board.isGoal()) {
            isSolvable = true;
            while (searchNode != null) {
                solutionSteps.push(searchNode.board);
                searchNode = searchNode.predecessor;
            }
            moves = solutionSteps.size() - 1;
        } else if (twinSearchNode.board.isGoal()) {
            isSolvable = false;
            solutionSteps = null;
            moves = -1;
        }
    }

    private void addNeighbors(MinPQ<Node> q, Node parent) {
        for (Board neighbor: parent.board.neighbors()) {
            if (parent.predecessor != null) {
                if (!parent.predecessor.board.equals(neighbor)) {
                    q.insert(new Node(neighbor, parent, parent.moves + 1));
                }
            } else {
                q.insert(new Node(neighbor, parent, parent.moves + 1));
            }

        }
    }

    private class Node {
        Board board;
        int moves;
        Node predecessor;
        Node(Board board, Node predecessor, int moves) {
            this.board = board;
            this.predecessor = predecessor;
            this.moves = moves;
        }
    }

    private class AStarPriorityFunction implements Comparator<Node> {
        @Override
        public int compare(Node b1, Node b2) {
            int b1Priority = b1.board.manhattan() + b1.moves;
            int b2Priority = b2.board.manhattan() + b2.moves;
            return Integer.compare(b1Priority, b2Priority);
        }
    }

    public boolean isSolvable() { // is the initial board solvable?
        return isSolvable;
    }
    public int moves() { // min number of moves to solve initial board; -1 if unsolvable
        return moves;
    }
    public Iterable<Board> solution() {  // sequence of boards in a shortest solution; null if unsolvable
        return solutionSteps;
    }
    public static void main(String[] args) { // solve a slider puzzle (given below)

    }
}
