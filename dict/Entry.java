/* Entry.java */

package dict;

public class Entry {

    public long ourBitBoard, opponentBitBoard;
    public int score;

    public Entry(){
    }

    public Entry(int s, long ourBoard, long opponentBoard){
        score = s;
        ourBitBoard = ourBoard;
        opponentBitBoard = opponentBoard;
    }
    public String toString(){
        return "<entry: " +score + " (" + ourBitBoard + " . " + opponentBitBoard + ")>";
    }
}
