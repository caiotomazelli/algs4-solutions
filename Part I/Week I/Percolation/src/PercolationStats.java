import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final int trials;
    private final double mean;
    private final double stddev;

    public PercolationStats(int n, int trials) {
        final double[] openSitesRatios;

        if (n <= 0 || trials <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        this.trials = trials;
        openSitesRatios = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                percolation.open(1 + StdRandom.uniform(n), 1 + StdRandom.uniform(n));
            }
            openSitesRatios[i] = (double) percolation.numberOfOpenSites() / (n*n);
        }
        mean = StdStats.mean(openSitesRatios);
        stddev = StdStats.stddev(openSitesRatios);
    }

    public double mean() {
        return mean;
    }
    public double stddev() {
        return stddev;
    }
    private double confidence(int sigma) {
        double sigmaConst = 1.96;
        return mean + sigma*sigmaConst*stddev/java.lang.Math.sqrt(trials);
    }
    public double confidenceLo() {
        return confidence(-1);
    }
    public double confidenceHi() {
        return confidence(1);
    }
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        int n = Integer.parseInt(args[0]);
        int repetitions = Integer.parseInt(args[1]);

        PercolationStats pStats = new PercolationStats(n, repetitions);
        System.out.println("mean \t\t\t = "+ pStats.mean());
        System.out.println("stddev \t\t\t = "+ pStats.stddev());
        System.out.println("95% confidence interval \t\t\t = ["+ pStats.confidenceLo() +
                ", " + pStats.confidenceHi() + "]");
    }
}