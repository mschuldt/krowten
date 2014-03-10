// player/Board.java

package player; //this with Piece.java don't seem like they belong here...

public class Board{
    Piece[][] pieceArray;
    int color;
    int opponentColor;
    public Board(int c){
        color = c; //0 for black, 1 for white
        opponentColor = 1-c;
        
        pieceArray = new Piece[64][64];
        pieceArray[0][0]   = null;
        pieceArray[63][0]  = null;
        pieceArray[0][63]  = null;
        pieceArray[63][63] = null;
    }
    //? public/protected?
    //This assumes that MOVE is valid
    private void move(Move move, int color){
        int toX, toY;
        switch (move.moveKind){
        case Move.ADD :
            toX = move.x1;
            toY = move.y1;
            //TODO: asserts to check index validity
            assert pieceArray[toX][toY] == null : "square is already full";
            pieceArray[toX][toY] = new Piece(color, move.x1, move.y1);
            break;
        case Move.STEP :
            int fromX = move.x2,
                fromY = move.y2;
            toX = move.x1;
            toY = move.y1;
            assert pieceArray[toX][toY] == null : "square is already full";
            assert pieceArray[fromX][fromY] != null : "square is empty";
            pieceArray[toX][toY] = pieceArray[fromX][fromY];
            pieceArray[fromX][fromY] = null;
            break;
        case Move.QUIT :
            //TODO
            break;
        }
    }
    
    public void move(Move move){
        move(move, color);
    }
    

}

