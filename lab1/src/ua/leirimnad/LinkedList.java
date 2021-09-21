package ua.leirimnad;

class LinkedList <T> implements PriorityQueue <T> {
    private PriorityNode<T> head;
    private PriorityNode<T> gear;

    @Override
    public void push(T elem, int priority) {

        PriorityNode<T> newNode = new PriorityNode<>(elem, priority);

        if (head == null) {
            head = gear = newNode;
        } else if(head.priority < priority){
            newNode.next = head;
            head = newNode;
        } else {
            PriorityNode<T> cur = head.next;
            PriorityNode<T> prev = head;
            while (cur != null && cur.priority >= priority){
                prev = cur;
                cur = cur.next;
            }
            prev.next = newNode;
            newNode.next = cur;
            if(prev == gear) gear = newNode;
        }
    }

    @Override
    public T pop() {
        if (head == null) return null;
        T ret = head.data;
        head = head.next;
        return ret;
    }

    @Override
    public T top() {
        if(head == null) return null;
        return head.data;
    }
}
