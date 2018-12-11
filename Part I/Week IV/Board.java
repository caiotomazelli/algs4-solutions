import edu.princeton.cs.algs4.In;

import java.util.Stack;

public class Board {
    private int[][] blocks;
    private final int dimension;

    public Board(int[][] blocks) { // construct a board from an n-by-n array of blocks (where blocks[i][j] = block in row i, column j)
        dimension = blocks[0].length;
        this.blocks = new int[dimension][dimension];
        for (int k = 0; k < dimension; k++) {
            System.arraycopy(blocks[k], 0, this.blocks[k], 0, dimension);
        }
    }

    public int dimension() { // board dimension n
        return dimension;
    }

    private boolean inGoalPosition(int i, int j) { // check if element i,j is in goal position
        return blocks[i][j] == (i * dimension + j + 1) % (dimension * dimension);
    }

    public int hamming() { // number of blocks out of place
        int distance = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] == 0) {
                    continue;
                }
                if (!inGoalPosition(i, j)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    private int absDistance(int x, int y) {
        if (x > y) {
            return x - y;
        } else {
            return y - x;
        }
    }

    public int manhattan() { // sum of Manhattan distances between blocks and goal
        int distance = 0;
        int current;
        int goalRow;
        int goalCol;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                current = blocks[i][j];
                if (inGoalPosition(i, j)) {
                    continue;
                } else if (current == 0) {
                    continue;
                } else {
                    goalRow = (blocks[i][j] - 1) / dimension;
                    goalCol = (blocks[i][j] - 1) % dimension;
                }
                distance += absDistance(i, goalRow) + absDistance(j, goalCol);
            }
        }
        return distance;
    }

    public boolean isGoal() { // is this board the goal board?
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (!inGoalPosition(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Board twin(int i, int j, int x, int y) { // a board that is obtained by exchanging block i, j with block x, y
        int[][] twinBlocks = new int[dimension][dimension];
        for (int k = 0; k < dimension; k++) {
            System.arraycopy(this.blocks[k], 0, twinBlocks[k], 0, dimension);
        }
        int backup = twinBlocks[i][j];
        twinBlocks[i][j] = twinBlocks[x][y];
        twinBlocks[x][y] = backup;
        return new Board(twinBlocks);
    }

    public Board twin() { // a board that is obtained by exchanging any pair of blocks
        int i = 0;
        int j = 0;
        int x = 0;
        int y = 1;
        int b1 = blocks[i][j];
        int b2 = blocks[x][y];
        if (b1 == 0) {
            i++;
        }
        if (b2 == 0) {
            x++;
        }
        return twin(i, j, x, y);
    }

    public boolean equals(Object y) { // does this board equal y?
        if (y == this) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (this.dimension() != that.dimension()) {
            return false;
        }
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] != that.blocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isInsideBoard(int i, int j) {
        return i >= 0 && i < dimension && j >= 0 && j < dimension;
    }

    public Iterable<Board> neighbors() { // all neighboring boards
        Stack<Board> neighbors = new Stack<>();
        int zeroRow = dimension;
        int zeroCol = dimension;
        emptyBlockFound:
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                    break emptyBlockFound;
                }
            }
        }
        if (zeroRow == dimension) {
            throw new java.lang.IllegalArgumentException();
        }
        int[][] potentialNeighbors = new int[][]{
                {zeroRow - 1, zeroCol},
                {zeroRow + 1, zeroCol},
                {zeroRow, zeroCol - 1},
                {zeroRow, zeroCol + 1}
        };
        for (int[] tuple : potentialNeighbors) {
            if (isInsideBoard(tuple[0], tuple[1])) {
                neighbors.push(twin(zeroRow, zeroCol, tuple[0], tuple[1]));
            }
        }
        return neighbors;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dimension + "\n");
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s.append(String.format("%2d ", blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    private static void testStatement(boolean statement, String msg) {
        if (!statement) {
            System.out.println(msg);
        }
    }
    public static void main(String[] args) { // unit tests (not graded)

        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            // solve the slider puzzle
            Board initial = new Board(tiles);
            testStatement(n == initial.dimension(), "n == initial.dimension()");
            System.out.println("----------START printing initial");
            System.out.println(initial);
            System.out.println("----------END printing initial");
            System.out.println("----------START printing neighbors");
            for (Board neighbor : initial.neighbors()) {
                System.out.println(neighbor);
            }
            System.out.println("Twin: \n" + initial.twin());
            System.out.println("----------END printing neighbors");
            System.out.println("Hamming distance: " + initial.hamming());
            System.out.println("Manhattan distance: " + initial.manhattan());
        }
    }
}
