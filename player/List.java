//  player/list/List.java
//package player.list;

//TODO: move the list files into their own directory
//      I was doing this but it was not compiling

package player;
import java.util.Iterator;

/**
 *  A List is a mutable doubly-linked list ADT.  Its implementation is
 *  circularly-linked and employs a sentinel node at the head
 *  of the list.
 */

public class List<T> implements Iterable<T>{
    /**
     *  head references the sentinel node.
     *  size is the number of items in the list.  (The sentinel node does not
     *       store an item.)
     */

    protected ListNode<T> head;
    protected int size;

    /* List invariants:
     *  1)  head != null.
     *  2)  For any ListNode x in a List, x.next != null.
     *  3)  For any ListNode x in a List, x.prev != null.
     *  4)  For any ListNode x in a List, if x.next == y, then y.prev == x.
     *  5)  For any ListNode x in a List, if x.prev == y, then y.next == x.
     *  6)  size is the number of ListNodes, NOT COUNTING the sentinel,
     *      that can be accessed from the sentinel (head) by a sequence of
     *      "next" references.
     */

    /**
     *  newNode() calls the DListNode constructor.f
     *  @param item the item to store in the node.
     *  @param prev the node previous to this node.
     *  @param next the node following this node.
     */
    protected ListNode<T> newNode(T item, ListNode<T> prev, ListNode<T> next) {
        return new ListNode<T>(item, prev, next);
    }

    /**
     *  List() constructor for an empty List.
     */
    public List() {
        head = newNode(null, null, null);
        head.next = head.prev = head;
        size = 0;
    }

    /**
     *  isEmpty() returns true if this List is empty, false otherwise.
     *  @return true if this List is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *  length() returns the length of this List.
     *  @return the length of this List.
     */
    public int length() {
        return size;
    }

    //get the item at index I
    public T get(int i){ //TODO: test
        int c = 0;
        ListNode<T> curr = head.next;

        if (i < 0 || i >= size){
            System.out.println("Error - List.get: Invalid index");
            //TODO: raise error
        }

        while (c < i){
            curr = curr.next;
            c++;
        }
        return curr.item;
    }


    /**
     *  insertFront() inserts an item at the front of this List.
     *  @param item is the item to be inserted.
     */
    public void insertFront(T item) {
        ListNode<T> node = newNode(item, head, head.next);
        node.next.prev = node;
        head.next = node;
        size++;
    }
    public void push (T item){
        insertFront(item);
    }

    /**
     *  insertBack() inserts an item at the back of this List.
     *  @param item is the item to be inserted.
     */
    public void insertBack(T item) {
        ListNode<T> node = newNode(item, head.prev, head);
        head.prev.next = node;
        head.prev = node;
        size++;
    }
    public void append(T item){
        insertBack(item);
    }

    //pop an item of the front of the list
    public T pop (){ //TODO: test
        if (head.next == head){
            return null;
        }
        T ret = head.next.item;
        head.next = head.next.next;
        return ret;
    }
    //pop an item of the back of the list
    public T popBack(){ //TODO: test
        if (head.next == head){
            return null;
        }
        T ret = head.prev.item;
        head.prev = head.prev.prev;
        return ret;
    }

    /**
     *  front() returns the node at the front of this List.  If the List is
     *  empty, return null.
     *
     *  @return the node at the front of this List.
     */
    public ListNode<T> front() {
        if (head.next == head){
            return null;
        }
        return head.next;
    }

    /**
     *  back() returns the node at the back of this List.  If the List is
     *  empty, return null.
     *
     *  @return the node at the back of this List.
     */
    public ListNode<T> back() {
        if (head.prev == head){
            return null;
        }
        return head.prev;
    }

    /**
     *  next() returns the node following "node" in this List.  If "node" is
     *  null, or "node" is the last node in this List, return null.
     *
     *  @param node the node whose successor is sought.
     *  @return the node following "node".
     */
    public ListNode<T> next(ListNode<T> node) {
        if (node == null || node.next == head){
            return null;
        }
        return node.next;
    }

    /**
     *  prev() returns the node prior to "node" in this List.  If "node" is
     *  null, or "node" is the first node in this List, return null.
     *
     *  @param node the node whose predecessor is sought.
     *  @return the node prior to "node".
     */
    public ListNode<T> prev(ListNode<T> node) {
        if (node == null || node.prev == head){
            return null;
        }
        return node.prev;
    }

    /**
     *  insertAfter() inserts an item in this List immediately following "node".
     *  If "node" is null, do nothing.
     *  @param item the item to be inserted.
     *  @param node the node to insert the item after.
     */
    public void insertAfter(T item, ListNode<T> node) {
        if (node != null){
            ListNode<T> nNode = newNode(item, node, node.next);
            nNode.prev.next = nNode;
            nNode.next.prev = nNode;
            size++;
        }
    }

    /**
     *  insertBefore() inserts an item in this List immediately before "node".
     *  If "node" is null, do nothing.
     *  @param item the item to be inserted.
     *  @param node the node to insert the item before.
     */
    public void insertBefore(T item, ListNode<T> node) {
        if (node != null){
            ListNode<T> nNode = newNode(item, node.prev, node);
            nNode.next.prev = nNode;
            nNode.prev.next = nNode;
            size++;
        }
    }

    /**
     *  remove() removes "node" from this List.  If "node" is null, do nothing.
     */
    public void remove(ListNode<T> node){
        if (node != null){
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.next = node.prev = null;
            size--;
        }
    }

    /**
     *  iterator() returns a newly created ListIterator that can iterate through
     *  its items.
     *
     *  @return a newly created ListIterator object set to the first run of this
     *  List.
     */
    public ListIterator<T> iterator() {
        return new ListIterator<T>(this);
    }


    /**
     *  toString() returns a String representation of this List.
     *
     *  @return a String representation of this List.
     */
    public String toString() {
        String result = "[  ";
        ListNode<T> current = head.next;
        while (current != head) {
            result = result + current.item.toString() + "  ";
            current = current.next;
        }
        return result + "]";
    }

    // public boolean check_invariants(){

    //     // 1
    //     if (head == null){
    //         System.out.println("ERROR: head == null");
    //         return false;
    //     }

    //     ListNode<T> node = head.next;

    //     // 6
    //     if (node == head && size != 0){
    //         System.out.println("ERROR: list is empty but 'size' = " + size);
    //         return false;
    //     }

    //     int count = 0;
    //     T lastItem;

    //     while (node != head ){
    //         count++;
    //         lastItem = node.item;
    //         // 2
    //         if (node.next == null){
    //             System.out.println("ERROR: node.next == null");
    //             return false;
    //         }
    //         // 3
    //         if (node.prev == null){
    //             System.out.println("ERROR: node.prev == null");
    //             return false;
    //         }

    //         // 4
    //         if (node.next.prev != node){
    //             System.out.print("ERROR: node.next.prev != node. node.item = ");
    //             System.out.println(node.item);
    //             return false;
    //         }

    //         // 5
    //         if (node.prev.next != node){
    //             System.out.println("ERROR: node.prev.next != node. node.item = ");
    //             System.out.println(node.item);
    //             return false;
    //         }

    //         node = node.next;

    //     }

    //     // 6
    //     if (count != size){
    //         System.out.println("ERROR: invalid size. Counted "+count+", expected " + size);
    //         return false;
    //     }
    //     System.out.println("OK");
    //     return true;
    // }
}
