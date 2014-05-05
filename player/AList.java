package player;
import java.util.Iterator;

// Array List

public class AList<T> implements Iterable<T>{
    protected T[] array;
    protected int len, maxSize;

    //we have Java's retardation to thank for this
    @SuppressWarnings("unchecked")
    public AList(int n){
        // prevent generic array creation error
        array = (T[]) new Object[n];

        maxSize = n;
        len = 0;
    }
    public int length(){
        return len;
    }
    public boolean empty(){
        return len == 0;
    }
    public AListIterator<T> iterator(){
        return new AListIterator<T>(array, len);
    }

    public void add(T item){
        if (len < maxSize){
            array[len] = item;
            len++;
        }else{
            //TODO: throw error (or resize)
            System.out.println("Error -- AList.add: overflow");
        }
    }
    public T get(int index){
        //if (index >= 0 && index < len){
        if (index >= 0 && index < maxSize){

            return array[index];
        }
        //TODO: throw invalid index error
        System.out.println("Error -- AList.get: Invalid index: "+index);
        return null;
    }

    public void set(int index, T value){
        if (index >= 0 && index < maxSize){
            array[index] = value;
        }else{
            //TODO: throw invalid index error
            System.out.println("Error -- AList.get: Invalid index: "+index);
        }
    }
    //remove the last item added and return it
    public T pop(){
        len--;
        T item = array[len];
        //array[len] = null;//don't help the GC. speed > memory
        return item;
    }
    public void clear(){
        len=0;
    }

    public String toString(){
        if (len == 0){
            return "<Empty AList>";
        }
        String ret = "<AList: " + array[0];
        for (int i = 1; i < len; i++){
            ret +=  (", " + array[i]);
        }
        return ret + ">";
    }
    //order this list by sorting the numbers in VALUES
    public void sorted(long[] values){
        sortMoves(values, 0, len-1);
    }

    private void sortMoves(long[] moveScores, int low, int high){
        if (low < high) {
            int pivotIndex = low; //TODO: choose better pivot
            long pivot = moveScores[pivotIndex];
            T mPivot = array[pivotIndex];
            moveScores[pivotIndex] = moveScores[high];
            array[pivotIndex] = array[high];
            moveScores[high] = pivot;
            array[high] = mPivot;

            int i = low - 1;
            int j = high;
            long tmp;
            T mTmp;
            do{
                do {
                    i++;
                }while (moveScores[i] < pivot);
                do {
                    j--;
                } while ((moveScores[j] > pivot) && (j > low));

                if (i < j){
                    tmp = moveScores[i];
                    mTmp = array[i];
                    moveScores[i] = moveScores[j];
                    array[i] = array[j];
                    moveScores[j] = tmp;
                    array[j] = mTmp;
                }
            } while (i < j);

            array[high] = array[i];
            moveScores[high] = moveScores[i];

            array[i] = mPivot;
            moveScores[i] = pivot;
            sortMoves(moveScores, low, i - 1);
            sortMoves(moveScores, i + 1, high);
        }
    }


}
