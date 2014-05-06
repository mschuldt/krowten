public class _test{
    // public static void addToList(AList<Move> m,int s,int n){
    //     m.clear();
    //     for (int i = s;i< n; i++){
    //         m.add(new Move(i,i));
    //     }
    // }

    public static void main (String[] args){
        long [] masks = new long[64];
        long bitRep = 1;
        for (int i = 0; i < 64; i++){
            masks[i] = bitRep;
            bitRep <<= 1;
        }

        for (long m :masks){
            System.out.println(m+" % 67 = " + m % 67);
        }

        // AList<Move> moves = new AList<Move>(400);
        // addToList(moves,0, 189);
        // addToList(moves,5, 10);

        // for (Move m: moves){
        //     System.out.println(m.x1);
        // }
        // addToList(moves,30, 38);
        // System.out.println("==");
        // System.out.println("size = " + moves.length());
        // for (int i = 0; i < moves.length(); i++){
        //     Move m = moves.get(i);
        //     System.out.println(m.x1);
        // }
    }
}
