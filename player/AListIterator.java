package player;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AListIterator<T> implements Iterator<T> {
    private T[] array;
    private int index;
    private int length;

    public AListIterator(T[] a, int len){
        array = a;
        length = len;
        index = 0;
    }
    public boolean hasNext(){
        return index < length;
    }

    public T next(){
        if (index >= length){
            throw new NoSuchElementException();
        }
        T val = array[index];
        index++;
        return val;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
