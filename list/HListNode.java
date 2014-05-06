/* HListNode.java */
package cs61bayn.list;
import cs61bayn.dict.Entry;
/**
 *  A HListNode is a node in a HList (doubly-linked list).
 */
public class HListNode {
    /**
     *  item references the item stored in the current node.
     *  prev references the previous node in the HList.
     *  next references the next node in the HList.
     *
     *  DO NOT CHANGE THE FOLLOWING FIELD DECLARATIONS.
     */

    public Entry entry;
    protected HListNode prev;
    protected HListNode next;

    /**
     *  HListNode() constructor.
     *  @param i the item to store in the node.
     *  @param p the node previous to this node.
     *  @param n the node following this node.
     */
    HListNode(Entry e, HListNode p, HListNode n) {
        entry = e;
        prev = p;
        next = n;
    }
}

