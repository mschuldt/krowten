// player/Board.java

package player; //this with Piece.java don't seem like they belong here...

public class Board{
    Piece[][] pieceArray;
    int color;
    int opponentColor;
    private Piece edge;

    public Board(int c){
        color = c; //0 for black, 1 for white
        opponentColor = 1-c;
        edge = new Piece(0,0,0);
        pieceArray = new Piece[66][66];
        
        for (int x = 0; x < 66; x++){
            pieceArray[x][0] = edge;
            pieceArray[x][65] = edge;
        }
        
        for (int y = 0; y < 66; y++){
            pieceArray[0][y] = edge;
            pieceArray[65][y] = edge;
        }
        
        pieceArray[1][1] = edge;
        pieceArray[64][1] = edge;
        pieceArray[64][64] = edge;
        pieceArray[1][64] = edge;
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
    //seporate methods for moveing our pieces and moving their pieces
    //so that we don't have to pass the color of the piece we intend
    //to move every time. (the Move object does not have a color field
    //but the Piece objects do)
    public void move(Move move){
        move(move, color);
    }
    
    public void opponentMove(Move move){
        move(move, opponentColor);
    }

    
    
    
}

