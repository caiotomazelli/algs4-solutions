import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;
    private int size;

    private static class Node {
        private final Point2D p;      // the point
        private final RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    public KdTree() { // construct an empty set of points
        root = null;
    }
    public boolean isEmpty() { // is the set empty?
        return size() == 0;
    }
    public int size() { // number of points in the set
        return size;
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        root = insert(root, p, 0, 0, 1, 1, false);
    }

    private Node insert(Node node, Point2D p, double xmin, double ymin, double xmax, double ymax, boolean verticalInsert) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException("calls insert() with a null point");
        }
        if (node == null) {
            size++;
            return new Node(p, new RectHV(xmin, ymin, xmax, ymax));
        }
        if (p.equals(node.p)) return node;
        if (verticalInsert) {
            if (p.y() < node.p.y()) {
                node.lb = insert(node.lb, p, node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y(), !verticalInsert);
            }
            else {
                node.rt = insert(node.rt, p, node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax(), !verticalInsert);
            }
        }
        else {
            if (p.x() < node.p.x()) {
                node.lb = insert(node.lb, p, node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax(), !verticalInsert);
            }
            else {
                node.rt = insert(node.rt, p, node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax(), !verticalInsert);
            }
        }
        return node;
    }
    public boolean contains(Point2D p) { // does the set contain point p?
        return contains(root, p, false);
    }

    private boolean contains(Node node, Point2D p, boolean verticalSearch) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException("calls insert() with a null point");
        }
        if (node == null) {
            return false;
        }
        if (node.p.compareTo(p) == 0) return true;
        if (verticalSearch) {
            if (p.y() < node.p.y()) {
                return contains(node.lb, p, !verticalSearch);
            }
            else {
                return contains(node.rt, p, !verticalSearch);
            }
        }
        else {
            if (p.x() < node.p.x()) {
                return contains(node.lb, p, !verticalSearch);
            }
            else {
                return contains(node.rt, p, !verticalSearch);
            }
        }
    }
    public void draw() { // draw all points to standard draw
        draw(root, true);
    }

    private void draw(Node node, boolean isVertical) {
        if (node.p != null) {
            draw(node.p, node.rect, isVertical);
        }
        if (node.lb != null) {
            draw(node.lb, !isVertical);
        }
        if (node.rt != null) {
            draw(node.rt, !isVertical);
        }
    }

    private void draw(Point2D p, RectHV rect, boolean isVertical) {
        StdDraw.setPenRadius(0.03);
        StdDraw.setPenColor();
        p.draw();
        StdDraw.setPenRadius();
        if (isVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            Point2D min = new Point2D(p.x(), rect.ymin());
            Point2D max = new Point2D(p.x(), rect.ymax());
            min.drawTo(max);
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            Point2D min = new Point2D(rect.xmin(), p.y());
            Point2D max = new Point2D(rect.xmax(), p.y());
            min.drawTo(max);
        }
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        Bag<Point2D> pointsInRange = new Bag<>();
        range(root, pointsInRange, rect);
        return pointsInRange;
    }

    private void range(Node node, Bag<Point2D> pointsInRange, RectHV rect) {
        if (node == null) {
            return;
        }
        if (node.p != null && rect.contains(node.p)) {
            pointsInRange.add(node.p);
        }
        Node[] nextNodes = {node.lb, node.rt};
        for (Node n: nextNodes) {
            if (n != null && rect.intersects(n.rect)) {
                range(n, pointsInRange, rect);
            }
        }
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        return nearest(root, p, null);
    }

    private Point2D nearest(Node node, Point2D p, Point2D nearestPoint) {
        if (node == null) {
            return nearestPoint;
        }
        if (nearestPoint == null) {
            nearestPoint = node.p;
        }
        if (p.distanceSquaredTo(node.p) <= p.distanceSquaredTo(nearestPoint)) {
            nearestPoint = node.p;
        }
        Node[] orderedNodes;
        if (node.rt != null && node.rt.rect.contains(p)) {
            orderedNodes = new Node[]{node.rt, node.lb};
        }
        else if (node.lb != null && node.lb.rect.contains(p)) {
            orderedNodes = new Node[]{node.lb, node.rt};
        }
        else {
            orderedNodes = new Node[2];
            if (node.rt != null) {
                orderedNodes[0] = node.rt;
            }
            if (node.lb != null) {
                orderedNodes[1] = node.lb;
            }
        }
        for (Node n: orderedNodes) {
            if (n != null) {
                Point2D nodeNearest = findNodeNearest(n, p, nearestPoint);
                if (p.distanceSquaredTo(nodeNearest) <= p.distanceSquaredTo(nearestPoint)) {
                    nearestPoint = nodeNearest;
                }
            }
        }
        return nearestPoint;
    }

    private Point2D findNodeNearest(Node node, Point2D p, Point2D nearestPoint) {
        if (node.rect.distanceSquaredTo(p) <= p.distanceSquaredTo(nearestPoint)) {
            Point2D nodeNearest = nearest(node, p, nearestPoint);
            if (p.distanceSquaredTo(nodeNearest) <= p.distanceSquaredTo(nearestPoint)) {
                return nodeNearest;
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args) { // unit testing of the methods (optional)
        KdTree set = new KdTree();
        set.insert(new Point2D(0.1, 0.2));
        set.insert(new Point2D(0.1, 0.2));
        System.out.println("Size should be 1. Actual size == " + set.size());
        System.out.println("Set should contain (0.1,0.2). Actual contains() returns: " + set.contains(new Point2D(0.1, 0.2)));
        set.insert(new Point2D(0.3, 0.4));
        set.insert(new Point2D(0.3, 0.7));
        set.insert(new Point2D(0.7, 0.4));
        System.out.println("Size should be 4. Actual size == " + set.size());
        System.out.println("Set should contain (0.3,0.7). Actual contains() returns: " + set.contains(new Point2D(0.3, 0.7)));
        System.out.println("Set should not contain (0.3,0.8). Actual contains() returns: " + set.contains(new Point2D(0.3, 0.8)));
        System.out.println("Set should contain (0.7,0.4). Actual contains() returns: " + set.contains(new Point2D(0.7, 0.4)));


        KdTree drawSet = new KdTree();
        drawSet.insert(new Point2D(0.7, 0.2));
        drawSet.insert(new Point2D(0.5, 0.4));
        drawSet.insert(new Point2D(0.2, 0.3));
        drawSet.insert(new Point2D(0.4, 0.7));
        drawSet.insert(new Point2D(0.9, 0.6));
        drawSet.draw();
    }
}
