import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private Picture picture;
    private double[][] energyMatrix;


    private class Tuple {
        private final int x;
        private final int y;
        public Tuple(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int x() {
            return x;
        }
        public int y() {
            return y;
        }
    }


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Constructor received null picture as argument");
        }
        this.picture = new Picture(picture);
        energyMatrix = new double[picture.width()][picture.height()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                energyMatrix[i][j] = energy(i, j);
            }
        }
    }
    // current picture
    public Picture picture() {
        return picture;
    }
    // width of current picture
    public int width() {
        return picture.width();
    }
    // height of current picture
    public int height() {
        return picture.height();
    }
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        checkRange(x, y);
        if (x == 0 || y == 0 || x == picture.width() - 1 || y == picture.height() - 1) {
            return 1000;
        }
        double squareGradX = squareGradient(x - 1, y, x + 1, y);
        double squareGradY = squareGradient(x, y - 1, x, y + 1);
        return Math.sqrt(squareGradX + squareGradY);
    }
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transposeProblem();
        int[] seam = findVerticalSeam();
        transposeProblem();
        return seam;
    }
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[picture.height()];
        double seamEnergy = Double.POSITIVE_INFINITY;
        int minEndPixel = 0;
        Tuple[][] parentPixel = new Tuple[picture.width()][picture.height()];
        double[][] distTo = new double[picture.width()][picture.height()];

        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                if (j == 0) {
                    distTo[i][j] = 0;
                }
                else {
                    distTo[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int j = 0; j < picture.height(); j++) {
            for (int i = 0; i < picture.width(); i++) {
                for (Tuple other: others(i, j)) {
                    double tempDist = distTo[i][j] + energy(i, j);
                    if (tempDist < distTo[other.x()][other.y()]) {
                        distTo[other.x()][other.y()] = tempDist;
                        parentPixel[other.x()][other.y()] = new Tuple(i, j);
                    }
                }
            }
        }

        for (int i = 0; i < picture.width(); i++) {
            if (distTo[i][picture.height() - 1] < seamEnergy) {
                seamEnergy = distTo[i][picture.height() - 1];
                minEndPixel = i;
            }
        }

        Tuple pixel = new Tuple(minEndPixel, picture.height() - 1);
        while (parentPixel[pixel.x()][pixel.y()] != null) {
            seam[pixel.y()] = pixel.x();
            pixel = parentPixel[pixel.x()][pixel.y()];
        }
        seam[0] = pixel.x();

        return seam;
    }


    // dumb heuristic to find sequence of indices for vertical seam
//    private MinEnergyCandidate[] getMinEnergyCandidates(int i, int j, double[][] energyMatrix) {
//        MinEnergyCandidate[] candidates;
//        if (i == 0) {
//            candidates = new MinEnergyCandidate[2];
//            candidates[0] = new MinEnergyCandidate(i, energyMatrix[i][j]);
//            candidates[1] = new MinEnergyCandidate(i+1, energyMatrix[i+1][j]);
//            return candidates;
//        }
//        else if (i == picture.width() - 1) {
//            candidates = new MinEnergyCandidate[2];
//            candidates[0] = new MinEnergyCandidate(i-1, energyMatrix[i-1][j]);
//            candidates[1] = new MinEnergyCandidate(i, energyMatrix[i][j]);
//            return candidates;
//        }
//        candidates = new MinEnergyCandidate[3];
//        candidates[0] = new MinEnergyCandidate(i-1, energyMatrix[i-1][j]);
//        candidates[1] = new MinEnergyCandidate(i, energyMatrix[i][j]);
//        candidates[2] = new MinEnergyCandidate(i+1, energyMatrix[i+1][j]);
//        return candidates;
//    }
//    private class MinEnergyCandidate {
//        private final int position;
//        private final double energy;
//        public MinEnergyCandidate(int position, double energy) {
//            this.position = position;
//            this.energy = energy;
//        }
//
//        public int position() {
//            return position;
//        }
//
//        public double energy() {
//            return energy;
//        }
//    }
//    private int[] findVerticalSeamHeuristically() {
//        int[] seam = new int[picture.height()];
//        int[] tempSeam = new int[picture.height()];
//        MinEnergyCandidate[] minTempCandidates;
//        double minSeamEnergy = Double.POSITIVE_INFINITY;
//        double minTempEnergy;
//        double minTempCandidateEnergy;
//        int minTempX;
//        for (int startI = 0; startI < picture.width(); startI++) {
//            minTempEnergy = 0;
//            minTempX = -1;
//            for (int j = 0, i = startI; j < picture.height(); j++) {
//                minTempCandidateEnergy = Double.POSITIVE_INFINITY;
//                minTempCandidates = getMinEnergyCandidates(i, j, energyMatrix);
//                for (MinEnergyCandidate candidate:  minTempCandidates) {
//                    if (candidate.energy() < minTempCandidateEnergy) {
//                        minTempCandidateEnergy = candidate.energy();
//                        minTempX = candidate.position();
//                    }
//                }
//                tempSeam[j] = minTempX;
//                i = minTempX;
//                minTempEnergy += minTempCandidateEnergy;
//            }
//            if (minTempEnergy < minSeamEnergy) {
//                minSeamEnergy = minTempEnergy;
//                System.arraycopy(tempSeam, 0, seam, 0, tempSeam.length);
//            }
//        }
//        return seam;
//    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkSeamValidity(seam, false);
        transposeProblem();
        removeVerticalSeam(seam);
        transposeProblem();
    }
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam)  {
        double[][] newEnergyMatrix = new double[picture.width() - 1][picture.height()];
        Picture newPicture = new Picture(picture.width() - 1, picture.height());
        checkSeamValidity(seam, true);
        for (int j = 0; j < picture.height(); j++) {
            for (int i = 0, newI = 0; i < picture.width(); i++) {
                if (i != seam[j]) {
                    newEnergyMatrix[newI][j] = energyMatrix[i][j];
                    newPicture.setRGB(newI, j, picture.getRGB(i, j));
                    newI++;
                }
            }
        }
        picture = newPicture;
        energyMatrix = newEnergyMatrix;

    }

    private Tuple[] others(int i, int j) {
        Tuple[] others;
        if (j == picture.height() - 1) {
            return new Tuple[]{};
        }
        if (i == 0 && picture.width() > 1) {
            others = new Tuple[2];
            others[0] = new Tuple(i, j+1);
            others[1] = new Tuple(i+1, j+1);
            return others;
        }
        if (picture.width() == 1) {
            others = new Tuple[1];
            others[0] = new Tuple(i, j+1);
            return others;
        }
        else if (i == picture.width() - 1) {
            others = new Tuple[2];
            others[0] = new Tuple(i-1, j+1);
            others[1] = new Tuple(i, j+1);
            return others;
        }
        others = new Tuple[3];
        others[0] = new Tuple(i-1, j+1);
        others[1] = new Tuple(i, j+1);
        others[2] = new Tuple(i+1, j+1);
        return others;
    }

    private void transposeProblem() {
        Picture transposePicture = new Picture(picture.height(), picture.width());
        double[][] transposeEnergyMatrix = new double[picture.height()][picture.width()];
        for (int j = 0; j < picture.height(); j++) {
            for (int i = 0; i < picture.width(); i++) {
                transposePicture.setRGB(j, i, picture.getRGB(i, j));
                transposeEnergyMatrix[j][i] = energyMatrix[i][j];

            }
        }
        picture = transposePicture;
        energyMatrix = transposeEnergyMatrix;
    }

    private void checkRange(int x, int y) {
        if (x < 0 || x >= picture.width()) {
            throw new IllegalArgumentException("x: " + x + " is an invalid image horizontal position");
        }
        if (y < 0 || y >= picture.height()) {
            throw new IllegalArgumentException("y: " + y + " is an invalid image vertical position");
        }
    }

    private void checkSeamSmoothness(int previous, int current) {
        if (current > previous + 1 || current < previous - 1) {
            throw new IllegalArgumentException("Seam violates smoothness check");
        }
    }

    private void checkSeamValidity(int[] seam, boolean isVertical) {
        if (seam == null) {
            throw new IllegalArgumentException("removeSeam received null seam as argument");
        }
        if (isVertical) {
            if (seam.length != picture.height()) {
                throw new IllegalArgumentException("removeVerticalSeam called with wrong seam height");
            }
            if (picture.width() < 2) {
                throw new IllegalArgumentException("removeVerticalSeam called for too narrow a picture");
            }
            checkRange(seam[0], 0);
            for (int i = 1; i < seam.length; i++) {
                checkRange(seam[i], i);
                checkSeamSmoothness(seam[i-1], seam[i]);
            }
        }
        else {
            if (seam.length != picture.width()) {
                throw new IllegalArgumentException("removeHorizontalSeam called with wrong seam width");
            }
            if (picture.height() < 2) {
                throw new IllegalArgumentException("removeHorizontalSeam called for too short a picture");
            }
            checkRange(0, seam[0]);
            for (int i = 1; i < seam.length; i++) {
                checkRange(i, seam[i]);
                checkSeamSmoothness(seam[i-1], seam[i]);
            }
        }
    }

    // ul stands for upper-left and br for bottom-right
    private double squareGradient(int ulX, int ulY, int brX, int brY) {
        Color ulColor = picture.get(ulX, ulY);
        Color brColor = picture.get(brX, brY);
        return diffSquare(ulColor.getRed(), brColor.getRed())
             + diffSquare(ulColor.getGreen(), brColor.getGreen())
             + diffSquare(ulColor.getBlue(), brColor.getBlue());
    }

    private double diffSquare(int x, int y) {
        return Math.pow(x - y, 2);
    }
}