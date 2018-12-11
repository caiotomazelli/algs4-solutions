/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Arrays;
import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        // Degenerated case
        if (compareTo(that) == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        // Horizontal case
        if (y - that.y == 0) {
            return 0.0;
        }
        // Vertical case
        if (x - that.x == 0) {
            return Double.POSITIVE_INFINITY;
        }
        // General case
        return (y - that.y) / (double) (x - that.x); // OK following IEEE 754 floating-point standard
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return (p1, p2) -> {
            final double slope1 = slopeTo(p1);
            final double slope2 = slopeTo(p2);
            // Degenerated collinear case
            if (slope1 == Double.NEGATIVE_INFINITY && slope2 == Double.NEGATIVE_INFINITY) {
                return 0;
            }
            // Vertical collinear case
            if (slope1 == Double.POSITIVE_INFINITY && slope2 == Double.POSITIVE_INFINITY) {
                return 0;
            }
            // General Collinear case
            if (slope1 - slope2 == 0.0) {
                return 0;
            } else {
                return slope1 - slope2 > 0 ? 1 : -1;
            }
        };
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        if (y - that.y < 0) {
            return -1;
        } else if (y - that.y > 0) {
            return 1;
        } else {
            return Integer.compare(x, that.x);
        }
    }


    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        Point base = new Point(0, 0);
        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(3, 4);
        Point p4 = new Point(3, 4);
        Point p5 = new Point(1, 6);
        Point[] points = {p5, p2, p5, p3, base, p4, p1};

        printWhenFail(p1.compareTo(p2) < 0, "compareTo check 1 fail");
        printWhenFail(base.slopeTo(p1) == 1.0, "slopeTo check 1 fail");
        printWhenFail(p1.slopeTo(p2) == 1.0, "slopeTo check 2 fail");
        printWhenFail(p1.slopeTo(p5) == Double.POSITIVE_INFINITY, "slopeTo check pos inf fail");
        printWhenFail(base.slopeTo(p1) == base.slopeTo(p2), "slopeTo check collinear fail");
        printWhenFail(base.slopeTo(p1) != base.slopeTo(p3), "slopeTo check not collinear fail");

        Arrays.sort(points, base.slopeOrder());

        printWhenFail(points[0] == base, "slopeOrder check 1 fail");
        printWhenFail(points[5] == p5, "slopeOrder check 2 fail");
        printWhenFail(points[6] == p5, "slopeOrder check 3 fail");

        System.out.println("Checking finished");
    }

    private static void printWhenFail(boolean statement, String message) {
        if (!statement) {
            System.out.println(message);
        }
    }
}
