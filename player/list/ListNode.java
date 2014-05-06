//  player/list/ListNode.java

package player.list;

public class ListNode<T> {
    public <T>item;
    protected ListNode prev;
    protected ListNode next;
    public int generation;

    /**
     *  ListNode() constructor.
     *  @param i the item to store in the node.
     *  @param p the node previous to this node.
     *  @param n the node following this node.
     */
        ListNode(<T> i, ListNode p, ListNode n, int gen) {
        item = i;
        prev = p;
        next = n;
        generation = gen;
    }
}

