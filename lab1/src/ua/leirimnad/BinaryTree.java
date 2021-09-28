package ua.leirimnad;

class BinaryTree <T> implements PriorityQueue <T> {

    private static class PriorityBinaryTreeNode<T> extends Node <T> {
        public int priority;
        public PriorityBinaryTreeNode<T> left, right;

        public PriorityBinaryTreeNode(T elem, int priority) {
            super(elem);
            this.priority = priority;
            this.left = this.right = null;
        }
    }


    private PriorityBinaryTreeNode<T> head;

    @Override
    public void push(T elem, int priority) {
        this.head = addRecursive(this.head, elem, priority);
    }

    @Override
    public T pop() {
        if (this.head == null) return null;
        PriorityBinaryTreeNode<T> cur = head;
        PriorityBinaryTreeNode<T> prev = null;
        while (cur.right != null) {
            prev = cur;
            cur = cur.right;
        }

        T ret = cur.data;
        if(prev != null) prev.right = cur.left;
        else this.head = cur.left;

        return ret;
    }

    @Override
    public T top() {
        if (this.head == null) return null;
        PriorityBinaryTreeNode<T> cur = head;
        while (cur.right != null) cur = cur.right;
        return cur.data;
    }

    private PriorityBinaryTreeNode<T> addRecursive(PriorityBinaryTreeNode<T> current, T elem, int priority) {
        if (current == null) return new PriorityBinaryTreeNode<T>(elem, priority);

        if (priority <= current.priority) {
            current.left = addRecursive(current.left, elem, priority);
        } else {
            current.right = addRecursive(current.right, elem, priority);
        }

        return current;
    }
}

