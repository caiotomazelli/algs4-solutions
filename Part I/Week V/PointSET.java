import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private final SET<Point2D> set;

    public PointSET() { // construct an empty set of points
        set = new SET<>();
    }
    public boolean isEmpty() { // is the set empty?
        return set.isEmpty();
    }
    public int size() { // number of points in the set
        return set.size();
    }
    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        set.add(p);
    }
    public boolean contains(Point2D p) { // does the set contain point p?
        return set.contains(p);
    }
    public void draw() { // draw all points to standard draw
        for (Point2D p: set) {
            p.draw();
        }
    }
    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        SET<Point2D> inRange = new SET<>();
        for (Point2D p: set) {
            if (rect.contains(p)) {
                inRange.add(p);
            }
        }
        return inRange;
    }
    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        Point2D nearest = null;
        double nearestSquaredDistance = Double.POSITIVE_INFINITY;
        for (Point2D q: set) {
            double dist = q.distanceSquaredTo(p);
            if (dist < nearestSquaredDistance) {
                nearest = q;
                nearestSquaredDistance = dist;
            }
        }
        return nearest;
    }

    public static void main(String[] args) { // unit testing of the methods (optional)

    }
}
