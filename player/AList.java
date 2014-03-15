package player;
import java.util.Iterator;

// Array List
public class AList<T> implements Iterable<T>{
    protected T[] array;
    private int len, maxSize;

    public AList(int n){
        //TODO:
        //array = new T[n]; //error: generic array creation
        maxSize = n;
        len = 0;
    }
    public int length(){
        return len;
    }
    public boolean isEmpty(){
        return len == 0;
    }
    public AListIterator<T> iterator(){
        return new AListIterator<T>(array, len);
    }

    public void add(T item){
        if (len < maxSize){
            array[len] = item;
            len++;
        }
        //TODO: throw error (or resize)
        System.out.println("Error -- AList.add: overflow");
    }
    public T get(int index){
        if (index >= 0 && index < len){
            return array[index];
        }
        //TODO: throw invalid index error
        System.out.println("Error -- AList.get: Invalid index");
        return null;
    }
    //remove the last item added and return it
    public T pop(){
        len--;
        T item = array[len];
        array[len] = null;//help the GC
        return item;
    }
}

