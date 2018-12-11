import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {
    private Node first;
    private Node last;
    private int size;
    
    private class Node {
        final Item item;
        Node next;
        Node previous;

        Node(Item item) {
            this.item = item;
        }
    }
    
    public Deque() {
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        Node node = new Node(item);
        if (size == 0) {
            first = node;
            last = node;
        } else {
            node.next = first;
            first.previous = node;
            first = node;
        }
        size++;
    }

    public void addLast(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        Node node = new Node(item);
        if (size == 0) {
            first = node;
            last = node;
        } else {
            node.previous = last;
            last.next = node;
            last = node;
        }
        size++;
    }

    public Item removeFirst() {
        if (size == 0) {
            throw new java.util.NoSuchElementException();
        }
        Item firstItem = first.item;
        if (size == 1) {
            first = null;
            last = null;
        } else {
            first = first.next;
        }
        size--;
        return firstItem;
    }

    public Item removeLast() {
        if (size == 0) {
            throw new java.util.NoSuchElementException();
        }
        Item lastItem = last.item;
        if (size == 1) {
            first = null;
            last = null;
        } else {
            last = last.previous;
        }
        size--;
        return lastItem;
    }

    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            Node current = first;
            @Override
            public boolean hasNext() {
                return current != null;
            }
            @Override
            public Item next() {
                if (hasNext()) {
                    Item item = current.item;
                    current = current.next;
                    return item;
                } else throw new java.util.NoSuchElementException();
            }
            @Override
            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        };
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(1);
        testAndPrint(deque.removeFirst() == 1,
                "Not 1: removeFirst fails");
        testAndPrint(deque.size() == 0,
                "Not 0: size fails");
        deque.addLast(5);
        deque.addLast(10);
        deque.addLast(15);
        testAndPrint(deque.removeLast() == 15,
                "Not 15: removeLast fails");
        testAndPrint(deque.removeFirst() == 5,
                "Not 5: removeFirst fails");
        deque.addFirst(3);
        deque.addLast(13);
        int i = 0;
        testAndPrint(deque.size() == 3,
                "Not 3: size fails: " + deque.size());        
        for (Integer item: deque) {
            if (i == 0) {
                System.out.println("Item on pos 0: " + item);
                testAndPrint(item == 3, "Not 3: iterator fails: " + item);
            } else if (i == 1) {
                System.out.println("Item on pos 1: " + item);
                testAndPrint(item == 10, "Not 10: iterator fails: " + item);
            } else {
                System.out.println("Item on pos 2: " + item);
                testAndPrint(item == 13, "Not 13: iterator fails: " + item);
            }
            i++;
        }
        testAndPrint(false, "Finished testing");
    }

    private static void testAndPrint(boolean statement, String descriptionIfFail) {
        if (!statement) {
            System.out.println(descriptionIfFail);
        }
    }
}
