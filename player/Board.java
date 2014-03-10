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
    //? public/protected?
    //This assumes that MOVE is valid
    public void move(Move move, int color){
        switch (move.moveKind){
        case Move.ADD :
            assert pieceArray[move.x1][move.y1] == null : "square is already full";
            pieceArray[move.x1][move.y1] = new Piece(color, move.x1, move.y1);
            break;
        case Move.STEP :
            assert pieceArray[move.x1][move.y1] == null : "square is already full";
            assert pieceArray[move.x2][move.y2] != null : "square is empty";
            pieceArray[move.x1][move.x1] = pieceArray[move.x2][move.x2];
            pieceArray[move.x2][move.x2] = null;
            break;
        case Move.QUIT :
            //TODO
            break;
        }
    }
}

