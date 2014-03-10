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
            toX = move.x1 + 1;
            toY = move.y1 + 1;
            //TODO: asserts to check index validity
            assert pieceArray[toX][toY] == null : "square is already full";
            pieceArray[toX][toY] = new Piece(color, move.x1, move.y1);
            break;
        case Move.STEP :
            int fromX = move.x2 + 1,
                fromY = move.y2 + 1;
            toX = move.x1 + 1;
            toY = move.y1 + 1;

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

    private int countBorderPieces (Piece[] pieces){
	int c = 0;
	for (Piece p : pieces){
	    if (p == edge) {
		c+=1;
	    }
	}
	return c;
    }

    private Piece[] removeEmptyPieces(Piece[] pieces){
	Piece [] ret = new Piece[countBorderPieces(pieces)];
	int i=0;
	for (Piece p : pieces){
	    if (!( p == edge)){
		ret[i++] = p;
	    }
	}
	return ret;
    }
    
    Piece[] adjacentPieces(int x, int y){
        x++; y++;
        
        Piece [] pieces = {pieceArray[x][y-1],	 //top
                           pieceArray[x+1][y-1],  //top right
                           pieceArray[x+1][y],	 //right
                           pieceArray[x+1][y+1],  //bottom right
                           pieceArray[x][y+1],	 //bottom
                           pieceArray[x-1][y+1],  //bottom left
                           pieceArray[x-1][y],	 //left
                           pieceArray[x-1][y-1]}; //top left

        return removeEmptyPieces(pieces);
    }
}

