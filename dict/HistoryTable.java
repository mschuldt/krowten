package dict;

import player.Move;

public class HistoryTable {
    private long [] array;
    private int numBuckets;
    private int numItems;
    public int collisions;

    private static final int NONE = -1;
    private static final int ENTRY_HASH = 0;
    private static final int ENTRY_SCORE = 1;

    int max = 0;
    long hashPrime = 1;

    /** construct a new hash table that maps Move objects to scores
     * the size is big enough to enable the internal hash method
     * to hash Moves to unique indexes.
     * the max load factor is about 0.107
     */
    public HistoryTable(){
        numBuckets = 38833;
        array = new long[numBuckets];
        for (int i =0;i < numBuckets; i++){
            array[i] = NONE;
        }
        numItems = collisions = 0;
    }

    /**
     *  This constructor in intended only for internal testing.
     **/
    private HistoryTable(int sizeEstimate) {
        numBuckets = (int) getNextPrime((int) (sizeEstimate*1.5));
        array = new long[numBuckets];
        for (int i =0;i < numBuckets; i++){
            array[i] = NONE;
        }
        numItems = collisions = 0;
    }

    /** returns the smallest prime >= n
     */
    private static long getNextPrime(long n){
        long sq;
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
        return (numItems)/((double)numBuckets);
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
    public void insert(Move move, long score) {

        int index = compFunction(hashMove(move));

        if (array[index] != NONE){
            collisions++;
        }

        numItems++;
        array[index] = score;
    }

    /** increment the score mapped to MOVE my AMOUNT
     */
    public void increment(Move move, long amount) {
        array[compFunction(hashMove(move))] += amount;
    }

    /**
     *  Search for an entry with the specified key.  If such an entry is found,
     *  return it; otherwise return null.
     **/
    public long find(Move move){
        return array[compFunction(hashMove(move))];
    }

    public String toString(){
        return "HistoryTable("+numBuckets+")";
    }

    public long hashMove(Move m){
        if (m.moveKind == Move.ADD){
            return ((m.x1+1) << 60) | ((m.y1+1) << 56);
        }else if (m.moveKind == Move.STEP){
            return ((m.x1+1) <<12) | ((m.y1+1) << 8) | ((m.x2+1) << 4) | m.y2;
        }
        return 0;
    }

    // divide the all the values in this table by 2
    public void decay (){
        //ADD moves
        for (int x = 0; x < 8; x ++){
            for (int y = 0; y < 8; y ++){
                array[compFunction(((x+1) << 60) | ((y+1) << 56))] /= 2;
            }
        }
        //STEP moves
        for (int x = 0; x < 8; x ++){
            for (int y = 0; y < 8; y ++){
                for (int x2 = 0; x2 < 8; x2 ++){
                    for (int y2 = 0; y2 < 8; y2 ++){
                        array[((x+1) <<12) | ((y+1) << 8) | ((x2+1) << 4) | y2] /= 2;
                    }
                }
            }
        }
    }

    public void fill (){
        int score = 0;
        Move m;
        for (int x = 0; x < 8; x ++){
            for (int y = 0; y < 8; y ++){
                m = new Move(x, y);
                insert(m, score);
                score++;
            }
        }
        // System.out.println("added ADD moves. found "
        //                    + ht.collisions + " collisions");

        //add step moves
        for (int x = 0; x < 8; x ++){
            for (int y = 0; y < 8; y ++){
                for (int x2 = 0; x2 < 8; x2 ++){
                    for (int y2 = 0; y2 < 8; y2 ++){
                        m = new Move(x, y, x2, y2);
                        insert(m, score);
                        score++;
                    }
                }
            }
        }
        // System.out.println("added STEP moves. found "
        //                    + ht.collisions + " collisions");
    }

    public static void findBestSize(){
        HistoryTable ht;
        int min = 1000;
        long hashPrime = 10000;
        //100189 with size = 50000
        while (true){
            hashPrime = getNextPrime(hashPrime+1);
            //ht = new HistoryTable(50000);
            ht = new HistoryTable((int) hashPrime-1);
            //ht.hashPrime = hashPrime;
            ht.fill();
            if (ht.collisions < min){
                min = ht.collisions;
                System.out.println("prime = " + hashPrime);
                System.out.println(" collisions = " + ht.collisions);

            }
            if (min  == 0){
                System.out.println("load factor = " + ht.loadFactor());
                System.out.println("size = " + getNextPrime((long) (25888*1.5)));
                return;
            }
        }
    }
    public static void main(String[] args){
        //findBestSize();
        HistoryTable ht = new HistoryTable();
        ht.fill();
        System.out.println("collisions = " + ht.collisions);
    }
}
