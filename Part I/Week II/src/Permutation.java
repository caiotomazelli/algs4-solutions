import edu.princeton.cs.algs4.StdIn;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            queue.enqueue(StdIn.readString());
            if (queue.size() == k) {
                break;
            }
        }
        for (String str: queue) {
            if (k > 0) {
                System.out.println(str);
                k--;
            } else {
                break;
            }
        }
    }
}