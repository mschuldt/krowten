/* Entry.java */

package cs61bayn.dict;

public class Entry {

    public long ourBitBoard, opponentBitBoard;
    public int score;
    public int generation;
    public int collisions;

    public Entry(){
        collisions = 0;
    }

    public Entry(int s, long ourBoard, long opponentBoard){
        score = s;
        ourBitBoard = ourBoard;
        opponentBitBoard = opponentBoard;
        collisions= 0;
    }
    public String toString(){
        return "<entry: " +score + " (" + ourBitBoard + " . " + opponentBitBoard + ")>";
    }
}
