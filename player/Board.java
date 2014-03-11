// player/Board.java

package player;

import java.util.List;
import java.util.ArrayList;

public class Board{
    Piece[][] pieceArray;
    int ourColor;
    int opponentColor;

    long ourBitBoard = 0;
    long opponentBitBoard = 0;
    // because the corners of the gameboard cannot be used, the last bit is
    // not needed (actually the last two). This is lucky because java has no
    // equivalent of an unsigned long integer
    //
    // The `bitReps' array was generated with this python code:
    // "{0, " + ", ".join([str(hex(int("1" + "0"*x, 2))) + "L" for x in range(64)]) + "}"
    long[] bitReps = {0x1L, 0x2L, 0x4L, 0x8L, 0x10L, 0x20L, 0x40L, 0x80L, 0x100L,
		      0x200L, 0x400L, 0x800L, 0x1000L, 0x2000L, 0x4000L,
		      0x8000L, 0x10000L, 0x20000L, 0x40000L, 0x80000L,
		      0x100000L, 0x200000L, 0x400000L, 0x800000L,
		      0x1000000L, 0x2000000L, 0x4000000L, 0x8000000L,
		      0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L,
		      0x100000000L, 0x200000000L, 0x400000000L,
		      0x800000000L, 0x1000000000L, 0x2000000000L,
		      0x4000000000L, 0x8000000000L, 0x10000000000L,
		      0x20000000000L, 0x40000000000L, 0x80000000000L,
		      0x100000000000L, 0x200000000000L, 0x400000000000L,
		      0x800000000000L, 0x1000000000000L, 0x2000000000000L,
		      0x4000000000000L, 0x8000000000000L, 0x10000000000000L,
		      0x20000000000000L, 0x40000000000000L,
		      0x80000000000000L, 0x100000000000000L,
		      0x200000000000000L, 0x400000000000000L,
		      0x800000000000000L, 0x1000000000000000L,
		      0x2000000000000000L, 0x4000000000000000L,
		      0x8000000000000000L};
    /* Python code template used for generating bitmasks
      hex(int("""
      00000000
      00000000
      00000000
      00000000
      00000000
      00000000
      00000000
      00000000""".replace("\n",""), 2))
    */
    long cornersMask = 0x4081000000000081L,
	
	upperGoalMask = 0x7e00000000000000L,
	lowerGoalMask = 0x7e,
	rightGoalMask = 0x1010101010100L,
	leftGoalMask = 0x80808080808000L,
	ourGoalMaskA,
	ourGoalMaskB,
	ourGoalMask,
	opponentGoalMaskA,
	opponentGoalMaskB,
	opponentGoalMask;

    //this piece is used to mark the edge of the board and the
    //four invalid corner squares
    private Piece edge;

    public Board(int c){
        ourColor = c; //0 for black, 1 for white
        opponentColor = 1-c;
	//TODO: assign goal masks
        edge = new Piece(0,0,0,0);
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

    //returns the binary representation of the piece at (X, Y)
    private long getBitRep(int x,int y){
	return bitReps[x*8 + y];
    }

    //? public/protected?
    //This assumes that MOVE is valid
    private void move(Move move, int color){
        int toX, toY;
	long bitRep;
        switch (move.moveKind){
        case Move.ADD :
            toX = move.x1 + 1;
            toY = move.y1 + 1;
	    bitRep = getBitRep(toX,toY);

	    //TODO: asserts to check index validity
            assert pieceArray[toX][toY] == null : "square is already full";

	    //does lefthand ternary operator work in java??
	    if (color == ourColor){
		ourBitBoard &= bitRep;
	    }else{
		opponentBitBoard &= bitRep;
	    }
            pieceArray[toX][toY] = new Piece(color, bitRep, move.x1, move.y1); //FIX
            break;
        case Move.STEP :
            int fromX = move.x2 + 1,
                fromY = move.y2 + 1;
            toX = move.x1 + 1;
            toY = move.y1 + 1;
	    bitRep = getBitRep(toX, toY);
	    assert pieceArray[toX][toY] == null : "square is already full";
            assert pieceArray[fromX][fromY] != null : "square is empty";

	    if (color == ourColor){
		//remove old location
		ourBitBoard ^= pieceArray[fromX][fromY].bitRep;
		//add new location
		ourBitBoard &= bitRep;

	    }else{
		opponentBitBoard ^= pieceArray[fromX][fromY].bitRep;
		opponentBitBoard &= bitRep;
	    }
            pieceArray[toX][toY] = pieceArray[fromX][fromY];
	    pieceArray[toX][toY].bitRep = getBitRep(toX, toY);
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
        move(move, ourColor);
    }

    public void opponentMove(Move move){
        move(move, opponentColor);
    }

    //This assumes that the move we are undoing was our move.
    //(the bitboards will get messed up if this was not the case)
    void unMove(Move move){
        switch (move.moveKind){
        case Move.ADD :
            int x = move.x1 + 1,
                y = move.y1 + 1;
            //TODO: asserts to check index validity
            assert pieceArray[x][x] != null : "square should not be empty";
	    ourBitBoard ^= pieceArray[x][y].bitRep;
            pieceArray[x][y] = null;
            break;
        case Move.STEP :
            int toX = move.x2 + 1,
                toY = move.y2 + 1,
                fromX = move.x1 + 1,
                fromY = move.y1 + 1;

            assert pieceArray[toX][toY] == null : "square is already full";
            assert pieceArray[fromX][fromY] != null : "square is empty";

	    ourBitBoard ^= pieceArray[fromX][fromY].bitRep;
	    ourBitBoard &= pieceArray[toX][toY].bitRep;

            pieceArray[toX][toY] = pieceArray[fromX][fromY];
	    pieceArray[toX][toY].bitRep = getBitRep(toX, toY);
            pieceArray[fromX][fromY] = null;
            break;
        case Move.QUIT :
            //TODO
            break;
        }
    }

    private int countEdgePieces (Piece[] pieces){
        int c = 0;
        for (Piece p : pieces){
            if (p == edge) {
                c+=1;
            }
        }
        return c;
    }

    private Piece[] removeEdgePieces(Piece[] pieces){
        Piece [] ret = new Piece[countEdgePieces(pieces)];
        int i=0;
        for (Piece p : pieces){
            if (!( p == edge)){
                ret[i++] = p;
            }
        }
        return ret;
    }

    public Piece[] adjacentPieces(int x, int y){
        x++; y++;
        //TODO: use an ArrayList to avoid having to traverse this array twice
        Piece [] pieces = {pieceArray[x][y-1],   //top
                           pieceArray[x+1][y-1],  //top right
                           pieceArray[x+1][y],   //right
                           pieceArray[x+1][y+1],  //bottom right
                           pieceArray[x][y+1],   //bottom
                           pieceArray[x-1][y+1],  //bottom left
                           pieceArray[x-1][y],   //left
                           pieceArray[x-1][y-1]}; //top left

        return removeEdgePieces(pieces);
    }

    public Piece[] adjacentPieces(Piece P){
        return adjacentPieces(P.x, P.y);
    }


    public boolean pieceAt(int x, int y){
        //TODO: bounds checking
        return pieceArray[x+1][y+1] != null;
    }

    public Piece getPiece(int x, int y){
        //TODO: bounds checking
        return pieceArray[x+1][y+1];
    }

    public Piece[] connectedPieces(int x, int y){
        List<Piece> pieces = new ArrayList<Piece>();
        int startX = x + 1;
        int startY = y + 1;
        int currentX = startX, currentY = startY -1;
        Piece current = pieceArray[currentX][currentY];
        int xInc, yInc;

        int[][] increments = {{0,-1}, //above
                              {0, 1}, //below
                              {-1,0}, //left
                              {0, 1}, //right
                              {1,-1}, //right upper diagonal
                              {1, 1}, //right lower diagonal
                              {-1,-1}, //left upper diagonal
                              {-1,1}}; //left lower diagonal

        for (int[] inc : increments){
            xInc = inc[0];
            yInc = inc[1];
            currentX = startX + xInc;
            currentY = startY + yInc;
            current = pieceArray[currentX][currentY];

            while (current == null){
                currentX += xInc;
                currentY += yInc;
                current = pieceArray[currentX][currentY];
            }
            if (current != edge){
                pieces.add(current);
            }
        }
        return (Piece[])pieces.toArray();
    }

    //returns a piece from goalA 
    private Piece getGoalPiece(){
	//TODO:
	
	//?? what is the orientation of the board during play
	//are we or a color always centered up-down or can it vary?
	return new Piece(0,0,0,0);
    }

    public Piece[] connectedPieces(Piece P){
        return connectedPieces(P.x, P.y);
    }

    // looks for a network from goalA -> goalB
    private boolean hasNetwork(Piece currentPiece, int color, long memberPieces, int m, int b){
 	long bitBoard = (color == ourColor ? ourBitBoard : opponentBitBoard);
	int newM, newB;
	for (Piece piece : connectedPieces(currentPiece)){
	    if ((piece.bitRep & ourGoalMaskB) != 0){
		return true; 
	    }
	    if ((piece.bitRep & memberPieces) != 0){
		return false; // we have already visited this piece
	    }
	    newM = (piece.y - currentPiece.y)/(piece.x == currentPiece.x ? 10 : (piece.x - currentPiece.x));
	    newB = piece.y - newM*piece.x;
	    if ((newM == m) && (newB == b)){
		return false; //on the same line
	    }
	    if (hasNetwork(piece, color, memberPieces & piece.bitRep, newM, newB)){
		return true;
	    }
	}
	return false;
    }

    // TODO: can we just assume that we are checking if we have a network?
    //       when would we have to check for our opponent?
    public boolean hasNetwork(int color){
	long bitBoard = (color == ourColor ? ourBitBoard : opponentBitBoard);
	long goalA = (color == ourColor ? ourGoalMaskA : opponentGoalMaskA);
	long goalB = (color == ourColor ? ourGoalMaskB : opponentGoalMaskB);
	if (((bitBoard & goalA) != 0) && ((bitBoard & goalB) != 0)){
	    return hasNetwork(getGoalPiece(), color, ourGoalMaskA, 11, 60); //11x+60: just an impossible line
	}
	return false; //does not have at lease one piece in each goal
    }

    //returns an array of charters representing the state of the board
    public char[][] toCharArray(){
	char[][] ret = new char[66][66];
	Piece piece;
	char c;
	for (int x = 0; x < 65; x++){
	    for (int y = 0; y < 65; y++){
		piece = pieceArray[x][y];
		if (piece == null){
		    c = ' ';
		}else if (piece == edge){
		    c = '#';
		}else if (piece.color == ourColor){
		    c = 'X';
		}else{
		    c = 'O';
		}
		ret[x][y] = c;
	    }
	}
	return ret;
    }
    
    public String toString(){
	String ret = "";
	char[][] charArray = toCharArray();
	for (int x = 0; x < 65; x++){
	    for (int y = 0; y < 65; y++){
		ret += charArray[x][y];
	    }
	    ret += "\n";
	}
	return ret;
    }
	    
}
