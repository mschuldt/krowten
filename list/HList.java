/* HList.java

   javac -g ../list/*.java
   java list.HList

*/
package list;
import dict.Entry;
/**
 *  A HList is a mutable doubly-linked list ADT.  Its implementation is
 *  circularly-linked and employs a sentinel (dummy) node at the head
 *  of the list.
 */
public class HList {
    /**
     *  head references the sentinel node.
     *  size is the number of items in the list.  (The sentinel node does not
     *       store an item.)
     */
    protected HListNode head;
    protected int size;

        /**
     *  newNode() calls the HListNode constructor.  Use this class to allocate
     *  new HListNodes rather than calling the HListNode constructor directly.
     *  That way, only this method needs to be overridden if a subclass of HList
     *  wants to use a different kind of node.
     *  @param entry the entry to store in the node.
     *  @param prev the node previous to this node.
     *  @param next the node following this node.
     */
    protected HListNode newNode(Entry entry, HListNode prev, HListNode next) {
        return new HListNode(entry, prev, next);
    }

    /**
     *  HList() constructor for an empty HList.
     */
    public HList() {
        head = newNode(null, null, null);
        head.next = head.prev = head;
        size = 0;
    }

    /**
     *  isEmpty() returns true if this HList is empty, false otherwise.
     *  @return true if this HList is empty, false otherwise.
     *  Performance:  runs in O(1) time.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *  length() returns the length of this HList.
     *  @return the length of this HList.
     *  Performance:  runs in O(1) time.
     */
    public int length() {
        return size;
    }

    /**
     *  add() inserts an entry at the front of this HList.
     *  @param entry is the entry to be inserted.
     *  Performance:  runs in O(1) time.
     */
    public void add(Entry entry) {
        HListNode node = newNode(entry, head, head.next);
        node.next.prev = node;
        head.next = node;
        size++;
    }

    /**
     * return the node whose entry matches KEY
     * if none are found, return null
     * Performance:  runs in O(awesome) time.
     */
    public HListNode find(long ourBoard, long oppBoard){
        HListNode node = head.next;
        while (node != head){
            if (node.entry.ourBitBoard == ourBoard
                && node.entry.opponentBitBoard == oppBoard){
                return node;
            }
            node = node.next;
        }
        return null;
    }

    /**
     *  front() returns the node at the front of this HList.  If the HList is
     *  empty, return null.
     *
     *  Do NOT return the sentinel under any circumstances!
     *
     *  @return the node at the front of this HList.
     *  Performance:  runs in O(1) time.
     */
    public HListNode front() {
        if (head.next == head){
            return null;
        }
        return head.next;
    }

    /**
     *  back() returns the node at the back of this HList.  If the HList is
     *  empty, return null.
     *
     *  Do NOT return the sentinel under any circumstances!
     *
     *  @return the node at the back of this HList.
     *  Performance:  runs in O(1) time.
     */
    public HListNode back() {
        if (head.prev == head){
            return null;
        }
        return head.prev;
    }

    /**
     *  next() returns the node following "node" in this HList.  If "node" is
     *  null, or "node" is the last node in this HList, return null.
     *
     *  Do NOT return the sentinel under any circumstances!
     *
     *  @param node the node whose successor is sought.
     *  @return the node following "node".
     *  Performance:  runs in O(1) time.
     */
    public HListNode next(HListNode node) {
        if (node == null || node.next == head){
            return null;
        }
        return node.next;
    }

    /**
     *  prev() returns the node prior to "node" in this HList.  If "node" is
     *  null, or "node" is the first node in this HList, return null.
     *
     *  Do NOT return the sentinel under any circumstances!
     *
     *  @param node the node whose predecessor is sought.
     *  @return the node prior to "node".
     *  Performance:  runs in O(1) time.
     */
    public HListNode prev(HListNode node) {
        if (node == null || node.prev == head){
            return null;
        }
        return node.prev;
    }

    /**
     *  remove() removes "node" from this HList.  If "node" is null, do nothing.
     *  Performance:  runs in O(1) time.
     */
    public Entry remove(HListNode node){
        //Entry ret = node.entry;
        if (node != null){
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.next = node.prev = null;
            size--;
            return node.entry;
        }
        return null;
    }

    // /**
    //    Remove the node whose entries key matches KEY
    //  */
    // public Entry removeEntry(Object key){
    //     return remove(find(key));
    //}


    /**
     *  toString() returns a String representation of this HList.
     *
     *  DO NOT CHANGE THIS METHOD.
     *
     *  @return a String representation of this HList.
     *  Performance:  runs in O(n) time, where n is the length of the list.
     */
    public String toString() {
        String result = "[  ";
        HListNode current = head.next;
        while (current != head) {
            result = result + current.entry + "  ";
            current = current.next;
        }
        return result + "]";
    }

    /**
       Delete all all nodes from this list
     */
    public void clear(){
        head.next = head;
        size = 0;
    }

    public boolean check_invariants(){

        // 1
        if (head == null){
            System.out.println("ERROR: head == null");
            return false;
        }

        HListNode node = head.next;

        // 6
        if (node == head && size != 0){
            System.out.println("ERROR: list is empty but 'size' = " + size);
            return false;
        }

        int count = 0;
        Entry lastEntry;
        //doing: fix: if the list has one node, this will note increment the couter
        while (node != head ){
            count++;
            lastEntry = node.entry;
            // 2
            if (node.next == null){
                System.out.println("ERROR: node.next == null");
                return false;
            }
            // 3
            if (node.prev == null){
                System.out.println("ERROR: node.prev == null");
                return false;
            }

            // 4
            if (node.next.prev != node){
                System.out.print("ERROR: node.next.prev != node. node.entry = ");
                System.out.println(node.entry);
                return false;
            }

            // 5
            if (node.prev.next != node){
                System.out.println("ERROR: node.prev.next != node. node.entry = ");
                System.out.println(node.entry);
                return false;
            }

            node = node.next;

        }

        // 6
        if (count != size){
            System.out.println("ERROR: invalid size. Counted "+count+", expected " + size);
            return false;
        }
        System.out.println("OK");
        return true;
    }



    public static void main(String[] args){
        HList l = new HList();
    }
}
