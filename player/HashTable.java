

// public class HashTable{
//     /**
//      *  Place any data fields here.
//      **/
//     private HList [] array;
//     private int numBuckets;
//     private int numItems;
//     private int collisions = 0;

//     /**
//      *  Construct a new empty hash table intended to hold roughly sizeEstimate
//      *  entries.  (The precise number of buckets is up to you, but we recommend
//      *  you use a prime number, and shoot for a load factor between 0.5 and 1.)
//      **/
//     public HashTable(int sizeEstimate) {
//         numBuckets = getNextPrime((int) (sizeEstimate*1.5));
//         array = new HList[numBuckets];
//         for (int i =0;i < numBuckets; i++){
//             array[i] = new HList();
//         }
//         numItems = 0;
//     }

//     /**
//      *  Construct a new empty hash table with a default size.  Say, a prime in
//      *  the neighborhood of 100.
//      **/
//     public HashTable() {
//         this(101);
//     }

//     /** returns the smallest prime >= n
//      */
//     private int getNextPrime(int n){
//         int sq;
//         start:
//         while (true){
//             sq = (int)Math.sqrt(n)+1;
//             for (int i=2;i<=sq;i++){
//                 if (n % i == 0){
//                     n++;
//                     continue start;
//                 }
//             }
//             return n;
//         }
//     }

//     /** returns the largest prime prime <= n
//      */
//     private int getPrevPrime(int n){
//         int sq;
//         if (n <= 1){
//             return 1; //::Q is 1 a prime?
//         }
//         start:
//         while (true){
//             sq = (int)Math.sqrt(n)+1;
//             for (int i=2;i<=sq;i++){
//                 if (n % i == 0){
//                     n--;
//                     continue start;
//                 }
//             }
//             return n;
//         }
//     }

//     /**
//      *  Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
//      *  to a value in the range 0...(size of hash table) - 1.
//      *
//      *  This function should have package protection (so we can test it), and
//      *  should be used by insert, find, and remove.
//      **/
//     int compFunction(int code) {
//         // Replace the following line with your solution.
//         int c = code % numBuckets;
//         return c < 0 ? c + numBuckets : c;
//     }

//     /**
//      *  Returns the number of entries stored in the dictionary.  Entries with
//      *  the same key (or even the same key and value) each still count as
//      *  a separate entry.
//      *  @return number of entries in the dictionary.
//      **/
//     public int size() {
//         //TODO: rename these size fields to something less retarded
//         return numItems;
//     }

//     public int numBuckets(){
//         return numBuckets;
//     }
//     public double loadFactor(){
//         return numItems/((double)numBuckets);
//     }

//     /**
//      *  Tests if the dictionary is empty.
//      *
//      *  @return true if the dictionary has no entries; false otherwise.
//      **/
//     public boolean isEmpty() {
//         return numItems == 0;
//     }

//     /**
//      *  Create a new Entry object referencing the input key and associated value,
//      *  and insert the entry into the dictionary.  Return a reference to the new
//      *  entry.  Multiple entries with the same key (or even the same key and
//      *  value) can coexist in the dictionary.
//      *
//      *  This method should run in O(1) time if the number of collisions is small.
//      *
//      *  @param key the key by which the entry can be retrieved.
//      *  @param value an arbitrary object.
//      *  @return an entry containing the key and value.
//      **/
//     public Entry insert(Object key, Object value) {
//         // Replace the following line with your solution.
//         Entry entry = new Entry();
//         entry.key = key;
//         entry.value = value;
//         int index = compFunction(key.hashCode());
//         array[index].add(entry);
//         if (array[index].length() > 1){
//             collisions++;
//         }
//         numItems++;
//         return entry;
//     }

//     /**
//      *  Search for an entry with the specified key.  If such an entry is found,
//      *  return it; otherwise return null.  If several entries have the specified
//      *  key, choose one arbitrarily and return it.
//      *
//      *  This method should run in O(1) time if the number of collisions is small.
//      *
//      *  @param key the search key.
//      *  @return an entry containing the key and an associated value, or null if
//      *          no entry contains the specified key.
//      **/
//     public Entry find(Object key) { //test
//         int hc = key.hashCode();
//         HListNode node = array[compFunction(hc)].find(key);
//         return node == null ? null : node.entry;
//     }

//      /**
//       *  Remove an entry with the specified key.  If such an entry is found,
//       *  remove it from the table and return it; otherwise return null.
//       *  If several entries have the specified key, choose one arbitrarily, then
//       *  remove and return it.
//       *
//       *  This method should run in O(1) time if the number of collisions is small.
//       *
//       *  @param key the search key.
//       *  @return an entry containing the key and an associated value, or null if
//       *          no entry contains the specified key.
//       */
//     public Entry remove(Object key) {
//         numItems--;
//         return array[compFunction(key.hashCode())].removeEntry(key);
//     }

//     /**
//      *  Remove all entries from the dictionary.
//      */
//     public void makeEmpty(){
//         for (int i = 0; i < numBuckets; i++){
//             array[i].clear();
//         }
//         collisions = numItems = 0;
//     }

//     public String toString(){
//         String str = "";
//         int len = 0;
//         int collisionCnt[] = new int[100];
//         int max = 0;
//         for (HList dl : array){
//             len = dl.length();
//             if (len > max){
//                 max = len;
//             }
//             if (len < 100){
//                 collisionCnt[len]++;
//             }else{
//                 System.out.println("You're hash sucks!");
//             }
//         }
//         int c = 0;
//         for (int i = 0; i <max; i++){
//             c = collisionCnt[i];
//             str += i + ": " + c + "\n";
//         }
//         return str;
//     }
//     public int collisions (){
//         return collisions;
//     }

//     public static void main(String[] args){
//         int n= 2;
//         HashTableChained ht = new HashTableChained();
//         HList list = new HList();
//         for (int i = 2; i < 100; i++){
//             n = ht.getNextPrime(n+1);
//             System.out.println(i + ":  " + n);
//         }
//         System.out.println("getPrevPrime(Integer.MAX_VALUE) = " +ht.getPrevPrime(Integer.MAX_VALUE));
//         System.out.println("getnextPrime(2147483644) = " +ht.getNextPrime(2147483644));
//         System.out.println("getprevPrime(1) = " +ht.getPrevPrime(1));

//     }

// }

