package uk.ac.standrews.cs.digitising_scotland.util.MTree.experiments.euclidean;

public class Point {

    public float x;
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    private final static float epsilon = 0.00000000001F;

    public String toString() {
        return "[" + x + "," + y + "]";
    }

    public boolean equals(Object o) {

        if (o instanceof Point) {
            Point p = (Point) o;
            return this.x == p.x && this.y == p.y;
            // return (Math.abs(this.x - p.x) < epsilon) && (Math.abs(this.y - p.y) < epsilon);
        } else return false;
    }
}
