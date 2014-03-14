//  player/list/ListNode.java

//package player.list;
package player;


public class ListNode<T> {
    public T item;
    protected ListNode<T> prev;
    protected ListNode<T> next;

    /**
     *  ListNode() constructor.
     *  @param i the item to store in the node.
     *  @param p the node previous to this node.
     *  @param n the node following this node.
     */
    ListNode(T i, ListNode<T> p, ListNode<T> n) {
        item = i;
        prev = p;
        next = n;
    }
}

