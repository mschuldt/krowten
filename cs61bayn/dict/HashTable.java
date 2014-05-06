/* HashTable.java */
//383
package cs61bayn.dict;

public class HashTable {
    private long [][] array;
    private int numBuckets;
    private int numItems;
    public int collisions;

    private static final int ENTRY_SCORE = 0;
    private static final int ENTRY_OUR_BITBOARD = 1;
    private static final int ENTRY_OPP_BITBOARD = 2;
    private static final int ENTRY_GENERATION = 3;
    private static final int ENTRY_EVALED_BOARDS = 4;
    private static final int ENTRY_DEPTH = 5;

    int max = 0;

    /**
     *  Construct a new empty hash table intended to hold roughly sizeEstimate
     *  entries.
     **/
    public HashTable(int sizeEstimate) {
        numBuckets = getNextPrime((int) (sizeEstimate*1.5));
        array = new long[numBuckets][];
        for (int i =0;i < numBuckets; i++){
            array[i] = new long[6];
        }
        numItems = collisions = 0;
    }

    /** returns the smallest prime >= n
     */
    private int getNextPrime(int n){
        int sq;
        start:
        while (true){
            sq = (int)Math.sqrt(n)+1;
            for (int i=2;i<=sq;i++){
                if (n % i == 0){
                    n++;
                    continue start;
                }
            }
            return n;
        }
    }


    /**
     *  Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
     *  to a value in the range 0...(size of hash table) - 1.
     **/
    int compFunction(long hashCode) {
        int c = ((int)hashCode) % numBuckets;
        return c < 0 ? c + numBuckets : c;
    }

    /**
     *  Returns the number of entries stored in the hash table.
     **/
    public int size() {
        return numItems;
    }

    public long numBuckets(){
        return numBuckets;
    }
    public double loadFactor(){
        return numItems/((double)numBuckets);
    }

    /**
     *  return true if the dictionary is empty.
     **/
    public boolean isEmpty() {
        return numItems == 0;
    }

    /**
     *  Modify the HASHCODE entry to contain SCORE, OURBOARD and OPPBOARD.
     *  update it's generation to GEN. Returns a reference to the entry.
     **/
    public long[] insert(long hashCode, int score, long ourBoard, long oppBoard, long evaledBoards, int depth, int gen) {

        long[] entry = array[compFunction(hashCode)];

        if (entry[ENTRY_GENERATION] == gen){
            collisions++;
        }else{
            numItems++;
        }

        if (entry[ENTRY_DEPTH] > depth
            && entry[ENTRY_GENERATION] == gen){
            return entry;
        }

        entry[ENTRY_GENERATION] = gen;
        entry[ENTRY_SCORE] = score;
        entry[ENTRY_OUR_BITBOARD] = ourBoard;
        entry[ENTRY_OPP_BITBOARD] = oppBoard;
        entry[ENTRY_EVALED_BOARDS] = evaledBoards;
        entry[ENTRY_DEPTH] = depth;

        return entry;
    }

    /**
     *  Search for an entry with the specified key.  If such an entry is found,
     *  return it; otherwise return null.
     **/
    public long[] find(long hashCode, long ourBoard, long oppBoard, int gen){
        long[] entry = array[compFunction(hashCode)];
        if (entry[ENTRY_GENERATION] == gen
            && entry[ENTRY_OUR_BITBOARD] == ourBoard
            && entry[ENTRY_OPP_BITBOARD] == oppBoard
            ){
            return entry;
        }
        return null;
    }

    public String toString(){
        String str = "";
        int len = 0;
        int collisionCnt[] = new int[100];
        int max = 0;

        int c = 0;
        // for (int i = 0; i <max; i++){
        //     str += i + ": " + array[i]collisions + "\n";
        // }
        return str;
    }

    public static void main(String[] args){
        int n= 2;
        HashTable ht = new HashTable(20);
        for (int i = 2; i < 100; i++){
            n = ht.getNextPrime(n+1);
            System.out.println(i + ":  " + n);
        }
        System.out.println("getnextPrime(2147483644) = " +ht.getNextPrime(2147483644));
    }
}
