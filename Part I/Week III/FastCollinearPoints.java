import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class FastCollinearPoints {
    private final LineSegment[] segments;
    private int numberOfSegments = 0;
    private int numberOfEndpoints = 0;

    public FastCollinearPoints(Point[] points) {
        checkArrayValidity(points);
        LineSegment[] tempSegments = new LineSegment[10];
        Point[] allEndpoints = new Point[20];
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            Point[] pointArray = points.clone();
            Arrays.sort(pointArray, i + 1, points.length, p.slopeOrder());
            int j = 0;
            while (j < pointArray.length) {
                Point q = pointArray[j];
                double currentSlope = p.slopeTo(q);
                Point[] collinearPoints = new Point[4];
                collinearPoints[0] = p;
                collinearPoints[1] = q;
                int collinearSize = 2;
                while (j + 1 < pointArray.length && currentSlope == p.slopeTo(pointArray[j + 1])) {
                    collinearPoints[collinearSize++] = pointArray[j + 1];
                    collinearPoints = updateArraySize(collinearPoints, collinearSize);
                    j++;
                }
                if (collinearSize > 3) {
                    Point[] endpoints = orderedSegment(collinearPoints, collinearSize);
                    if (!alreadyIncluded(allEndpoints, numberOfEndpoints, endpoints)) {
                        tempSegments[numberOfSegments++] = new LineSegment(endpoints[0], endpoints[1]);
                        allEndpoints[numberOfEndpoints++] = endpoints[0];
                        allEndpoints[numberOfEndpoints++] = endpoints[1];
                        tempSegments = updateArraySize(tempSegments, numberOfSegments);
                        allEndpoints = updateArraySize(allEndpoints, numberOfEndpoints);
                    } else {
                        j++;
                    }
                } else {
                    j++;
                }
            }
        }
        segments = new LineSegment[numberOfSegments];
        for (int k = 0; k < numberOfSegments; k++) {
            segments[k] = tempSegments[k];
        }
    }
    public int numberOfSegments() {
        return numberOfSegments;
    }

    public LineSegment[] segments() {
        return segments.clone();
    }
    private Point[] updateArraySize(Point[] array, int size) {
        if (size + 1 > array.length) {
            Point[] newArray = new Point[2*size];
            for (int i = 0; i < array.length; i++) {
                newArray[i] = array[i];
            }
            return newArray;
        }
        return array;
    }
    private LineSegment[] updateArraySize(LineSegment[] array, int size) {
        if (size + 1 > array.length) {
            LineSegment[] newArray = new LineSegment[2*size];
            for (int i = 0; i < array.length; i++) {
                newArray[i] = array[i];
            }
            return newArray;
        }
        return array;
    }
    private Point[] orderedSegment(Point[] points, int size) {
        Point max = new Point(0, 0);
        Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (int i = 0; i < size; i++) {
            Point point = points[i];
            if (point.compareTo(max) > 0) {
                max = point;
            }
            if (point.compareTo(min) < 0) {
                min = point;
            }
        }
        return new Point[]{min, max};
    }
    private boolean alreadyIncluded(Point[] allEndpoints, int size, Point[] endpoints) {
        for (int i = 0; i + 1 < size; i = i + 2) {
            Point start = allEndpoints[i];
            Point end = allEndpoints[i + 1];
            boolean equalStarts = start.compareTo(endpoints[0]) == 0;
            boolean equalEnds = end.compareTo(endpoints[1]) == 0;
            boolean sameSlope = start.slopeTo(end) == endpoints[0].slopeTo(endpoints[1]);
            if ((equalStarts || equalEnds) && sameSlope) {
                return true;
            }
        }
        return false;
    }
    private void checkArrayValidity(Point[] points) {
        if (points == null) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < points.length; i++) {
            Point p1 = points[i];
            if (p1 == null) {
                throw new java.lang.IllegalArgumentException();
            }
            for (int j = i + 1; j < points.length; j++) {
                Point p2 = points[j];
                if (p2 == null || p1.compareTo(p2) == 0) {
                    throw new java.lang.IllegalArgumentException();
                }
            }
        }
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}
