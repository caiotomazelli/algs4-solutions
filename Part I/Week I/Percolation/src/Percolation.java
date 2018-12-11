import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int n;
    private boolean[][] grid;
    private int numberOfOpenSites;
    private final WeightedQuickUnionUF uf;

    public Percolation(int n) {
        if (n <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        this.n = n;
        this.numberOfOpenSites = 0;
        this.grid = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = false;
            }
        }
        // initialize union-find structure
        this.uf = new WeightedQuickUnionUF(n*n+2); // all elements of grid plus 2 virtual sites
        // Initialize virtual sites in locations n and n+1
        for (int i = 0; i < n; i++) {
            uf.union(i, n*n); // all elements of top row connected to top virtual site
            uf.union(n*(n-1) + i, n*n+1); // all elements of bottow row connected to bottom virtual site
        }
    }

    private boolean getGridAt(int row, int col) {
        int r = row - 1;
        int c = col - 1;
        return grid[r][c];
    }

    private void openCellAt(int row, int col) {
        int r = row - 1;
        int c = col - 1;
        grid[r][c] = true;
    }

    private int posToUfId(int row, int col) {
        int r = row - 1;
        int c = col - 1;
        return n*r + c;
    }

    private boolean inRange(int row, int col) {
        return row > 0 && row <= n && col > 0 && col <= n;
    }

    private void checkAccessRange(int row, int col) {
        if (!inRange(row, col)) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    public void open(int row, int col) {
        if (!isOpen(row, col)) {
            openCellAt(row, col);
            this.numberOfOpenSites++;
            final int[][] neighbors = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            int neighborRow;
            int neighborCol;
            for (int[] neighbor: neighbors) {
                neighborRow = row + neighbor[0];
                neighborCol = col + neighbor[1];
                if (inRange(neighborRow, neighborCol)) {
                    if (getGridAt(neighborRow, neighborCol)) {
                        uf.union(posToUfId(row, col), posToUfId(neighborRow, neighborCol));
                    }
                }
            }
        }
    }
    public boolean isOpen(int row, int col) {
        checkAccessRange(row, col);
        return getGridAt(row, col);
    }
    public boolean isFull(int row, int col) {
        return isOpen(row, col) && uf.connected(posToUfId(row, col), n*n);
    }
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }
    public boolean percolates() {
        return numberOfOpenSites > 0 && uf.connected(n*n, n*n+1);
    }

    public static void main(String[] args) {
        int n = 3;
        Percolation percolation = new Percolation(n);
        percolation.open(2, 1);
        percolation.open(1, 3);
        percolation.open(2, 3);
        percolation.open(3, 3);

        if (!percolation.percolates()) {
            System.out.println("Failed Percolation test");
        }
        if (!percolation.isFull(2, 3)) {
            System.out.println("Failed isFull test for row 2 col 3");
        }
        if (!percolation.isOpen(2, 1)) {
            System.out.println("Failed isOpen test for row 2 col 1");
        }
        if (percolation.isOpen(2, 2)) {
            System.out.println("Failed isOpen test for row 2 col 2");
        }
        if (percolation.isFull(2, 1)) {
            System.out.println("Failed isFull test for row 2 col 1");
        }

        System.out.println("Test finished");

    }
}