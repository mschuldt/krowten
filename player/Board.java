// player/Board.java

package player;

//import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Deque;
import java.util.ArrayDeque;
import java.io.*;

public class Board{
    Piece[][] pieceArray;
    int ourColor;
    int opponentColor;
    int ourPieceCount, opponentPieceCount;
    List<Piece> P = new List<Piece>();
    PieceList PP = new PieceList();
    public static final int white = 1;
    public static final int black = 0;
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
    //TODO: why are these masks effectively reflected?
    //      NOTE: they have been reversed in hasNetwork to account for this
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
    public static final Piece edge = new Piece(0,0,0,0);

    /** Board.Board(int) constructs a new board for player with color COLOR
     *  The board is initially empty
     *
     *  COLOR must be white or black (1 or 0)
     *
     *  @param color an integer representing the color of the player
     *         who 'owns' this board. 1 for white and 0 for black.n
     */
    public Board(int color){
        if (color != 1 && color != 0){
            System.out.println("Board.Board(int) -- Error: invalid color");
        }
        ourPieceCount =  opponentPieceCount = 0;
        ourColor = color; //0 for black, 1 for white
        opponentColor = 1-color;
        //TODO: assign goal masks
        pieceArray = new Piece[10][10];

        //NOTE: if this goal mask assignment is changed, then
        //      code in getStartGoalPieces must be updated.
        if (ourColor == 1){ //white's goals are on the left and right
            ourGoalMaskA = leftGoalMask;
            ourGoalMaskB = rightGoalMask;
            opponentGoalMaskA = lowerGoalMask;
            opponentGoalMaskB = upperGoalMask;
        }else{
            ourGoalMaskA = lowerGoalMask;
            ourGoalMaskB = upperGoalMask;
            opponentGoalMaskA = leftGoalMask;
            opponentGoalMaskB = rightGoalMask;
        }

        ourGoalMask = (ourGoalMaskA | ourGoalMaskB);
        opponentGoalMask = (opponentGoalMaskA | opponentGoalMaskB);

        for (int x = 0; x < 10; x++){
            pieceArray[x][0] = edge;
            pieceArray[x][9] = edge;
        }

        for (int y = 0; y < 10; y++){
            pieceArray[0][y] = edge;
            pieceArray[9][y] = edge;
        }

        pieceArray[1][1] = edge;
        pieceArray[8][1] = edge;
        pieceArray[1][8] = edge;
        pieceArray[8][8] = edge;
    }

    //returns the binary representation of the piece at (X, Y)
    private long getBitRep(int x,int y){
        //NOTE: (x,y) is the ACTUAL position of the piece on the board. Not the 1+ thing
        assert x >= 0 && y >= 0 && (x*8 + y) < 64 : "invalid index (Board.getBitRep)";
        return bitReps[y*8 + x];
    }

    /** Board.move(Move,int) moves a piece on the board as described
     *  by MOVE. COLOR is the color of the piece to be moved.
     *
     *  Unusual conditions:
     *    * If MOVE is not an illegal move, the behavior of this method
     *      is not defined.
     *    * If COLOR is not a valid color, the behavior is not defined.
     *
     * @param move describes the type of move to make
     * @param color color of the piece to be moved (1 for white, 0 for black)
     */
    private void move(Move move, int color){ //? public/protected?
        int toX, toY;
        long bitRep;
        switch (move.moveKind){
        case Move.ADD :
            toX = move.x1 + 1;
            toY = move.y1 + 1;
            bitRep = getBitRep(toX-1, toY-1);

            //TODO: asserts to check index validity
            assert pieceArray[toX][toY] == null : "square is already full";

            //does lefthand ternary operator work in java??
            if (color == ourColor){
                ourBitBoard |= bitRep;
                ourPieceCount++;
                assert ourPieceCount <= 10 : colorStr(color) + " has more then 10 pieces";
            }else{
                opponentBitBoard |= bitRep;
                opponentPieceCount++;
                assert opponentPieceCount <= 10 : colorStr(color) + " has more then 10 pieces";
            }
            pieceArray[toX][toY] = new Piece(color, bitRep, move.x1, move.y1); //FIX
            break;
        case Move.STEP :
            int fromX = move.x2 + 1,
                fromY = move.y2 + 1;

            toX = move.x1 + 1;
            toY = move.y1 + 1;

            bitRep = getBitRep(toX-1, toY-1);
            assert pieceArray[toX][toY] == null : "square is already full";
            assert pieceArray[fromX][fromY] != null : "square is empty";
            assert pieceArray[fromX][fromY].color == color : "cannot move opponents piece";

            if (color == ourColor){
                //remove old location
                ourBitBoard ^= pieceArray[fromX][fromY].bitRep;
                //add new location
                ourBitBoard |= bitRep;

            }else{
                opponentBitBoard ^= pieceArray[fromX][fromY].bitRep;
                opponentBitBoard |= bitRep;
            }
            Piece p = pieceArray[fromX][fromY];
            pieceArray[toX][toY] = p;
            p.bitRep = getBitRep(toX-1, toY-1);
            p.x = toX-1;
            p.y = toY-1;
            pieceArray[fromX][fromY] = null;
            break;
        case Move.QUIT :
            //TODO
            break;
        }
        verify();
    }
    //seporate methods for moveing our pieces and moving their pieces
    //so that we don't have to pass the color of the piece we intend
    //to move every time. (the Move object does not have a color field
    //but the Piece objects do)
    /** Board.move() moves a piece on the board as described
     *  by MOVE. The piece moved belongs to the owner of this board,
     *  that is, the player whose color is Board.color
     *
     *  Unusual conditions:
     *    * If MOVE is not an illegal move, the behavior of this method
     *      is not defined.
     *
     * @param move describes the type of move to make
     */
    public void move(Move move){
        move(move, ourColor);
    }

    /** Board.opponentMove() moves a piece on the board as described
     *  by MOVE. The piece moved belongs to the opponent of the owner
     *  of this board, that is, the player whose color is Board.opponentColor
     *
     *  Unusual conditions:
     *    * If MOVE is not an illegal move, the behavior of this method
     *      is not defined.
     *
     * @param move describes the type of move to make
     */
    public void opponentMove(Move move){
        move(move, opponentColor);
    }

    //This assumes that the move we are undoing was our move.
    //(the bitboards will get messed up if this was not the case)
    /** Board.unMove(Move) reverses the effects of the move MOVE.
     *  MOVE must have been a move for the pieces of the player
     *  who owns this board, that is, the player whose color
     *  was passed to the initializer.
     *
     *  Unusual conditions:
     *  The behaviour of this method is not defined for the case
     *  in which MOVE is invalid or is intended for the opponent.
     *
     *  @param move the move to reverse.
     */
    void unMove(Move move){
        Piece p = null;
        switch (move.moveKind){
        case Move.ADD :
            int x = move.x1 + 1,
                y = move.y1 + 1;
            //TODO: asserts to check index validity
            p = pieceArray[x][y];
            assert p != null : "square should not be empty";
            assert p != edge : "cannot undo: piece is an edge";

            if (p.color == ourColor){
                ourBitBoard ^= p.bitRep;
                ourPieceCount--;

            }else{
                opponentBitBoard ^= p.bitRep;
                opponentPieceCount--;
            }
            pieceArray[x][y] = null;
            break;
        case Move.STEP :
            int toX = move.x2 + 1,
                toY = move.y2 + 1,
                fromX = move.x1 + 1,
                fromY = move.y1 + 1;
            long toBitRep = getBitRep(toX-1, toY-1);

            p = pieceArray[fromX][fromY];
            assert pieceArray[toX][toY] == null : "square is already full";
            assert p != null : "square is empty";

            if (p.color == ourColor){
                ourBitBoard ^= p.bitRep;
                ourBitBoard |= toBitRep;
            }else{
                opponentBitBoard ^= p.bitRep;
                opponentBitBoard |= toBitRep;
            }
            //TODO: change p.x and p.y
            pieceArray[toX][toY] = p;
            pieceArray[toX][toY].bitRep = toBitRep;
            pieceArray[fromX][fromY] = null;
            break;
        case Move.QUIT :
            //TODO
            break;
        }
        verify();
    }

    //return the hash of the current board
    public long hash(){
        return ((ourBitBoard % 1073741789) << 31) | (opponentBitBoard % 1073741789);
    }


    /** Board.adjacentPieces(int,int) returns an array of pieces
     * that are adjacent to the pieces at coordinates (X,Y) on
     * the board.
     *
     * The x,y coordinates are indexed from the top left with x
     * increasing to the right and y increasing down.
     *
     *  If there is a piece at (x,y) then only adjacent pieces of the
     *  same color as it will be returned.
     *  If there is no piece at (x,y) but (x,y) is still a valid
     *  board location, then all the pieces surrounding (x,y)
     *  of any color will be returned.
     *
     * The list returned does not include PIECE, just the ones around it,
     * so the returned list ranges in length from 0 to 8.

     * Unusual conditions:
     *  if (x,y) is not a valid location on the board, then this
     *  method will likely cause the program to crash.
     *  The behavior in this case is undefined (but the corner
     *  pieces are ok).
     *
     *  @param x the x-coordinate location of the square
     *  @param y the y-coordinate location of the square
     *
     *  @returns an array of pieces adjacent to location (x,y) on the board
     */
    public PieceList adjacentPieces(int x, int y,int color){
        x++; y++;
        //TODO: index bounds checking
        Piece [] pieces = {pieceArray[x][y-1],    //top
                           pieceArray[x+1][y-1],  //top right
                           pieceArray[x+1][y],    //right
                           pieceArray[x+1][y+1],  //bottom right
                           pieceArray[x][y+1],    //bottom
                           pieceArray[x-1][y+1],  //bottom left
                           pieceArray[x-1][y],    //left
                           pieceArray[x-1][y-1]}; //top left

        PieceList lst = new PieceList();
        lst.addIfColor(pieces, color);
        return lst;
    }

    //TODO: interface docs
    public PieceList adjacentPieces(int x, int y){
        assert pieceArray[x+1][y+1] != null : "cannot get adjacent pieces to empty square";
        return adjacentPieces(x, y, pieceArray[x+1][y+1].color);
    }

    /** Board.adjacentPieces(Piece) returns an array of pieces
     * that are adjacent to PIECE on the board
     *
     * The list returned does not include PIECE, just the ones around it,
     * so the returned list ranges in length from 0 to 8.
     *
     * Unusual conditions:
     *  The behavior of this method is undefined if piece is not
     *  actually on the board.
     *
     *  @param piece a piece on the board whose adjacent pieces will be returned
     *
     *  @returns an array of pieces adjacent to PIECE on the board
     */
    public PieceList adjacentPieces(Piece piece){
        return adjacentPieces(piece.x, piece.y);
    }

    //TODO: interface docs
    public PieceList adjacentPieces(Piece piece, int color){
        return adjacentPieces(piece.x, piece.y, color);
    }

    /** Board.pieceAt(int,int) returns true if a piece is located
     * at square (X,Y) on this board, else false.
     *
     *  if (x,y) is not a valid coordinate on this board, return false
     *
     * @param x the x-coordinate of the square to check
     * @param y the y-coordinate of the square to check
     *
     * @returns boolean; true if a piece is located at (X,Y), elsefalse
     */
    public boolean pieceAt(int x, int y){
        if (x < 0 || y < 0 || y > 7 || x > 7){
            return false;
        }
        Piece p = pieceArray[x+1][y+1];
        return (p != null && p != edge);
    }

    /** Board.getPiece(int,int) returns the piece located at (X,Y)
     *  on this board.
     *
     *  Unusual conditions:
     *   If (x,y) is not a valid coordinate on board, then the
     *   behavior of this method is undefined (and may likely
     *   crash the program)
     *
     * @param x the x-coordinate of the piece to be returned
     * @param y the y-coordinate of the piece to be returned
     *
     * @returns the piece located at (X,Y) on this board.
     */
    public Piece getPiece(int x, int y){
        //TODO: bounds checking
        return pieceArray[x+1][y+1];
    }
    /** Board.connectedPieces(int, int) returns a list of all the pieces
     *   'connected' the the piece located at square (X,Y) on this board.
     *   The exact rules for connectedness are as defined in this projects
     *   readme file.
     *
     *   If there is no piece at (X,Y) return null
     *   Only pieces of the same color as the one at (X,Y) are returned.
     *
     *   Unusual conditions:
     *    -If (X,Y) does not describe a valid location on the board, the
     *     behavior of this program is undefined. It may return a list
     *     of pieces or it may crash the program.
     *
     * @param x the x-coordinate of the piece on the board
     * @param y the y-coordinate of the piece on the board
     *
     * @returns an array of pieces that are 'connected' to the one at (X,Y)
     */
    public PieceList connectedPieces(int x, int y){
        int startX = x + 1;
        int startY = y + 1;
        Piece current = pieceArray[startX][startY];
        if (current == null){
            return null;
        }
        int color = current.color;
        PieceList pieces = new PieceList();
        int currentX = startX, currentY = startY -1;
        int xInc, yInc;
        int[][] increments = {{0,-1}, //above
                              {0, 1}, //below
                              {-1,0}, //left
                              {1, 0}, //right
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
            //            System.out.print("Trying: ");
            while (current == null){
                System.out.print(locStr(currentX-1, currentY-1));
                currentX += xInc;
                currentY += yInc;
                current = pieceArray[currentX][currentY];
            }
            //            System.out.println("=> found non-nil");
            if (current != edge){
                //                System.out.println("Found "+colorStr(current.color)+" piece at" + locStr(currentX-1, currentY-1));
                pieces.addIfColor(current, color);
            }

            // for (Piece p : pieces){
            //     System.out.print(locStr(p.x,p.y));
            // }
            //            System.out.println("");
        }
        return pieces;
    }

    /** Board.connectedPieces(Piece) returns a list of all the pieces
     *   'connected' the piece PIECE.
     *
     *   Unusual conditions:
     *    If PIECE is not on this board then the behavior of this method
     *    is undefined.
     *
     * @param piece the piece on whose connected pieces will be returned

     * @returns an array of pieces that are 'connected' to PIECE
     */
    public PieceList connectedPieces(Piece piece){
        return connectedPieces(piece.x, piece.y);
    }

    //returns a piece from goalA
    private PieceList getStartGoalPieces(int color){
        Piece p = null;
        PieceList lst = new PieceList();
        if (color == white){ //start at the left
            for (int y=2; y<8; y++){
                lst.addIfColor(pieceArray[1][y],color);
            }
        }else{//black starts at bottom
            for (int x=2; x<8; x++){
                lst.addIfColor(pieceArray[x][8],color);
            }
        }
        return lst;
    }

    // looks for a network from goalA -> goalB
    private boolean hasNetwork(Piece currentPiece, long bitBoard, long memberPieces,long goalmask,
                               int m, int b, int depth, PrintBoard pb){ //printBoard is just for debugging
        int newM, newB;

        // System.out.println("current piece:");
        // System.out.println(bitBoardToString(currentPiece.bitRep));
        // System.out.println("memberPieces:");
        // System.out.println(bitBoardToString(memberPieces));

        // System.out.println(pl.length() + " Connected pieces");
        // long tmp = 0;
        // for (Piece p : pl){
        //     tmp |= p.bitRep;
        // }
        // System.out.println("connected pieces:");
        // System.out.println(bitBoardToString(tmp));

        PieceList pl = connectedPieces(currentPiece);
        if (pl == null){
            return false;
        }
        for (Piece piece : pl){
            //System.out.println("(" + piece.x +"," + piece.y + ")");
            // System.out.println("trying Piece:");
            // System.out.println(bitBoardToString(piece.bitRep));

            if (piece.x == currentPiece.x){
                newM = 10;
                newB = piece.x;
            }else{
                newM = (piece.y - currentPiece.y)/(piece.x - currentPiece.x);
                newB = piece.y - newM*piece.x;
            }

            // System.out.println("m = "+ newM);
            //System.out.println("b = "+ newB);

            if ((newM == m) && (newB == b)){
                //System.out.println("on the same line");
                continue; //on the same line
            }

            if ((piece.bitRep & goalmask) != 0){
                // System.out.println("found network!");
                // System.out.println("end: (" + piece.x +"," + piece.y + ")");
                if (depth >= 6){
                pb.drawLine(currentPiece.x, currentPiece.y, piece.x, piece.y);
                return true;
                }
                continue; //can't visit a goal piece until the end
            }
            if ((piece.bitRep & memberPieces) != 0){
                // System.out.println("already visited");
                continue; // we have already visited this piece
            }

            if (hasNetwork(piece, bitBoard, memberPieces | currentPiece.bitRep, goalmask, newM, newB, depth+1,pb)){
                // System.out.println("==>(" + piece.x +"," + piece.y + ")");
                // System.out.println("found network!!");
                pb.drawLine(currentPiece.x, currentPiece.y, piece.x, piece.y);
                return true;
            }
        }
        return false;
    }
    //this is just temporary to maintain the interface. the origonal has a printboard passed to it
    //so that it can draw the lines on it.
    private boolean hasNetwork(Piece currentPiece, long bitBoard, long memberPieces, int m, int b){
        return hasNetwork(currentPiece, bitBoard, memberPieces, ourGoalMaskA, m, b, 1, toPrintBoard());
    }

    /**
     *  Board.hasNetwork() determines whether "this" Board has a valid
     *  network for player whose color is Board.color.
     *
     *  Unusual conditions:
     *  If the board contain illegal squares, the behavior of this
     *        method is undefined.
     *
     *  @return true if player whose color is 'this.color' has a winning network
     *          on 'this' GameBoard; false otherwise.
     **/
    public boolean hasNetwork(){
        if (((ourBitBoard & ourGoalMaskA) != 0)
            && ((ourBitBoard & ourGoalMaskB) != 0)){
            for (Piece piece : getStartGoalPieces(ourColor)){
                if (hasNetwork(piece, ourBitBoard, ourGoalMaskB, ourGoalMaskA,11, 60, 1,toPrintBoard())){ //11x+60: just an impossible line
                    return true;
                }
            }
        }
        return false; //does not have at least one piece in each goal
    }

    public boolean hasNetwork(int color, PrintBoard pb){
        long bitBoard = (color == ourColor ? ourBitBoard : opponentBitBoard);
        long goalA = (color == ourColor ? ourGoalMaskA : opponentGoalMaskA);
        long goalB = (color == ourColor ? ourGoalMaskB : opponentGoalMaskB);

        if (((bitBoard & goalA) != 0) && ((bitBoard & goalB) != 0)){
            for (Piece piece : getStartGoalPieces(color)){
                if (hasNetwork(piece, bitBoard, goalB, goalA, 11, 60,1, pb)){ //11x+60: just an impossible line
                    return true;
                }
            }
        }
        return false; //does not have at lease one piece in each goal
    }
    public boolean hasNetwork(int color){//Temp for debugging
        return hasNetwork(color,toPrintBoard());
    }
    /**
     *  formsIllegalCluster returns true if Move m will result in a cluster of 3 or more pieces
     */
    public boolean formsIllegalCluster(Move m, int color){
        int x = m.x1;
        int y = m.y1;
        int numNeighbors;
        if (this.pieceAt(x,y)){
            return false; // for now... probably need to throw an error, but isValidMove will also take care of it
        }
        this.move(m, color); //Board is updated
        PieceList neighbors = this.adjacentPieces(x, y, color); // get neighboring pieces of (presumably) the same color
        numNeighbors = neighbors.length();
        if (numNeighbors >1){
            this.unMove(m);
            return true;
        }
        if (numNeighbors == 1){
            Piece oneNeighbor = neighbors.get(0);
            PieceList moreNeighbors = this.adjacentPieces(oneNeighbor);
            int moreNumNeighbors = moreNeighbors.length();
            if (moreNumNeighbors >1){
                this.unMove(m);
                return true;
            }
        }
        this.unMove(m); //ALWAYS UNMOVE
        return false;
    }

    /*
     * returns list of all valid moves available for color, meaning
     * 1) move placing a new piece if he has < 10 pieces on the board and moving a piece otherwise
     * 2) the location where the piece will be placed is a valid and legal location on the board
     * (i.e., it is actually on the board, is not one of the four corners, and is not the other player's goals)
     * and is not currently occupied by any piece including itself.
     * 3) there will not be any clusters >= 3 on the board after making the Move.
     */
    public AList<Move> validMoves(int color) {
        int numPieces = getNumPieces(color);
        Piece[] pieces = new Piece[numPieces];
        AList<Move> mList = new AList<Move>(440);
        int x_lower, y_lower, x_upper, y_upper;
        if (color == 0) { // black
            x_lower = 1;
            x_upper = 6;
            y_lower = 0;
            y_upper = 7;
        } else { // white
            x_lower = 0;
            x_upper = 7;
            y_lower = 1;
            y_upper = 6;
        }
        if (numPieces < 10) { //ADD moves
            for (int x = x_lower; x <= x_upper; x++) {
                for (int y = y_lower; y <= y_upper; y++) {
                    if (!pieceAt(x, y)) {
                        Move m = new Move(x, y);
                        if (!formsIllegalCluster(m, color))
                            mList.add(m);
                    }
                }
            }
        } else {                      // STEP moves
            for (int x = x_lower; x <= x_upper; x++) {
                for (int y = y_lower; y <= y_upper; y++) {
                    if (!pieceAt(x, y)) {
                        for (int i = 0; i < numPieces; i++) {
                            Move m = new Move(x, y, pieces[i].x, pieces[i].y);
                            if (!formsIllegalCluster(m, color))
                                mList.add(m);
                        }
                    }
                }
            }
        }
        return mList;
    }

    /*
     *
     */
    public Piece[] getPieces(int color) {
        Piece[] pieces = new Piece[10];
        int i = 0;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (pieceAt(x, y) && (getPiece(x, y).color == color)) {
                    pieces[i] = getPiece(x, y);
                    i++;
                }
        return pieces;
    }

    public int getNumPieces(int color){
        if (color == ourColor){
            return ourPieceCount;
        }
        return opponentPieceCount;
    }

    //==========================================================================
    // Verification and testing code ===========================================
    //==========================================================================

    private String locStr(int x, int y){
        return "(" + x + "," + y +")";
    }

    //verify that all internal state is valid
    public boolean verify(){
        boolean ok = true;
        int c = 0;
        Piece p;

        //check that the bitboards and pieceArray are synced
        for (int x = 1; x < 9; x++){
            for (int y = 1; y < 9; y++){
                p = pieceArray[x][y];
                if (p == edge){
                    continue;
                }

                if (p == null){
                    //check that this space is also empty in the bitboads
                    if ((getBitRep(x-1,y-1) & ourBitBoard) != 0){
                        ok = false;
                        System.out.println(colorStr(ourColor) +" bitBoard has a piece at " + locStr(x,y) + " but the pieceArray is empty there");
                    }
                    if ((getBitRep(x-1,y-1) & opponentBitBoard) != 0){
                        ok = false;
                        System.out.println(colorStr(1 - ourColor) + " bitBoard has a piece at " + locStr(x,y) + " but the pieceArray is empty there");
                    }
                    continue;
                }
                c = p.color;
                //the piece of this color is in its bitboard and also not in the other bitboard
                if (c == ourColor){
                    if ((p.bitRep & ourBitBoard) == 0){
                        ok = false;
                        System.out.println(colorStr(c) + " Piece at" + locStr(p.x, p.y) + " is missing from its biboard");
                    }
                    if ((p.bitRep & opponentBitBoard) != 0){
                        ok = false;
                        System.out.println(colorStr(c) + " Piece at" + locStr(p.x, p.y) + " is in its opponents bitboard");
                    }

                } else {//Else it's the  opponents piece
                    if ((p.bitRep & opponentBitBoard) == 0){
                        ok = false;
                        System.out.println(colorStr(1-c) + " Piece at" + locStr(p.x, p.y) + " is missing from its biboard");
                    }
                    if ((p.bitRep & ourBitBoard) != 0){
                        ok = false;
                        System.out.println(colorStr(c) + " Piece at" + locStr(p.x, p.y) + " is in its opponents bitboard");
                    }
                }
                //check that no color has a piece in their opponents goals
                if (p.x != x-1 || p.y != y-1){
                    ok = false;
                    System.out.println(colorStr(c) + "Piece at " + locStr(x-1, y-1) + " has internal coordinate of "+ locStr(p.x,p.y));
                }

            }
        }
        //check for shared bits
        if ((ourBitBoard & opponentBitBoard) != 0){
            System.out.println("bitboards share pieces");
            ok = false;
        }
        //check that players are not in opponents goals
        if ((ourGoalMask & opponentBitBoard) != 0){
            System.out.println(colorStr(1-ourColor) + " has pieces in opponents goal");
            ok = false;
        }
        if ((opponentGoalMask & ourBitBoard) != 0){
            System.out.println(colorStr(ourColor) + " has pieces in opponents goal");
            ok = false;
        }

        //check piece counts
        if (ourPieceCount > 10){
            System.out.println(colorStr(ourColor) + " has "+ ourPieceCount + " pieces");
            ok = false;
        }
        if (opponentPieceCount > 10){
            System.out.println(colorStr(1-ourColor) + " has "+ opponentPieceCount + " pieces");
            ok = false;
        }

        return ok;
    }
    //construct a board from a string representation of it.
    //'x' for black pieces, 'o' for white piece (case does not matter).
    //example:
    // Board(color,
    //       "    x   " +
    //       "      o " +
    //       " xx  o  " +
    //       "        " +
    //       " o   x  " +
    //       " x      " +
    //       " x      " +
    //       "        ")
    //
    public Board (int color, String boardString){
        this(color);
        int whiteCount=0, blackCount=0;
        Move m;
        boardString = boardString.toLowerCase();
        if (boardString.length() != 64){
            System.out.println("Error --Board.Board(int, String)-- invalid board string");
        }
        char[] chars = boardString.toCharArray();
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                switch (boardString.charAt(y*8 + x)){
                case ' ' :
                    continue;
                case 'x' :
                    m = new Move(x, y);
                    if (ourColor == black){
                        move(m);
                    }else{
                        opponentMove(m);
                    }
                    blackCount++;
                    continue;
                case 'o' :
                    m = new Move(x,y);
                    if (ourColor == white){
                        move(m);
                    }else{
                        opponentMove(m);
                    }
                    whiteCount++;
                    continue;
                default:
                    System.out.println("Error - Board.Board(int, String)- invalid char");
                }
            }
        }
        if (whiteCount > 10 || blackCount > 10){
            System.out.println("Error - Board.Board(int, String) - constructed illegal board");
        }
    }

    public Cell[][] toCellArray(){
        Piece piece;
        Cell[][] cells = new Cell[8][8];

        Cell cell;
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){

                piece = pieceArray[x+1][y+1];
                cell = new Cell(x, y);
                cells[x][y] = cell;
                if (piece == null){
                    cell.defaultChar = " ";
                }else if (piece == edge){
                    cell.isEdge = true;
                }else if (piece.color == black){
                    cell.defaultChar = "X";
                }else {
                    cell.defaultChar = "~";
                }
            }
        }
        return cells;
    }

    public PrintBoard toPrintBoard(){
        return new PrintBoard(this);
    }

    public String toString(){
        //return charArrayToString(toCharArray());
        return toPrintBoard().toString();

    }
    //convert a bitboard/mask to a string representation
    private String bitBoardToString(long bitBoard){
        char[][] chars = new char[8][8];

        String str = "";
        //have to iterate throught each row first because of the way
        // the `bitReps' array is indexed in `getBitRep()'
        for (int y = 0; y<8; y++){
            for (int x = 0; x <8; x++){
                str += ((getBitRep(x,y) & bitBoard) != 0) ? "X" : "_";
            }
            str += "\n";
        }
        return str;
    }

    private boolean isValidIndex(int x, int y){
        if (x < 0 || x > 7 || y < 0  || y > 7
            || (x == 0 && y == 0)
            || (x == 7 && y == 0)
            || (x == 0 && y == 7)
            || (x == 7 && y == 7)){
            return false;
        }
        return true;
    }
    //check if N is a valid number for referencing squares
    private boolean isValidSquareRef(String n, boolean printMessages){
        int i,x,y;

        try{
            i = Integer.parseInt(n);
        }catch(NumberFormatException err){
            if (printMessages){
                System.out.println("Invalid number: '"+ n +"'");
            }
            return false;
        }

        x = i / 10;
        y = i % 10;
        if (isValidIndex(x,y)){
            return true;
        }
        if (printMessages){
            System.out.println("Invalid Index: ("+ x +"," +y+")");
        }
        return false;
    }

    private boolean isValidSquareRef(String n){
        return isValidSquareRef(n, true);
    }

    private int[] unpackIndexes(String n){
        if (! isValidSquareRef(n)){
            System.out.println("Error - Board.unpackIndexes: Invalid number");
        }
        int[] ret = new int[2];
        int i = Integer.parseInt(n);
        ret[0] = i / 10;
        ret[1] = i % 10;
        return ret;
    }

    private String colorStr(int color){
        switch (color){
        case 0: return "black";
        case 1: return "white";
        default: return "rainbow";
        }
    }

    public void interactiveDebug(){

        BufferedReader keybd = new BufferedReader(new InputStreamReader(System.in));
        Stack<Move> history = new Stack<Move>();
        String input = "";
        String[] splitInput = null;
        int nArgs = 0;
        String command = "";
        int color = ourColor;
        PrintBoard pb = toPrintBoard();

        boolean showNumbers = true;
        boolean loop = true;
        //set to true when the single arg is a valid index
        boolean arg1isRef = false,
            arg2isRef = false;
        int[] tmp = null;
        int argX1 = 0, argY1 = 0,
            argX2 = 0, argY2 = 0;
        String arg1 = "", arg2 = "";
        boolean inhibitBoardPrint = false;
        boolean fakeInput = false;
        boolean showBitBoards = true;
        Move m = null;
        //keeping track of messages this way is allows us to print the board before the
        //messages
        Deque<String> messages = new ArrayDeque<String>();

        //TODO: ::Q split up a string
        //      ::Q string to number

        System.out.println(pb.toString());
        System.out.print("you are color " + colorStr(color).toUpperCase());
        System.out.print(color == ourColor ? "" : " (your opponent)");
        System.out.println(". (Use commands 'white' & 'black' to switch)");
        System.out.println("Use command 'help' to print options");
        while (loop){
            verify();
            pb = toPrintBoard();
            System.out.print(">>> ");

            if (!fakeInput){
                try{
                    input = keybd.readLine();
                }catch (IOException err){
                    System.out.println("Error reading input");
                }
                if (input.equals("")){
                    input = "print";
                }
            }
            fakeInput = false;
            inhibitBoardPrint = false;
            splitInput = input.split("[ ]+");
            nArgs = splitInput.length -1;
            command = splitInput[0];
            arg1isRef = false;
            arg2isRef = false;
            arg1 = arg2 = "<none>";
            if (nArgs >= 1){
                if (isValidSquareRef(splitInput[1], false)){
                    arg1isRef = true;
                    tmp = unpackIndexes(splitInput[1]);
                    argX1 = tmp[0];
                    argY1 = tmp[1];
                }
                arg1 = splitInput[1];
            }
            if (nArgs >= 2){
                if (isValidSquareRef(splitInput[2], false)){
                    arg2isRef = true;
                    tmp = unpackIndexes(splitInput[2]);
                    argX2 = tmp[0];
                    argY2 = tmp[1];
                }
                arg2 = splitInput[2];
            }

            switch(command.toLowerCase()){

            case "help": case "h":
                interactiveHelp(messages);
                //inhibitBoardPrint = true;
                break;

            case "white": case "w":
                color = white;
                break;
            case "black": case "b":
                color = black;
                break;
            case "mark":
                if (arg1isRef){
                    pb.mark(argX1, argY1);
                } break;
                // case "us":
                //     Piece p;
                //     for (int x = 1; x < 9; x++){
                //         for (int y = 1; y < 9; y++){
                //             p = pieceArray[x][y];
                //             if (piece != null && piece != edge && piece.color == ourColor){

                //             }

                //         }
                //     }
                //     break;
            case "them":
                // highlight their pieces
                break;
            case "shownumbers": case "shownums": case "shown":
                //toggle box numberings;
                pb.showNumbers();
                showNumbers = true;
                messages.add("Showing numbers");
                break;
            case "hidenumbers": case "hidenums": case "hiden":
                pb.hideNumbers();
                showNumbers = false;
                messages.add("Hiding numbers");
                break;

            case "showbitboards": case "showbb": //ok
                showBitBoards = true;
                messages.add("Displaying bit boards");
                break;

            case "hidebitboards": case "hidebb": //ok
                showBitBoards = false;
                messages.add("Hiding bit boards");
                break;

            case "add": case "a": //ok
                //g&t 9.24
                if (arg1isRef){
                    if (((color == ourColor) && (ourPieceCount >= 10))
                        ||((color != ourColor) && (opponentPieceCount >= 10))){
                            messages.add("Cannot add more then 10 pieces");
                            break;
                        }
                        m = new Move(argX1, argY1);
                        input = "_applyMove";
                    fakeInput = true;
                }else{
                    messages.add("Invalid arg: " + arg1);
                }
                break;

            case "move": case "mov": case "mv": case "m": //ok
                //move <from> <to>
                if (arg1isRef && arg2isRef){
                    m = new Move(argX2, argY2, argX1, argY1);
                    input = "_applyMove";
                    fakeInput = true;
                }else{
                    messages.add("Invalid arg(s): " + arg1 + "," + arg2);
                }
                break;

                //from lecture: transposition tables
            case "remove": case "rem": case "r":
            case "delete": case "del": case "d":
                messages.add("Not Implemented");
                break;

            case "_applymove": //ok
                System.out.println("applying move...");
                if (m != null){
                    move(m,color);
                    history.push(m);
                    pb = toPrintBoard(); //update board
                    messages.add("Made move: " + m.toString());
                    if (m.moveKind == Move.ADD){
                        if (color == ourColor){
                            messages.add(colorStr(color) +" now has " + ourPieceCount + " pieces");
                        }else{
                            messages.add(colorStr(color) +" now has " + opponentPieceCount + " pieces");
                        }
                    }
                }else{
                    System.out.println("Error: invalid move");
                }
                break;

            case "undo": case "u": //ok
                if (color != ourColor){
                    messages.add("Cannot undo opponents moves");
                    break;
                }
                if (history.empty()){
                    messages.add("No history to undo");
                    break;
                }
                Move mv = history.pop();
                unMove(mv);
                pb = toPrintBoard(); //update board
                messages.add("Undid move: " + mv);
                break;

            case "connected": case "connect": case "c":
                if (arg1isRef){
                    Piece p;
                    PieceList pieces = connectedPieces(argX1, argY1);
                    if (pieces == null){
                        messages.add("no piece at ("+argX1+","+argY1+")");
                        break;
                    }

                    for (Piece pp: pieces){
                        pb.drawLine(argX1, argY1, pp.x, pp.y);
                        System.out.print("(" + pp.x + "," + pp.y + ")");
                    }
                    System.out.println("");
                    messages.add("found " + pieces.length() + " pieces");
                }
                break;

            case "adjacent": case "around": case "surround": case "s": //ok
                if (arg1isRef){
                    pb.mark(adjacentPieces(argX1, argY1));
                }
                break;

            case "line": //line <from> <to>
                if (arg1isRef && arg2isRef){
                    pb.drawLine(argX1, argY1, argX2, argY2);
                    messages.add("Added new line: ("+ argX1+","+ argY1+") -> ("+ argX2 +","+ argY2+")");
                }else{
                    messages.add("Invalid arg(s): " + arg1 + "," + arg2);
                }
                break;

            case "validmoves":
                System.out.println("Valid moves:");
                AList<Move> validMoves = new AList<Move>(1);
                validMoves = validMoves(color);
                AListIterator iter = new AListIterator(new Integer[] {1,2,3,4,5}, 0);
                iter = validMoves.iterator();
                while (iter.hasNext())
                    System.out.print(iter.next() + ", ");
                break;

                //idea: use transposition table from last move to help order the moves
            case "verify": case "valid": case "v": //ok
                if (verify()){
                    messages.add("Everything seems OK.");
                }else{
                    messages.add("Board is corrupted.");
                }
                break;

            case "invalid": case "illegal": case "i": //invalid moves
                messages.add("Not Implemented");
                break;
            case "network": case "net": case "n":
                // visually show the detected network
                messages.add(hasNetwork(color, pb) ? "YES" : "NO");
                break;
            case "network?": case "net?": case "n?":
                System.out.println((hasNetwork(color) ? "YES": "NO"));
                break;
            case "moves": //ok
                AList<Move> moves = validMoves(color);
                messages.add("found " + moves.length() +" moves");
                System.out.print("moves: ");
                for (Move move : moves){
                    System.out.print(locStr(move.x1, move.y1));
                    //                    pb.mark(move.x1,move.y2);
                }
                pb.mark(moves);
                break;

            case "pieceat": case "pa": //Piece At    //ok
                if (arg1isRef){
                    Piece p = pieceArray[argX1+1][argY1+1];
                    String loc = locStr(argX1, argY1);
                    if (p == null){
                        messages.add("No piece at "+ loc);
                    }else if (p == edge){
                        messages.add(loc + " is an edge piece");
                    }else{
                        messages.add("Piece at " + loc);
                        messages.add("  Color = " + colorStr(p.color));
                        messages.add("  bitRep = " + p.bitRep);
                        messages.add("  x,y = " + p.x + "," + p.y);
                    }
                }else{
                    messages.add("Invalid arg: " + arg1);
                }
                break;

            case "clusters": case "cluster": case "clus"://ok
                int c = 0;
                Move mov;
                for (int x = 0; x<8; x++){
                    for (int y = 0; y<8; y++){
                        mov = new  Move(x,y);
                        if (isValidIndex(x,y) && formsIllegalCluster(mov, color)){
                            pb.mark(x,y);
                            c++;
                        }
                    }
                }
                messages.add("found "+c+" illegal squares");
                break;

            case "markall": //ok
                int c2 = 0;
                for (int x = 0; x<8; x++){
                    for (int y = 0; y<8; y++){
                        mov = new  Move(x,y);
                        if (pieceAt(x,y)){
                            pb.mark(x,y);
                            c2++;
                        }
                    }
                }
                messages.add("found "+c2+" pieces");
                break;

            case "goalpieces": //ok
                PieceList pl = getStartGoalPieces(color);
                pb.mark(pl);
                messages.add("Found " + pl.length() + " network start pieces");
                break;

            case "goalmaska": case "gmaska"://Goal Mask A  //ok
                long goalmask = (color == ourColor ? ourGoalMaskA : opponentGoalMaskA);
                Piece p;
                int c3=0;
                for (int x = 1; x<9; x++){
                    for (int y = 1; y<9; y++){
                        p = pieceArray[x][y];
                        if (p != null && p != edge && (p.bitRep & goalmask) != 0){
                            pb.mark(x-1,y-1);
                            c3++;
                        }
                    }
                }
                messages.add("found " + c3 + " goal A pieces");
                break;

            case "goalmaskb": case "gmaskb"://Goal Mask B  //ok
                long goalmask2 = (color == ourColor ? ourGoalMaskB : opponentGoalMaskB);
                Piece p2;
                int c4=0;
                for (int x = 1; x<9; x++){
                    for (int y = 1; y<9; y++){
                        p2 = pieceArray[x][y];
                        if (p2 != null && p2 != edge && (p2.bitRep & goalmask2)!=0){
                            pb.mark(x-1,y-1);
                            c4++;
                        }
                    }
                }
                messages.add("found " + c4 + " goal A pieces");
                break;

            case "print":
                break;
            case "exit": case "quit": case "done":
                loop = false;
                break;

            default:
                messages.add("Invalid Command");
            }

            if (!inhibitBoardPrint){
                System.out.println("\n\n\n\n\n");
                if (!showNumbers){
                    pb.hideNumbers();
                }
                if (showBitBoards){
                    System.out.println(pb.toString(bitBoardToString(ourBitBoard),
                                                   bitBoardToString(opponentBitBoard)));
                }else{
                    System.out.println(pb.toString());
                }

                System.out.print("you are color " + colorStr(color).toUpperCase());
                System.out.println(color == ourColor ? "" : " (your opponent)");
                while (messages.size() != 0){
                    System.out.println(messages.remove());
                }
            }
        }
        System.out.println("done");
    }

    private void interactiveHelp(Deque<String> messages){
        String [] lines = {
            "\nAvailable commands -----------------------",
            "(Case does not matter)",
            "'add' <num>  ",
            "'move' <from> <to> ",
            "'undo'      undo the last move",
            "'black'     run commands as the black player",
            "'white'     run commands as the white player",
            "'hideBB'    hide the bitboards",
            "'showBB'    display the bitboards",
            "'hideNums'  hide square numbers",
            "'showNums'  display the square numbers",
            "'around' <num>    mark pieces that surround <num>",
            "'connect' <num>   draw lines to pieces connected to <num>",
            "'validmoves'    displays valid moves"};

        for (String line: lines){
            messages.add(line);
        }
    }

    private boolean miscTests(){
        boolean pass = true;
        //tests for isValidSquareRef ===================================
        String[] invalidNums = {"df", "99", "-12","00", "07","7","77", "100"};
        String[] validNums = {"23", "34", "04", "1","01", "066", "001"};
        System.out.println("Invalid nums:");

        for (String s :invalidNums){
            System.out.print(s);
            if (isValidSquareRef(s)){
                System.out.println("Error: isValidSquareRef(" + s + ") should be false");
                pass = false;
            }
        }
        System.out.println("valid nums:");
        for (String s :validNums){
            if (! isValidSquareRef(s)){
                System.out.println("Error: isValidSquareRef(" + s + ") should be true");
                pass = false;
            }

        }
        return pass;
    }

    public static void main(String[] args){
        // Board b = new Board(white,
        //                     " oo   x " +
        //                     "   o ox " +
        //                     "  o  o o" +
        //                     "xo o   o" +
        //                     "   x    " +
        //                     " x   o  " +
        //                     " o      " +
        //                     " o o xx ");
        Board b = new Board(white,
                            "      x " +
                            " o    x " +
                            "   o   o" +
                            "o    o  " +
                            "   x o  " +
                            " x     o" +
                            " o o   o" +
                            "     xx ");

        PrintBoard pb = b.toPrintBoard();

        //left/right and upper/lower goals masks are switched
        // System.out.println(b.bitBoardToString(b.leftGoalMask));
        // System.out.println(b.bitBoardToString(b.ourBitBoard & b.leftGoalMask));
        //b.test();
        b.interactiveDebug();
        //

    }
}

