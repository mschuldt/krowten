/* Entry.java */

package dict;

public class Entry {

    public long ourBitBoard, opponentBitBoard;
    public int score;

    public Entry(int s, long ourBoard, long opponentBoard){
        score = s;
        ourBitBoard = ourBoard;
        opponentBoard = opponentBitBoard;
    }
}
