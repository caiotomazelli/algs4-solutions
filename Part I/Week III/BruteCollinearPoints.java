import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private final LineSegment[] segments;
    private int numberOfSegments = 0;

    public BruteCollinearPoints(Point[] points) {
        checkArrayValidity(points);
        LineSegment[] tempSegments = new LineSegment[10];
        Point p1, p2, p3, p4;
        for (int i = 0; i < points.length; i++) {
            p1 = points[i];
            for (int j = i+1; j < points.length; j++) {
                p2 = points[j];
                for (int k = j+1; k < points.length; k++) {
                    p3 = points[k];
                    if (p1.slopeTo(p2) != p1.slopeTo(p3)) {
                        continue;
                    }
                    for (int m = k+1; m < points.length; m++) {
                        p4 = points[m];
                        if (p1.slopeTo(p3) == p1.slopeTo(p4)) {
                            Point[] endpoints = orderedSegment(new Point[]{p1, p2, p3, p4});
                            tempSegments[numberOfSegments++] = new LineSegment(endpoints[1], endpoints[0]);
                            tempSegments = updateArraySize(tempSegments, numberOfSegments);
                        }
                    }
                }
            }
        }
        segments = new LineSegment[numberOfSegments];
        for (int i = 0; i < numberOfSegments; i++) {
            segments[i] = tempSegments[i];
        }
    }
    public int numberOfSegments() {
        return numberOfSegments;
    }
    public LineSegment[] segments() {
        return segments.clone();
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

    private LineSegment[] updateArraySize(LineSegment[] segs, int nSegs) {
        if (nSegs + 1 > segs.length) {
            LineSegment[] newSegments = new LineSegment[2*nSegs];
            for (int i = 0; i < segs.length; i++) {
                newSegments[i] = segs[i];
            }
            return newSegments;
        }
        return segs;
    }
    private Point[] orderedSegment(Point[] points) {
        Point max = new Point(0, 0);
        Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (Point point : points) {
            if (point.compareTo(max) > 0) {
                max = point;
            }
            if (point.compareTo(min) < 0) {
                min = point;
            }
        }
        return new Point[]{min, max};
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
