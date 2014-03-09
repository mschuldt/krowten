// player/Board.java

package player; //this with Piece.java don't seem like they belong here...

public class Board{
    Piece[][] pieceArray;
    public Board(){
        pieceArray = new Piece[64][64];
        pieceArray[0][0]   = null;
        pieceArray[63][0]  = null;
        pieceArray[0][63]  = null;
        pieceArray[63][63] = null;
    }
}

