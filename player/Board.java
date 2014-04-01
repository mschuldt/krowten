// player/Board.java

package player;

import java.io.*;

public class Board{
    Piece[][] pieceArray;

    Piece ourGoalListA, ourGoalListB;
    Piece opponentGoalListA, opponentGoalListB;
    Piece[] rows;
    Piece[] columns;
    //forward and backward diagonals (think forward/backward slash)
    Piece[] fDiagonals;
    Piece[] bDiagonals;

    int ourColor, opponentColor;
    int ourPieceCount, opponentPieceCount;
    int ourNumInGoalA,ourNumInGoalB,
        opponentNumInGoalA,opponentNumInGoalB;

    public static final int white = 1;
    public static final int black = 0;
    long ourBitBoard = 0;
    long opponentBitBoard = 0;

    PieceList pieces; //all unused pieces
    PieceList ourPieces;
    PieceList opponentPieces;

    PieceList adjacentPieceList;

    Move m = new Move(0,0);

    boolean verifyAll = true; //when true, run this.verify() after every move
    // because the corners of the gameboard cannot be used, the last bit is
    // not needed (actually the last two). This is lucky because java has no
    // equivalent of an unsigned long integer
    //
    // The `bitReps' array was generated with this python code:
    // "{0, " + ", ".join([str(hex(int("1" + "0"*x, 2))) + "L" for x in range(64)]) + "}"
    final long[] bitReps = {0x1L, 0x2L, 0x4L, 0x8L, 0x10L, 0x20L, 0x40L, 0x80L, 0x100L,
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
    final long cornersMask = 0x4081000000000081L,
        upperGoalMask = 0x7e00000000000000L,
        lowerGoalMask = 0x7e,
        rightGoalMask = 0x1010101010100L,
        leftGoalMask = 0x80808080808000L;
    long ourGoalMaskA,
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
        ourNumInGoalA = ourNumInGoalB = 0;
        opponentNumInGoalA = opponentNumInGoalB = 0;

        ourColor = color; //0 for black, 1 for white
        opponentColor = 1-color;

        pieceArray = new Piece[10][10];


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

        ourPieces = new PieceList(10);
        opponentPieces = new PieceList(10);

        pieces = new PieceList(20);
        for (int i = 0; i < 20; i++){
            pieces.add(new Piece());
        }

        adjacentPieceList = new PieceList(8);
        rows = new Piece[8];
        columns = new Piece[8];
        fDiagonals = new Piece[14];
        bDiagonals = new Piece[14];

        Piece ourGoalListA, ourGoalListB;
        Piece oppoentGoalListA, oppoentGoalListB;


        //NOTE: if this goal mask assignment is changed, then
        //      code in getStartGoalPieces must be updated.
        if (ourColor == white){ //white's goals are on the left and right
            ourGoalMaskA = leftGoalMask;
            ourGoalMaskB = rightGoalMask;
            opponentGoalMaskA = lowerGoalMask;
            opponentGoalMaskB = upperGoalMask;
            //remember these are backwards because of the goal mask confusion
            ourGoalListA = columns[7];
            ourGoalListB = columns[0];
            opponentGoalListA = rows[0];
            opponentGoalListB = rows[7];
        }else{
            ourGoalMaskA = lowerGoalMask;
            ourGoalMaskB = upperGoalMask;
            opponentGoalMaskA = leftGoalMask;
            opponentGoalMaskB = rightGoalMask;

            ourGoalListA = rows[0];
            ourGoalListB = rows[7];
            opponentGoalListA = columns[7];
            opponentGoalListB = columns[0];
        }

        ourGoalMask = (ourGoalMaskA | ourGoalMaskB);
        opponentGoalMask = (opponentGoalMaskA | opponentGoalMaskB);

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
    public void move(Move move, int color){
        int toX, toY;
        long bitRep;
        Piece p;
        switch (move.moveKind){
        case Move.ADD :
            toX = move.x1 + 1;
            toY = move.y1 + 1;
            bitRep = getBitRep(toX-1, toY-1);
            p = pieces.pop().set(color, bitRep, move.x1, move.y1);

            //TODO: asserts to check index validity
            assert pieceArray[toX][toY] == null : "square is already full";

            if (color == ourColor){
                ourBitBoard |= bitRep;
                ourPieceCount++;
                if ((bitRep & ourGoalMaskA) != 0){
                    ourNumInGoalA++;
                }else if ((bitRep & ourGoalMaskB) != 0){
                    ourNumInGoalB++;
                }
                assert ourPieceCount <= 10 : colorStr(color) + " has more then 10 pieces";

                ourPieces.add(p);

            }else{
                opponentBitBoard |= bitRep;
                opponentPieceCount++;
                if ((bitRep & opponentGoalMaskA) != 0){
                    opponentNumInGoalA++;
                }else if ((bitRep & opponentGoalMaskB) != 0){
                    opponentNumInGoalB++;
                }
                assert opponentPieceCount <= 10 : colorStr(color) + " has more then 10 pieces";

                opponentPieces.add(p);
            }

            //pieceArray[toX][toY] = new Piece(color, bitRep, move.x1, move.y1); //FIX

            pieceArray[toX][toY] = p;
            addToMatrix(p);
            break;

        case Move.STEP :
            int fromX = move.x2 + 1,
                fromY = move.y2 + 1;

            toX = move.x1 + 1;
            toY = move.y1 + 1;

            long toBitRep = getBitRep(toX-1, toY-1);
            long fromBitRep = pieceArray[fromX][fromY].bitRep;

            assert pieceArray[toX][toY] == null : "square is already full";
            assert pieceArray[fromX][fromY] != null : "square is empty";
            assert pieceArray[fromX][fromY].color == color : "cannot move opponents piece";

            if (color == ourColor){
                //remove old location
                ourBitBoard ^= pieceArray[fromX][fromY].bitRep;
                //add new location
                ourBitBoard |= toBitRep;
                //increment counter if moveing to a goal
                if ((toBitRep & ourGoalMaskA) != 0){
                    ourNumInGoalA++;
                }else if ((toBitRep & ourGoalMaskB) != 0){
                    ourNumInGoalB++;
                }
                //decrease counter is removing frm a goal
                if ((fromBitRep & ourGoalMaskA) != 0){
                    ourNumInGoalA--;
                }else if ((fromBitRep & ourGoalMaskB) != 0){
                    ourNumInGoalB--;
                }
            }else{
                opponentBitBoard ^= pieceArray[fromX][fromY].bitRep;
                opponentBitBoard |= toBitRep;

                if ((toBitRep & opponentGoalMaskA) != 0){
                    opponentNumInGoalA++;
                }else if ((toBitRep & opponentGoalMaskB) != 0){
                    opponentNumInGoalB++;
                }
                if ((fromBitRep & opponentGoalMaskA) != 0){
                    opponentNumInGoalA--;
                }else if ((fromBitRep & opponentGoalMaskB) != 0){
                    opponentNumInGoalB--;
                }
            }
            p = pieceArray[fromX][fromY];
            removeFromMatrix(p);
            pieceArray[toX][toY] = p;
            p.bitRep = getBitRep(toX-1, toY-1);
            p.x = toX-1;
            p.y = toY-1;
            pieceArray[fromX][fromY] = null;
            addToMatrix(p);
            break;
        case Move.QUIT :
            //TODO
            break;
        }
        if (verifyAll){
            verify();
        }
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
        Piece pp = null;
        switch (move.moveKind){
        case Move.ADD :
            int x = move.x1 + 1,
                y = move.y1 + 1;
            //TODO: asserts to check index validity
            p = pieceArray[x][y];
            assert p != null : "square should not be empty";
            assert p != edge : "cannot undo: piece is an edge";

            long bitRep = p.bitRep;

            if (p.color == ourColor){
                ourBitBoard ^= p.bitRep;
                ourPieceCount--;

                if ((bitRep & ourGoalMaskA) != 0){
                    ourNumInGoalA--;
                }else if ((bitRep & ourGoalMaskB) != 0){
                    ourNumInGoalB--;
                }

                pp = ourPieces.pop();
                assert pp == p : "pop removed wrong piece from `ourPieces'";

            }else{
                opponentBitBoard ^= p.bitRep;
                opponentPieceCount--;

                if ((bitRep & opponentGoalMaskA) != 0){
                    opponentNumInGoalA--;
                }else if ((bitRep & opponentGoalMaskB) != 0){
                    opponentNumInGoalB--;
                }
                pp = opponentPieces.pop();
                assert pp == p : "pop removed wrong piece from `opponentPieces'";
            }
            p = pieceArray[x][y];
            pieces.add(p);
            removeFromMatrix(p);
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

            long fromBitRep = p.bitRep;

            if (p.color == ourColor){
                ourBitBoard ^= p.bitRep;
                ourBitBoard |= toBitRep;

                if ((fromBitRep & ourGoalMaskA) != 0){
                    ourNumInGoalA--;
                }else if ((fromBitRep & ourGoalMaskB) != 0){
                    ourNumInGoalB--;
                }
                if ((toBitRep & ourGoalMaskA) != 0){
                    ourNumInGoalA++;
                }else if ((toBitRep & ourGoalMaskB) != 0){
                    ourNumInGoalB++;
                }
            }else{
                opponentBitBoard ^= p.bitRep;
                opponentBitBoard |= toBitRep;

                if ((fromBitRep & opponentGoalMaskA) != 0){
                    opponentNumInGoalA--;
                }else if ((fromBitRep & opponentGoalMaskB) != 0){
                    opponentNumInGoalB--;
                }
                if ((toBitRep & opponentGoalMaskA) != 0){
                    opponentNumInGoalA++;
                }else if ((toBitRep & opponentGoalMaskB) != 0){
                    opponentNumInGoalB++;
                }
            }
            removeFromMatrix(p);
            p.bitRep = toBitRep;
            p.x = toX-1;
            p.y = toY-1;
            pieceArray[toX][toY] = p;
            addToMatrix(p);
            pieceArray[fromX][fromY] = null;
            break;

        case Move.QUIT :
            //TODO
            break;
        }
        if (verifyAll){
            verify();
        }
    }

    // //add P to linked Piece list PIECES.
    // //P is added so that its x and y fields are in the appropriate order
    // private void addToLinkedPieceList(Piece pieces, Piece p){

    // }

    //add a piece to the linked matrix
    private void addToMatrix(Piece p){
        Piece curr = null;
        int x = p.x;
        int y = p.y;
        //correct: left, up

        //add to row
        curr = rows[y];
        if (curr == null){
            rows[y] = p;
        }else if(curr.x > p.x){ //add to front
            p.right = curr;
            curr.left = p;
            rows[y] = p;
        }
        else{ //add to middle or end
            while (curr.right != null && curr.right.x < x){
                curr = curr.right;
            }
            assert curr.right == null || curr.right.x != x
                : "a piece already exists in the matrix (row)";
            p.left = curr;
            p.right = curr.right;
            if (curr.right != null){
                curr.right.left = p;
            }
            curr.right = p;
        }

        //add to column
        curr = columns[x];
        if (curr == null){
            columns[x] = p;
        }else if(curr.y >  p.y){
            p.down = curr;
            curr.up = p;
            columns[x] = p;
        }else{
            while (curr.down != null && curr.down.y < y){
                curr = curr.down;
            }
            assert curr.down == null || curr.down.y != y
                : "a piece already exists in the matrix (column)";
            p.up = curr;
            p.down = curr.down;
            if (curr.down != null){
                curr.down.up = p;
            }
            curr.down = p;
        }

        //add to forward diagonal
        curr = fDiagonals[x+y];
        if (curr == null){
            fDiagonals[x+y] = p;
        }else if(curr.y >  p.y){
            p.leftDown = curr;
            curr.rightUp = p;
            fDiagonals[x+y] = p;
        }else{
            while (curr.leftDown != null && curr.leftDown.x > x){
                curr = curr.leftDown;
            }
            assert curr.leftDown == null || curr.leftDown.x != x
                : "a piece already exists in the matrix (forward diagonal)";
            p.rightUp = curr;
            p.leftDown = curr.leftDown;
            if (curr.leftDown != null){
                curr.leftDown.rightUp = p;
            }
            curr.leftDown = p;
        }

        //add to backward diagonal
        curr = bDiagonals[y-x+6];
        if (curr == null){
            bDiagonals[y-x+6] = p;
        }else if(curr.y >  p.y){
            p.rightDown = curr;
            curr.leftUp = p;
            bDiagonals[y-x+6] = p;
        }else{
            while (curr.rightDown != null && curr.rightDown.x < x){
                curr = curr.rightDown;
            }
            assert curr.rightDown == null || curr.rightDown.x != x
                : "a piece already exists in the matrix (backward diagonal)";
            p.leftUp = curr;
            p.rightDown = curr.rightDown;
            if (curr.rightDown != null){
                curr.rightDown.leftUp = p;
            }
            curr.rightDown = p;
        }

    }

    //remove a piece from the linked matrix
    private void removeFromMatrix(Piece p){
        System.out.println("removing " + p);
        //remove from row
        if (p.left == null){
            if (p.right == null){
                rows[p.y] = null;
            }
        }else{
            p.left.right = p.right;
        }
        if (p.right != null){
            p.right.left = p.left;
        }
        //remove from column
        if (p.up == null){
            if (p.down == null){
                columns[p.x] = null;
            }
        }else{
            p.up.down = p.down;
        }
        if (p.down != null){
            p.down.up = p.up;
        }

        //remove from forward diagonal
        if (p.leftDown == null){
            if (p.rightUp == null){
                fDiagonals[p.y+p.x] = null;
            }
        }else{
            p.leftDown.rightUp = p.rightUp;
        }
        if (p.rightUp != null){
            p.rightUp.leftDown = p.leftDown;
        }
        //remove from backward diagonal
        if (p.rightDown == null){
            if (p.leftUp == null){
                bDiagonals[p.y-p.x+6] = null;
            }
        }else{
            p.rightDown.leftUp = p.leftUp;
        }
        if (p.leftUp != null){
            p.leftUp.rightDown = p.rightDown;
        }

        p.left = p.right = p.up = p.down = null;
        p.leftUp = p.leftDown = p.rightUp = p.rightDown = null;
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
     *
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
        return adjacentPieces(piece.x, piece.y, piece.color);
    }

    //TODO: interface docs
    public PieceList adjacentPieces(Piece piece, int color){
        return adjacentPieces(piece.x, piece.y, color);

    }

    public void updateAdjacentPiecesList(int x, int y, int color){
        x++; y++; //mbs
        adjacentPieceList.clear();
        for (int xAdj = -1; xAdj <= 1; xAdj++){
            for (int yAdj = -1; yAdj <= 1; yAdj++){
                adjacentPieceList.addIfColor(pieceArray[x+xAdj][y+yAdj], color);
            }
        }
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
            while (current == null){
                currentX += xInc;
                currentY += yInc;
                current = pieceArray[currentX][currentY];
            }
            if (current != edge){
                pieces.addIfColor(current, color);
            }
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
            for (int y=1; y<7; y++){
                lst.addIfColor(getPiece(0, y), color);
            }
        }else{//black starts at bottom
            for (int x=1; x<7; x++){
                lst.addIfColor(getPiece(x, 7), color);
            }
        }
        return lst;
    }

    private boolean hasNetwork(Piece currentPiece, long bitBoard, long memberPieces,long goalmask,
                               int m, int b, int depth){
        int newM, newB;
        PieceList pl = connectedPieces(currentPiece);
        if (pl == null){
            return false;
        }
        for (Piece piece : pl){
            if (piece.x == currentPiece.x){
                newM = 10;
                newB = piece.x;
            }else{
                newM = (piece.y - currentPiece.y)/(piece.x - currentPiece.x);
                newB = piece.y - newM*piece.x;
            }
            if ((newM == m) && (newB == b)){
                continue; //on the same line
            }
            if ((piece.bitRep & goalmask) != 0){
                if (depth >= 5){//5 because depth does not include this 'piece'
                    return true;
                }
                continue; //can't visit a goal piece until the end
            }
            if ((piece.bitRep & memberPieces) != 0){
                continue; // we have already visited this piece
            }
            if (hasNetwork(piece, bitBoard, memberPieces | currentPiece.bitRep, goalmask, newM, newB, depth+1)){
                return true;
            }
        }
        return false;
    }

    public boolean hasNetwork(int color){
        if ((color == ourColor ? ourPieceCount : opponentPieceCount) < 6){
            return false;
        }
        long bitBoard, goalA, goalB;

        if (color == ourColor){
            bitBoard = ourBitBoard;
            goalA = ourGoalMaskA;
            goalB = ourGoalMaskB;
        }else{
            bitBoard = opponentBitBoard;
            goalA = opponentGoalMaskA;
            goalB = opponentGoalMaskB;
        }

        if (((bitBoard & goalA) != 0) && ((bitBoard & goalB) != 0)){
            for (Piece piece : getStartGoalPieces(color)){
                if (hasNetwork(piece, bitBoard, goalB, goalA, 11, 60,1)){ //11x+60: just an impossible line
                    return true;
                }
            }
        }
        return false; //does not have at lease one piece in each goal
    }

    //version of hasNetwork that takes a printBoard so that it can draw the network lines on it
    private boolean hasNetwork(Piece currentPiece, long bitBoard, long memberPieces,long goalmask,
                               int m, int b, int depth, PrintBoard pb){
        int newM, newB;
        PieceList pl = connectedPieces(currentPiece);
        if (pl == null){
            return false;
        }
        for (Piece piece : pl){
            if (piece.x == currentPiece.x){
                newM = 10;
                newB = piece.x;
            }else{
                newM = (piece.y - currentPiece.y)/(piece.x - currentPiece.x);
                newB = piece.y - newM*piece.x;
            }
            if ((newM == m) && (newB == b)){
                continue; //on the same line
            }
            if ((piece.bitRep & goalmask) != 0){
                if (depth >= 5){//5 because depth does not include this 'piece'
                    pb.drawLine(currentPiece.x, currentPiece.y, piece.x, piece.y);
                    return true;
                }
                continue; //can't visit a goal piece until the end
            }
            if ((piece.bitRep & memberPieces) != 0){
                continue; // we have already visited this piece
            }
            if (hasNetwork(piece, bitBoard, memberPieces | currentPiece.bitRep, goalmask, newM, newB, depth+1,pb)){
                pb.drawLine(currentPiece.x, currentPiece.y, piece.x, piece.y);
                return true;
            }
        }
        return false;
    }
    public boolean hasNetwork(int color, PrintBoard pb){
        if ((color == ourColor ? ourPieceCount : opponentPieceCount) < 6){
            return false;
        }
        long bitBoard, goalA, goalB;

        if (color == ourColor){
            bitBoard = ourBitBoard;
            goalA = ourGoalMaskA;
            goalB = ourGoalMaskB;
        }else{
            bitBoard = opponentBitBoard;
            goalA = opponentGoalMaskA;
            goalB = opponentGoalMaskB;
        }

        if (((bitBoard & goalA) != 0) && ((bitBoard & goalB) != 0)){
            for (Piece piece : getStartGoalPieces(color)){
                if (hasNetwork(piece, bitBoard, goalB, goalA, 11, 60,1, pb)){ //11x+60: just an impossible line
                    return true;
                }
            }
        }
        return false; //does not have at lease one piece in each goal
    }

    /**
     *  formsIllegalCluster returns true if Move m will result in a cluster of 3 or more pieces
     */
    public boolean formsIllegalCluster(Move m, int color){
        int x = m.x1;
        int y = m.y1;
        int numNeighbors;
        //since we are no longer doing the actual move, in the case that the move
        //is a step move we need to account for the case in which the location
        //we are moving the the piece from is also part of the adjacent pieces
        //the the square we are testing
        boolean stepMove = (m.moveKind == Move.STEP);
        long originBitRep=0;
        if (stepMove){
            originBitRep = getBitRep(m.x2, m.y2);
        }

        if (this.pieceAt(x,y)){
            return false; // for now... probably need to throw an error, but isValidMove will also take care of it
        }
        //PieceList neighbors = this.adjacentPieces(x, y, color); // get neighboring pieces of (presumably) the same color

        updateAdjacentPiecesList(x, y, color);


        numNeighbors = adjacentPieceList.length();
        if (stepMove && adjacentPieceList.containsPiece(originBitRep)){
            numNeighbors--;
        }
        if (numNeighbors >1){
            return true;
        }
        if (numNeighbors == 1){
            Piece oneNeighbor = adjacentPieceList.get(0);
            if (stepMove && oneNeighbor.bitRep == originBitRep){
                oneNeighbor = adjacentPieceList.get(1);
                //TODO: will there always be another?
            }

            //TODO: this does not work for some reason...
            PieceList moreNeighbors = this.adjacentPieces(oneNeighbor);
            //updateAdjacentPiecesList(oneNeighbor.x, oneNeighbor.y, oneNeighbor.color);
            //int moreNumNeighbors = adjacentPieceList.length();
            int moreNumNeighbors = moreNeighbors.length();
            if (stepMove &&
                //adjacentPieceList.containsPiece(originBitRep)){
                moreNeighbors.containsPiece(originBitRep)){
                moreNumNeighbors--;
            }
            if (moreNumNeighbors >0){
                return true;
            }
        }
        return false;
    }

    public boolean isValidMove(Move m, int color){
        int toX,toY,fromX,fromY;
        int pieceCount;
        long goal;
        if (color == ourColor){
            pieceCount = ourPieceCount;
            goal = opponentGoalMask;
        }else{
            pieceCount = opponentPieceCount;
            goal = ourGoalMask;
        }

        switch (m.moveKind){
        case Move.ADD:
            toX = m.x1;
            toY = m.y1;
            //check that indexes are valid
            if (! isValidIndex(toX, toY)){
                return false;
            }
            //check that destination square is empty
            if (pieceArray[toX+1][toY+1] != null){
                System.out.println("invalid add move: piece at " + locStr(toX,toY));
                return false;
            }
            //check for correct piece count
            if (pieceCount >= 10){
                System.out.println("invalid add move: piece count >= 10" );
                return false;
            }
            //make sure not to move into opponents goal
            if ((getBitRep(toX, toY) & goal) != 0){
                System.out.println("invalid add move: adding to opponents goal" );
                return false;
            }
            //check that no illegal cluster will form
            if (formsIllegalCluster(m, color)){
                System.out.println("invalid add move: forms illegal cluster" );
                return false;
            }
            return true;

        case Move.STEP:
            toX = m.x1;
            toY = m.y1;
            fromX = m.x2;
            fromY = m.y2;
            //check that destination indexes are valid
            if (! isValidIndex(toX, toY)){
                System.out.println("invalid step move: invalid destination index" + locStr(toX,toY));
                return false;
            }
            if (! isValidIndex(fromX, fromY)){
                System.out.println("invalid step move: invalid origin index" + locStr(fromX,fromY));
                return false;
            }
            //check that destination square is empty
            if (pieceArray[toX+1][toY+1] != null){
                System.out.println("invalid step move: destination square is full" + locStr(toX,toY));
                return false;
            }
            //make sure there is a piece to move
            if (pieceArray[fromX+1][fromY+1] == null){
                System.out.println("invalid step move: origin square is empty" + locStr(toX,toY));
                return false;
            }

            //check for correct piece count
            if (pieceCount < 10){
                System.out.println("invalid step move: piece count < 10");
                return false;
            }

            //make sure not to move into opponents goal
            if ((getBitRep(toX, toY) & goal) != 0){
                System.out.println("invalid step move: moving into opponents goal");
                return false;
            }

            //check that no illegal cluster will form
            if (formsIllegalCluster(m, color)){
                System.out.println("invalid step move: forming illegal cluster");
                // System.out.println("ourBitBoord:");
                // System.out.println(bitBoardToString(ourBitBoard));
                // System.out.println("opponentBitBoord:");
                // System.out.println(bitBoardToString(opponentBitBoard));
                // System.out.println("Board: ");
                System.out.println(toBoardString());
                return false;
            }

            //check that the move does not move to the same spot
            if (m.x1 == m.x2 && m.y1 == m.y2){
                System.out.println("invalid step move: moving to same spot");
                return false;
            }
        }

        return true;
    }

    private void setMove(Move m, int x1, int y1){
        m.x1 = x1;
        m.y1 = y1;
        m.moveKind = Move.ADD;
    }

    private void setMove(Move m, int x1, int y1, int x2, int y2){
        m.x1 = x1;
        m.y1 = y1;
        m.x2 = x2;
        m.y2 = y2;
        m.moveKind = Move.STEP;
    }

    //like validMoves but does not allocate any memory
    public void validMoves2(int color, MoveList mList){
        mList.clear();

        int numPieces = getNumPieces(color);
        PieceList pieces = getPieces(color);
        //AList<Move> mList = new AList<Move>(440);
        int x_lower, y_lower, x_upper, y_upper;
        if (color == black) {
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
                        setMove(m, x, y);
                        if (!formsIllegalCluster(m, color)){
                            mList.add(x, y);
                        }
                    }
                }
            }
        } else {                      // STEP moves
            for (int x = x_lower; x <= x_upper; x++) {
                for (int y = y_lower; y <= y_upper; y++) {
                    if (!pieceAt(x, y)) {
                        for (Piece p : pieces){
                            setMove(m, x, y, p.x, p.y);
                            if (!formsIllegalCluster(m, color)){
                                mList.add(x, y, p.x, p.y);
                            }
                        }
                    }
                }
            }
        }
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
        PieceList pieces = getPieces(color);
        AList<Move> mList = new AList<Move>(440);
        int x_lower, y_lower, x_upper, y_upper;
        if (color == black) {
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
                        if (!formsIllegalCluster(m, color)){
                            mList.add(m);
                        }
                    }
                }
            }
        } else {                      // STEP moves
            for (int x = x_lower; x <= x_upper; x++) {
                for (int y = y_lower; y <= y_upper; y++) {
                    if (!pieceAt(x, y)) {
                        for (Piece p : pieces){
                            Move m = new Move(x, y, p.x, p.y);
                            if (!formsIllegalCluster(m, color)){
                                mList.add(m);
                            }
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
    public PieceList getPieces(int color){
        PieceList pieces = new PieceList(10);
        Piece p = null;
        int i = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++){
                if (pieceAt(x, y)){
                    p = getPiece(x, y);
                    if (p.color == color){
                        pieces.add(p);
                    }
                }
            }
        }
        return pieces;
    }

    public int getNumPieces(int color){
        if (color == ourColor){
            return ourPieceCount;
        }
        return opponentPieceCount;
    }

    private int runLength(Piece currentPiece, long[] memberPieces,
                               int m, int b, int length){
        int newM, newB;
        int len= 0;
        long members = memberPieces[0];
        PieceList pl = connectedPieces(currentPiece);
        if (pl == null){
            return length;
        }
        for (Piece piece : pl){
            if (piece.x == currentPiece.x){
                newM = 10;
                newB = piece.x;
            }else{
                newM = (piece.y - currentPiece.y)/(piece.x - currentPiece.x);
                newB = piece.y - newM*piece.x;
            }
            if ((newM == m) && (newB == b)){
                continue; //on the same line
            }

            if ((piece.bitRep & members) != 0){
                continue; // we have already visited this piece
            }

            memberPieces[0] = (members | currentPiece.bitRep);
            len += runLength(piece, memberPieces, newM, newB, 1);
        }
        return length + len;
    }

    //returns the length of the partial network starting with STARTPIECE
    public int runLength(Piece startPiece,long[] memberPieces){

        long bitBoard, goalA, goalB;

        if (startPiece.color == ourColor){
            goalA = ourGoalMaskA;
            goalB = ourGoalMaskB;
        }else{
            goalA = opponentGoalMaskA;
            goalB = opponentGoalMaskB;
        }
        memberPieces[0] = (memberPieces[0] | goalA | goalB);
        return runLength(startPiece, memberPieces ,11, 60, 1);
    }

    int[][] whiteSquareValues = {{ 0, 0, 0, 0,  0, 0, 0, 0},
                                 {-3,-3, 2,-1,  2, 1, 0,-4},
                                 {-4,-3,-1,-3, -1,-1, 0,-3},
                                 {-2,-3, 4,-3,  4, 1, 0,-3},
                                 {-4,-3,-3,-3, -3,-1, 0,-4},
                                 {-2,-2, 3,-3,  3, 1, 0,-3},
                                 {-4,-2, 0,-3,  0,-1, 0,-3},
                                 {0 , 0, 0, 0,  0, 0, 0, 0}};

    //return the sum of the square values of each piece
    private int squareScoreSum(int color){
        int sum= 0;
        if (color == white){
            for (Piece p : getPieces(white)){
                sum += whiteSquareValues[p.y][p.x];
            }
        }else{
            for (Piece p : getPieces(black)){
                sum += whiteSquareValues[p.x][p.y];
            }
        }
        return sum;
    }

    //? interface change:
    //    MachinePlayer.scoreBoard(Board B, Player P)
    //    -> Baord.score(int color);
    //

    public int score(int color){
        assert ! hasNetwork(color): "Board.score: board has a network";

        int sum=squareScoreSum(color);
        PieceList pieces = getPieces(color);
        PieceList adjacent;
        for (Piece p: pieces){
            //sum the total connections
            //  sum += connectedPieces(p).length();
            //give points for pieces with no other adjacent pieces
            adjacent = adjacentPieces(p, color);
            if (adjacent.length() ==0){
                sum+=5;
            }
        }
        //give points for partial networks
        // for (Piece piece : getStartGoalPieces(color)){
        //     sum+=1.5*runLength(piece);
        //     //System.out.println("run length = " + runLength(piece));
        // }

        long[] memberPieces = {0};
        long br = 0;

        PieceList goalPieces = getStartGoalPieces(color);
        if (goalPieces.length() > 0){
            for (Piece p: goalPieces){
                br = p.bitRep;
                memberPieces[0]= (memberPieces[0] | br);
                sum+=3*runLength(goalPieces.pop(), memberPieces);
            }
        }

        for (Piece p : getPieces(color)){
            br = p.bitRep;
            if ((memberPieces[0] & br) == 0){
                memberPieces[0]= (memberPieces[0] | br);
                sum+= 3*runLength(p, memberPieces);
            }
        }

        //try to prevent more then one piece in each goal
        /*
        long a,b,board;
        boolean inA,inB;
        int numA, numB;
        if (color == ourColor){
            a = ourGoalMaskA;
            b = ourGoalMaskB;
            board = ourBitBoard;
            numA = ourNumInGoalA;
            numB = ourNumInGoalB;
        }else{
            a = opponentGoalMaskA;
            b = opponentGoalMaskB;
            board = opponentBitBoard;
            numA = opponentNumInGoalA;
            numB = opponentNumInGoalB;
        }
        if (numA > 1){
            sum -= ((numA-1)*10);
        }
        if (numB > 1){
            sum -= ((numB-1)*10);
        }
        if (sum<0){
            sum=0;
        }

        //make sure they have a piece in each goal
        if (numA == 0 || numB == 0){
            sum/=2;
        }
        */

        return sum;
    }

    public int score(){
        return score(ourColor) - score(opponentColor);

    }

    //==========================================================================
    // Verification and testing code ===========================================
    //==========================================================================

    private String locStr(int x, int y){
        return "(" + x + "," + y +")";
    }

    public boolean verifyBitBoards(){
        boolean ok = true;
        int c = 0;
        Piece p;
        //check that the bitboards and pieceArray are synced
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                p = getPiece(x,y);
                if (p == edge){
                    continue;
                }

                if (p == null){
                    //check that this space is also empty in the bitboads
                    if ((getBitRep(x,y) & ourBitBoard) != 0){
                        ok = false;
                        System.out.println(colorStr(ourColor) +" bitBoard has a piece at " + locStr(x,y) + " but the pieceArray is empty there");
                    }
                    if ((getBitRep(x,y) & opponentBitBoard) != 0){
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
                //check that internal coordinates match board coordinates
                if (p.x != x || p.y != y){
                    ok = false;
                    System.out.println(colorStr(c) + " Piece at " + locStr(x, y) + " has internal coordinate of "+ locStr(p.x,p.y));
                }

            }
        }

        //check for shared bits (another way)
        if ((ourBitBoard & opponentBitBoard) != 0){
            System.out.println("bitboards share pieces");
            ok = false;
        }
        return ok;
    }

    public boolean verifyGoals(){
        boolean ok = true;
        //check that players are not in opponents goals
        if ((ourGoalMask & opponentBitBoard) != 0){
            System.out.println(colorStr(1-ourColor) + " has pieces in opponents goal");
            ok = false;
        }
        if ((opponentGoalMask & ourBitBoard) != 0){
            System.out.println(colorStr(ourColor) + " has pieces in opponents goal");
            ok = false;
        }
        return ok;
    }

    public boolean verifyPieceCount(){
        boolean ok = true;
        //check piece counts
        if (ourPieceCount > 10){
            System.out.println(colorStr(ourColor) + " has "+ ourPieceCount + " pieces");
            ok = false;
        }
        if (opponentPieceCount > 10){
            System.out.println(colorStr(1-ourColor) + " has "+ opponentPieceCount + " pieces");
            ok = false;
        }

        //check piece count methods
        PieceList wp = getPieces(white);
        PieceList bp = getPieces(black);
        if (getNumPieces(white) != wp.length()){
            System.out.println("getPieces(white).length() != getNumPieces(white)");
            ok = false;
        }
        if (getNumPieces(black) != bp.length()){
            System.out.println("getPieces(black).length() != getNumPieces(black)");
            ok = false;
        }

        //check piece lists
        if (ourPieceCount != ourPieces.length()){
            System.out.println("`ourPieceCount' and `ourPieces.length()' do not match");
            ok = false;
        }
        if (opponentPieceCount != opponentPieces.length()){
            System.out.println("`opponentPieceCount' and `opponentPieces.length()' do not match");
            ok = false;
        }

        return true;
    }

    public boolean verifyGoalCount(){
        boolean ok = true;
        Piece p;
        int ourA=0,ourB=0,oppA=0,oppB=0;
        for (int x = 0; x<8; x++){
            for (int y = 0; y<8; y++){
                p = getPiece(x, y);
                if (p != null && p != edge){
                    if ((p.bitRep & ourGoalMaskA) != 0){
                        ourA++;
                    }else if ((p.bitRep & ourGoalMaskB) != 0){
                        ourB++;
                    }else if ((p.bitRep & opponentGoalMaskA) != 0){
                        oppA++;
                    }else if ((p.bitRep & opponentGoalMaskB) != 0){
                        oppB++;
                    }
                }
            }
        }
        if (ourA != ourNumInGoalA){
            System.out.println("incorrect value for `ourNumInGoalA' expect: "+ ourA + " got: "+ ourNumInGoalA);
            ok = false;
        }
        if (ourB != ourNumInGoalB){
            System.out.println("incorrect value for `ourNumInGoalB' expect: "+ ourB + " got: "+ ourNumInGoalB);
            ok = false;
        }
        if (oppA != opponentNumInGoalA){
            System.out.println("incorrect value for `opponentNumInGoalA' expect: "+ oppA + " got: "+ opponentNumInGoalA);
            ok = false;
        }
        if (oppB != opponentNumInGoalB){
            System.out.println("incorrect value for `opponentNumInGoalB' expect: "+ oppB + " got: "+ opponentNumInGoalB);
            ok = false;
        }
        return ok;
    }

    public boolean verifyMatrix(){
        return verifyMatrix(white) && verifyMatrix(black);
    }

    public boolean verifyMatrix(int color){
        boolean ok = true;
        PieceList connections;
        for (Piece p : color == ourColor ? ourPieces : opponentPieces){
            connections = connectedPieces(p);
            Piece[] matrixConnections = {p.up,
                                         p.down,
                                         p.left,
                                         p.right,
                                         p.rightUp,
                                         p.rightDown,
                                         p.leftUp,
                                         p.leftDown};
            for (Piece connect : matrixConnections){
                if (connect != null
                    && connect.color == color
                    && !connections.containsPiece(connect)){
                    ok = false;
                    System.out.println(connect + " does not have proper matrix connections");
                }
            }
        }
        return ok;
    }

    //verify that all internal state is valid
    public boolean verify(){
        boolean ok = true;
        ok = verifyBitBoards() && ok;
        ok = verifyGoals() && ok;
        ok = verifyPieceCount() && ok;
        ok = verifyGoalCount() && ok;
        ok = verifyMatrix() && ok;
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

    //
    public Board (int color, String boardString){
        this(color);
        setupFromBoardString(boardString);
    }

    //clear current board and set it up from BOARDSTRING
    public void setupFromBoardString(String boardString){
        clearBoard();
        int whiteCount=0, blackCount=0;
        Move m;
        boardString = boardString.toLowerCase();
        if (boardString.length() != 64){
            System.out.println("Error --Board.Board(int, String)-- invalid board string:\n"+boardString);
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

    //returns a string that evaluates to a string used to reconstruct
    //this board. For use with the Board.Board(int, String) constructor
    // if trueString is non-false, return the true string representation
    // (no  '+', '"' or '\n' included)
    public String toBoardString(boolean trueString){
        String lines = "";
        String line = "";
        long bitRep = 0;
        long whiteBB = (ourColor == white ? ourBitBoard : opponentBitBoard);
        long blackBB = (ourColor == black ? ourBitBoard : opponentBitBoard);

        for (int y = 0; y < 8; y++){
            line = trueString? "" : "\"";
            for (int x = 0; x < 8; x++){
                bitRep = getBitRep(x, y);
                if ((bitRep & whiteBB) != 0){
                    line += "o";
                }else if ((bitRep & blackBB) != 0){
                    line += "x";
                }else{
                    line += " ";
                }
            }
            if (y == 7){
                lines = lines + line + (trueString ? "" : "\"");
                continue;
            }
            lines = lines + line + (trueString ? "" : "\" +\n");

        }
        return lines;
    }
    public String toBoardString(){
        return toBoardString(false);
    }

    public long clustersBB(int color){
        //this includes clusters that would be formed by placing
        //pieces in the opponents goals, which would otherwise
        //be illegal
        long clusterBoard = 0;
        Move m = null;
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                if (! pieceAt(x,y) && isValidIndex(x,y)){
                    m = new Move(x, y);
                    if (formsIllegalCluster(m, color)){
                        clusterBoard |= getBitRep(x, y);
                    }
                }
            }
        }
        return clusterBoard;
    }

    public long legalMovesBB(int color){
        long movesBoard = 0;
        for (Move m: validMoves(color)){
            movesBoard |= getBitRep(m.x1, m.y1);
        }
        return movesBoard;
    }

    public long[] connectedPiecesBB(int color){
        long[] clusters = {0,0,0,0,0,0,0,0,0};
        int n = 0;
        for (Piece p : getPieces(color)){
            clusters[connectedPieces(p).length()] |= p.bitRep;
        }
        return clusters;
    }

    public Cell[][] toCellArray(){
        Piece piece;
        Cell[][] cells = new Cell[8][8];

        Cell cell;
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){

                piece = getPiece(x, y);
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

    private void clearBoard(){
        ourBitBoard = opponentBitBoard = 0;
        ourPieceCount =  opponentPieceCount = 0;
        ourNumInGoalA = ourNumInGoalB = 0;
        opponentNumInGoalA = opponentNumInGoalB = 0;
        ourPieces.clear();
        opponentPieces.clear();
        pieceArray = new Piece[10][10];//the easy way
        pieces = new PieceList(20);
        rows = new Piece[8];
        columns = new Piece[8];
        fDiagonals = new Piece[14];
        bDiagonals = new Piece[14];
        for (int i = 0; i < 20; i++){
            pieces.add(new Piece());
        }

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
    private String colorStr(int color){
        switch (color){
        case 0: return "black";
        case 1: return "white";
        default: return "rainbow";
        }
    }
    public long getOurBitBoard(){
        return ourBitBoard;
    }
    public long getOpponentBitBoard(){
        return opponentBitBoard;
    }

    public void interactiveDebug(){
        interactiveDebug(null);
    }
    public void interactiveDebug(MachinePlayer player){
        loadGeneratedTests();
        BufferedReader keybd = new BufferedReader(new InputStreamReader(System.in));
        AList<Move> history = new AList<Move>(300);
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
        String lastInput = "print";
        Move m = null;

        //keeping track of messages this way is allows us to print the board before the
        //messages
        AList<String> messages = new AList<String>(100);

        PrintWriter writer = null;
        try{
            writer = new PrintWriter("_new-tests.java", "UTF-8");

        }catch (FileNotFoundException err){
            messages.add("Error saving to file: file not found");
        }catch (UnsupportedEncodingException err){
            messages.add("Error saving to file: unsupported encoding");
        }

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
                }else if (input.equals(".")){
                    input = lastInput;
                    System.out.println(">>>> " + input);
                }
            }
            lastInput = input;
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
                    history.add(m);
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

            case "adjacent": case "around": case "surround": //ok
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
                AListIterator<Move> iter;// = new AListIterator(new Integer[] {1,2,3,4,5}, 0);
                iter = validMoves.iterator();
                while (iter.hasNext())
                    System.out.print(iter.next() + ", ");
                break;

                //idea: use transposition table from last move to help order the moves
            case "verify": case "valid": case "v": //ok
                if (verify()){
                    messages.add("Everything verified ok.");
                }else{
                    messages.add("Board failed to validate.");
                }
                if (runGeneratedTests()){
                    messages.add("All tests pass");
                }else{
                    messages.add("Test(s) failed");
                }
                break;
            case "verify-": //verifications without running tests
                if (verify()){
                    messages.add("Everything verified ok.");
                }else{
                    messages.add("Board failed to validate.");
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
                messages.add(hasNetwork(color) ? "YES": "NO");
                break;
            case "moves": //ok
                AList<Move> moves = validMoves(color);
                messages.add("found " + moves.length() +" moves");
                System.out.print("moves: ");
                int nBad = 0;
                for (Move mm : moves){
                    if (! isValidMove(mm, color)){
                        nBad++;
                        pb.mark(mm.x1, mm.x2);
                    }
                }
                if (nBad > 0){
                    messages.add("found " + nBad + " disagreements(marked)");
                }else{
                    messages.add("all moves are ok");
                    pb.mark(moves);
                }

                // for (Move move : moves){
                //     System.out.print(locStr(move.x1, move.y1));
                // }

                break;

            case "pieceat": case "pa": //Piece At    //ok
                if (arg1isRef){
                    Piece p = getPiece(argX1, argY1);
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
                        if (p.up != null){
                            messages.add("  up = " +p.up);
                        }
                        if (p.down != null){
                            messages.add("  down = " +p.down);
                        }
                        if (p.left != null){
                            messages.add("  left = " +p.left);
                        }
                        if (p.right != null){
                            messages.add("  right = " +p.right);
                        }
                        if (p.rightUp != null){
                            messages.add("  rightUp = " +p.rightUp);
                        }
                        if (p.rightDown != null){
                            messages.add("  rightDown = " +p.rightDown);
                        }
                        if (p.leftUp != null){
                            messages.add("  leftUp = " +p.leftUp);
                        }
                        if (p.leftDown != null){
                            messages.add("  leftDown = " +p.leftDown);
                        }
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
                int num= 0;
                PieceList pLst = getPieces(black);
                num+=pLst.length();
                for (Piece p: pLst){
                    pb.mark(p);
                }
                pLst = pLst = getPieces(white);
                num+=pLst.length();
                for (Piece p: pLst){
                    pb.mark(p);
                }

                messages.add("found "+num+" pieces");
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
                for (int x = 0; x<8; x++){
                    for (int y = 0; y<8; y++){
                        p = getPiece(x, y);
                        if (p != null && p != edge && (p.bitRep & goalmask) != 0){
                            pb.mark(x,y);
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
                for (int x = 0; x<8; x++){
                    for (int y = 0; y<8; y++){
                        p2 = getPiece(x,y);
                        if (p2 != null && p2 != edge && (p2.bitRep & goalmask2)!=0){
                            pb.mark(x,y);
                            c4++;
                        }
                    }
                }
                messages.add("found " + c4 + " goal A pieces");
                break;

            case "ingoala":case "iga":
                long goal,board;
                if (color == ourColor){
                    goal = ourGoalMaskA;
                    board = ourBitBoard;
                }else{
                    goal = opponentGoalMaskA;
                    board = opponentBitBoard;
                }
                if ((goal & board) != 0){
                    messages.add("Piece(s)in goal A");
                    break;
                }
                messages.add("goal A is empty");
                break;

            case "ingoalb":case "igb":
                long goal2,board2;
                if (color == ourColor){
                    goal2 = ourGoalMaskB;
                    board2 = ourBitBoard;
                }else{
                    goal2 = opponentGoalMaskB;
                    board2 = opponentBitBoard;
                }
                if ((goal2 & board2) != 0){
                    messages.add("Piece(s)in goal B");
                    break;
                }
                messages.add("goal B is empty");
                break;

            case "boardstring":
                messages.add("boardString:");
                messages.add(toBoardString());
                Board testBoard = new Board(color, toBoardString(true));
                if ((testBoard.getOurBitBoard() == ourBitBoard)
                    && (testBoard.getOpponentBitBoard() == opponentBitBoard)){
                    messages.add("tests ok");
                }else{
                    messages.add("failure to reconstruct");
                }

                break;

            case "clustersbitboard" : case "clustersbb":
                messages.add(colorStr(color) + " cluster bitBoard:");
                messages.add(bitBoardToString(clustersBB(color)));
                break;

            case "movesbitboard" : case "movesbb":
                messages.add(colorStr(color) + " moves bitBoard:");
                messages.add(bitBoardToString(legalMovesBB(color)));
                break;

            case "connectedbb": case "cbb":
                int index = 0;
                try{
                    index = Integer.parseInt(arg1);
                }catch(NumberFormatException err){
                    messages.add("Invalid number: '"+ arg1 +"'");
                    break;
                }
                if (index <0 || index > 8){
                    messages.add("  board index must be between 0 and 8");
                    break;
                }
                messages.add("bitboard for pieces with "+ index+ " connections: ");
                messages.add(bitBoardToString(connectedPiecesBB(color)[index]));
                break;

            case "maketest": case "createtest": case "mt":
                BoardTest test = makeTest(writer);

                messages.add(test.toString());

                if (compareToTest(test)){
                    messages.add("(Test is OK)");
                }else{
                    messages.add("(Test fails)");
                }

                break;

            case "ntests":
                messages.add("there are " + BoardTest.tests.length() + " generated tests");
                break;

            case "runtests": case "rt":
                if (runGeneratedTests()){
                    messages.add("All tests pass.");
                    break;
                }
                messages.add("Tests failed. Board is broken. Time to cry");
                break;

            case "showtest": case "findtest": case "gettest": case "st":
                int id = 0;
                try{
                    id = Integer.parseInt(arg1);
                }catch(NumberFormatException err){
                    messages.add("Invalid number: '"+ arg1 +"'");
                    break;
                }
                if (id < 0) {
                    messages.add("Invalid test id. Must be > 0");
                    break;
                }

                BoardTest tst = getTest(id);
                if (tst == null){
                    messages.add("No test with id = "+ id);
                    break;
                }
                messages.add("test " +id +": ");
                messages.add("white bitBoard: " + tst.whiteBB);
                messages.add("black bitBoard: " + tst.blackBB);
                messages.add(bitBoardToString(tst.whiteBB | tst.blackBB));
                break;

            case "setuptest": //setup board as it is in a test
                try{
                    id = Integer.parseInt(arg1);
                }catch(NumberFormatException err){
                    messages.add("Invalid number: '"+ arg1 +"'");
                    break;
                }
                if (id < 0) {
                    messages.add("Invalid test id. Must be > 0");
                    break;
                }
                BoardTest tst2 = getTest(id);
                if (tst2 == null){
                    messages.add("No test with id = "+ id);
                    break;
                }
                setupFromBoardString(tst2.board);
                messages.add("board setup from test "+ id);
                input = "print";
                fakeInput = true;
                break;

            case "_remakealltests":
                //this should be avoided when possible,
                //if it is used, it might be a good idea to diff the tests
                //just be be sure that only the corrupted bits have changed
                String currentBoard = toBoardString(true);
                int testcount=0;
                int failedcount =0;
                BoardTest test2=null;
                for (BoardTest t : BoardTest.tests){
                    setupFromBoardString(t.board);
                    test2 = makeTest(writer);
                    if (!compareToTest(test2)){
                        messages.add("generated test failed: \n" + test2.boardString);
                        failedcount++;
                    }
                    testcount++;
                }
                messages.add("re-generated " + testcount + " tests");
                if (failedcount > 0){
                    messages.add(failedcount + " tests failed immediate testing");
                }
                setupFromBoardString(currentBoard);
                pb = toPrintBoard();
                break;

            case "piececount": case "numpieces":
                messages.add("piece count:");
                messages.add("white: "+(ourColor==white? ourPieceCount: opponentPieceCount));
                messages.add("black: "+(ourColor==black? ourPieceCount: opponentPieceCount));
                break;
            case "evaluate": case "eval":case "score": case "s":
                messages.add("(" +colorStr(ourColor)+ ") board score: " + score());
                break;
            case "choosemove": case "cm":
                if (player == null){
                    messages.add("Error: player is unknown");
                    break;
                }
                if (hasNetwork(ourColor)){
                    messages.add("we already won");
                    break;
                }
                Move move = player.chooseMove();
                pb.mark(move.x1, move.y1);
                messages.add(colorStr(ourColor) + " player choose move: " + move);
                break;

            case "clearboard": case "clear":
                clearBoard();
                history = new AList<Move>(100);
                messages.add("cleared the board");
                break;

            case "rungame": case "rg":
                clearBoard();
                MachinePlayer player1 = new MachinePlayer(white);
                MachinePlayer player2 = new MachinePlayer(black);
                Move m1, m2;
                while (true){
                    m1 =  player1.chooseMove();
                    history.add(m1);
                    player2.opponentMove(m1);
                    move(m1,white);

                    pb = toPrintBoard();
                    System.out.println(pb.toString());
                    System.out.println("white moved: " + m1);

                    if (player1.board.hasNetwork(white,pb)){
                        messages.add("white wins");
                        break;
                    }

                    m2 =  player2.chooseMove();
                    history.add(m2);
                    player1.opponentMove(m2);
                    move(m2, black);

                    pb = toPrintBoard();
                    System.out.println(pb.toString());
                    System.out.println("black moved: " + m2);

                    if (player1.board.hasNetwork(black, pb)){
                        messages.add("black wins");
                        break;
                    }

                    if (!player1.board.verify()){
                        System.out.println("player 1 has a corrupted board");
                    }
                    if (!player2.board.verify()){
                        System.out.println("player 2 has a corrupted board");
                    }
                }
                break;

            case "ourpieces":
                messages.add(colorStr(ourColor) + " has " + ourPieces.length() + " pieces:");
                for (Piece _p : ourPieces){
                    messages.add(""+ _p);
                }
                break;

            case "opponentpieces": case "opppieces": case "opieces":
                messages.add(colorStr(opponentColor) + " has " + opponentPieces.length() + " pieces:");
                for (Piece ___p : opponentPieces){
                    messages.add(""+ ___p);
                }
                break;

            case "matrixremove": case "mremove":
                if (arg1isRef){
                    if (!pieceAt(argX1, argY1)){
                        messages.add("no piece at " + locStr(argX1, argY1));
                        break;
                    }
                    p = getPiece(argX1, argY1);
                    removeFromMatrix(p);
                    messages.add(p + " was removed from the matrix");
                }else{
                    messages.add("Invalid arg: " + arg1);
                }
                break;

            case "matrixadd": case "madd":
                if (arg1isRef){
                    Piece newPiece = new Piece(color,
                                         getBitRep(argX1, argY1),
                                         argX1, argY1);
                    addToMatrix(newPiece);
                    messages.add(newPiece + " was created and added to the matrix");
                }else{
                    messages.add("Invalid arg: " + arg1);
                }
                break;
            case "vnotmatrix": //verify not in matrix
                if (arg1isRef){
                    if (!pieceAt(argX1, argY1)){
                        messages.add("no piece at " + locStr(argX1, argY1));
                        break;
                    }
                    p = getPiece(argX1, argY1);
                    if (verifyNotInMatrix(p)){
                        messages.add(p + " is NOT in the matrix");
                    }else{
                        messages.add(p + " is in the matrix");
                    }

                }else{
                    messages.add("Invalid arg: " + arg1);
                }
                break;
            case "print":
                break;
            case "exit": case "quit": case "done":
                loop = false;
                break;

            case "printmatrix":
                System.out.println("todo");
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
                for (String message : messages){
                    System.out.println(message);
                }
                messages.clear();
            }
        }
        System.out.println("done");
    }
    public BoardTest getTest (int id){
        for (BoardTest test : BoardTest.tests){
            if (test.getId() == id){
                return test;
            }
        }
        return null;
    }

    private void interactiveHelp(AList<String> messages){
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

    //make a test that tests the current state of the board
    //save it to OUTFILE and return it
    private BoardTest makeTest(PrintWriter outFile){
        BoardTest test = new BoardTest(1);
        //test.boardString is written to file, test.board is read from file
        //it is provided here to that this test can also be run
        //in this round with 'runtests'
        test.boardString = toBoardString();
        test.board = toBoardString(true);
        test.whiteBB = (ourColor == white? ourBitBoard : opponentBitBoard);
        test.blackBB = (ourColor == black? ourBitBoard : opponentBitBoard);
        test.whiteClustersBB = clustersBB(white);
        test.blackClustersBB = clustersBB(black);
        test.whiteLegalMovesBB = legalMovesBB(white);
        test.blackLegalMovesBB = legalMovesBB(black);
        test.whiteConnectedPieces = connectedPiecesBB(white);
        test.blackConnectedPieces = connectedPiecesBB(black);
        test.whiteNetwork = hasNetwork(white);
        test.blackNetwork = hasNetwork(black);
        test.whiteNumPieces = ourColor == white? ourPieceCount: opponentPieceCount;
        test.blackNumPieces = ourColor == black? ourPieceCount: opponentPieceCount;
        test.passedBitBoardTests = verifyBitBoards();
        test.passedGoalTests = verifyGoals();
        test.passedPieceCountTests = verifyPieceCount();

        outFile.println(test.toString());
        outFile.flush();
        return test;
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

    //compare 'this' boards fields to those in test,
    //return true if they match, otherwise false.
    public boolean compareToTest(BoardTest test){
        boolean ok = true;
        AList<String> errors  = new AList<String>(10);

        if (test.whiteBB != (ourColor == white ? ourBitBoard : opponentBitBoard)){
            errors.add("white bitBoard does not match");
        }
        if (test.blackBB != (ourColor == black ? ourBitBoard : opponentBitBoard)){
            errors.add("black bitBoard does not match");
        }
        if (test.whiteClustersBB != clustersBB(white)){
            errors.add("white clusters does not match");
        }
        if (test.blackClustersBB != clustersBB(black)){
            errors.add("black clusters does not match");
        }
        if (test.whiteLegalMovesBB != legalMovesBB(white)){
            errors.add("white legal moves does not match");
        }
        if (test.blackLegalMovesBB != legalMovesBB(black)){
            errors.add("black legal moves does not match");
        }
        long[] wcp = connectedPiecesBB(white);
        long[] bcp = connectedPiecesBB(black);

        for (int i = 0; i < 8; i++){
            if (test.whiteConnectedPieces[i] != wcp[i]){
                errors.add("white Connected pieces don't match (level " + i +")");
            }
            if (test.blackConnectedPieces[i] != bcp[i]){
                errors.add("black Connected pieces don't match (level " + i +")");
            }
        }
        if (test.whiteNetwork != hasNetwork(white)){
            errors.add("hasNetwork(white) does not match");
        }
        if (test.blackNetwork != hasNetwork(black)){
            errors.add("hasNetwork(black) does not match");
        }
        if (test.whiteNumPieces
            != (ourColor == white ? ourPieceCount: opponentPieceCount)){
            errors.add("white piece count does not match");
        }
        if (test.blackNumPieces
            != (ourColor == black ? ourPieceCount: opponentPieceCount)){
            errors.add("blackpiece count does not match");
        }
        if (test.passedBitBoardTests != verifyBitBoards()){
            errors.add("verfiyBitBoards test result does not match");
        }
        if (test.passedGoalTests != verifyGoals()){
            errors.add("verifyGoals test result does not match");
        }
        if (test.passedPieceCountTests != verifyPieceCount()){
            errors.add("verifyPieceCount test result does not match");
        }

        int nErrors = errors.length();
        if (nErrors != 0){
            System.out.println(nErrors + " errors with test #" + test.getId());
            for (String s: errors){
                System.out.println("   "+ s);
            }
            return false;
        }
        return true;
    }
    // run a generated test, this does not modify 'this' board
    private boolean runGeneratedTest (BoardTest test){
        if (test.version != 1){
            System.out.println("Warning: unknown test version");
        }
        Board testBoard = new Board(white, test.board);
        return testBoard.compareToTest(test);
    }

    //run all generated tests, this does not modify 'this' board
    private boolean runGeneratedTests(){
        boolean ok = true;
        for (BoardTest test : BoardTest.tests){
            if (! runGeneratedTest(test)){
                ok = false;
            }
        }
        return ok;
    }

    private void loadGeneratedTests(){
        BoardTest test = null;
        /// all code that follows are tests generated with the
        /// 'makeTest' interactive command

        test = new BoardTest(1);
        test.board =
            "        " +
            "        " +
            "        " +
            "        " +
            "        " +
            "        " +
            "        " +
            "        ";
        test.whiteBB = 0L;
        test.blackBB = 0L;
        test.whiteClustersBB = 0L;
        test.blackClustersBB = 0L;
        test.whiteLegalMovesBB = 72057594037927680L;
        test.blackLegalMovesBB = 9114861777597660798L;
        test.whiteConnectedPieces = new long[] {0L,0L,0L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,0L,0L,0L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 0;
        test.blackNumPieces = 0;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "        " +
            "        " +
            "        " +
            "   o    " +
            "        " +
            "        " +
            "        " +
            "        ";
        test.whiteBB = 134217728L;
        test.blackBB = 0L;
        test.whiteClustersBB = 0L;
        test.blackClustersBB = 0L;
        test.whiteLegalMovesBB = 72057593903709952L;
        test.blackLegalMovesBB = 9114861777463443070L;
        test.whiteConnectedPieces = new long[] {134217728L,0L,0L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,0L,0L,0L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 1;
        test.blackNumPieces = 0;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "        " +
            "        " +
            "        " +
            "   oo   " +
            "        " +
            "        " +
            "        " +
            "        ";
        test.whiteBB = 402653184L;
        test.blackBB = 0L;
        test.whiteClustersBB = 258305949696L;
        test.blackClustersBB = 0L;
        test.whiteLegalMovesBB = 72057335329324800L;
        test.blackLegalMovesBB = 9114861777195007614L;
        test.whiteConnectedPieces = new long[] {0L,402653184L,0L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,0L,0L,0L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 2;
        test.blackNumPieces = 0;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "        " +
            "        " +
            "        " +
            "  xoo   " +
            "        " +
            "        " +
            "        " +
            "        ";
        test.whiteBB = 402653184L;
        test.blackBB = 67108864L;
        test.whiteClustersBB = 258238840832L;
        test.blackClustersBB = 0L;
        test.whiteLegalMovesBB = 72057335329324800L;
        test.blackLegalMovesBB = 9114861777127898750L;
        test.whiteConnectedPieces = new long[] {0L,402653184L,0L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {67108864L,0L,0L,0L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 2;
        test.blackNumPieces = 1;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "        " +
            "        " +
            "        " +
            "  xoo   " +
            "  x     " +
            "        " +
            "        " +
            "        ";
        test.whiteBB = 402653184L;
        test.blackBB = 17246978048L;
        test.whiteClustersBB = 241058971648L;
        test.blackClustersBB = 15436146933760L;
        test.whiteLegalMovesBB = 72057335329324800L;
        test.blackLegalMovesBB = 9114846323801095806L;
        test.whiteConnectedPieces = new long[] {0L,402653184L,0L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,17246978048L,0L,0L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 2;
        test.blackNumPieces = 2;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "        " +
            "  x   x " +
            "        " +
            "  xoo   " +
            "  x     " +
            "        " +
            "        " +
            "     x  ";
        test.whiteBB = 402653184L;
        test.blackBB = 2305843026460689408L;
        test.whiteClustersBB = 241058971648L;
        test.blackClustersBB = 15436146933760L;
        test.whiteLegalMovesBB = 72057335329307392L;
        test.blackLegalMovesBB = 6809003314587384446L;
        test.whiteConnectedPieces = new long[] {0L,402653184L,0L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,2305843009213710336L,17246979072L,0L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 2;
        test.blackNumPieces = 5;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "    x   " +
            "  x   x " +
            "        " +
            "  xoo   " +
            "  x     " +
            "        " +
            "        " +
            "     x  ";
        test.whiteBB = 402653184L;
        test.blackBB = 2305843026460689424L;
        test.whiteClustersBB = 241058971648L;
        test.blackClustersBB = 15436146944040L;
        test.whiteLegalMovesBB = 72057335329307392L;
        test.blackLegalMovesBB = 6809003314587374150L;
        test.whiteConnectedPieces = new long[] {0L,402653184L,0L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {16L,2305843009213710336L,17246979072L,0L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 2;
        test.blackNumPieces = 6;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "      x " +
            "  x   x " +
            "   x   o" +
            "  x o o " +
            "  x     " +
            "o  o o  " +
            "      o " +
            "     x  ";
        test.whiteBB = 18059479836786688L;
        test.blackBB = 2305843026461213760L;
        test.whiteClustersBB = 4661455380589608960L;
        test.blackClustersBB = 6640194992686L;
        test.whiteLegalMovesBB = 4228734791400192L;
        test.blackLegalMovesBB = 6790953730624716816L;
        test.whiteConnectedPieces = new long[] {0L,1099788451840L,18058378974593024L,1073741824L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {2305843009213693952L,0L,17180409920L,67109888L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 7;
        test.blackNumPieces = 7;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "      x " +
            "  x   x " +
            "   x   o" +
            "  x o o " +
            "  x     " +
            "o o  o  " +
            " o    o " +
            "     x  ";
        test.whiteBB = 18618031743696896L;
        test.blackBB = 2305843026461213760L;
        test.whiteClustersBB = 5097471127515594752L;
        test.blackClustersBB = 11038241503790L;
        test.whiteLegalMovesBB = 186071808L;
        test.blackLegalMovesBB = 6790390780671295504L;
        test.whiteConnectedPieces = new long[] {0L,8388608L,36284152152064L,18577349536645120L,4398046511104L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,2305843009213693952L,540736L,17246979072L,0L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = true;
        test.whiteNumPieces = 8;
        test.blackNumPieces = 7;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "     x  " +
            "        " +
            "   x x  " +
            "o x o oo" +
            " o o    " +
            "  x     " +
            "        " +
            "  x x   ";
        test.whiteBB = 46456111104L;
        test.blackBB = 1441156278874800160L;
        test.whiteClustersBB = 30739799408640L;
        test.blackClustersBB = 580401419326487552L;
        test.whiteLegalMovesBB = 72022409666166528L;
        test.blackLegalMovesBB = 7093304035104522846L;
        test.whiteConnectedPieces = new long[] {0L,2164260864L,44291850240L,0L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,0L,1441151880827764768L,4398047035392L,0L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = true;
        test.whiteNumPieces = 6;
        test.blackNumPieces = 7;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "     x  " +
            "        " +
            "   x x  " +
            "o x o o " +
            " o o   o" +
            "  x     " +
            "        " +
            "  x x   ";
        test.whiteBB = 594064441344L;
        test.blackBB = 1441156278874800160L;
        test.whiteClustersBB = 241298423611392L;
        test.blackClustersBB = 580401419326487552L;
        test.whiteLegalMovesBB = 71811303433633536L;
        test.blackLegalMovesBB = 7093304035104522846L;
        test.whiteConnectedPieces = new long[] {0L,16777216L,559687925760L,34359738368L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,0L,1441151880827764768L,4398047035392L,0L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = true;
        test.whiteNumPieces = 6;
        test.blackNumPieces = 7;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "      x " +
            "  x   x " +
            "   x   o" +
            "  xoo o " +
            "  x     " +
            "o  o    " +
            "      o " +
            "     x  ";
        test.whiteBB = 18024295598915584L;
        test.blackBB = 2305843026461213760L;
        test.whiteClustersBB = 1067843878912L;
        test.blackClustersBB = 6640060774958L;
        test.whiteLegalMovesBB = 54032213347613440L;
        test.blackLegalMovesBB = 6790988914996805648L;
        test.whiteConnectedPieces = new long[] {0L,1099520016384L,18023194870939648L,1207959552L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {2305843009213693952L,17179869248L,540672L,67109888L,0L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = false;
        test.whiteNumPieces = 7;
        test.blackNumPieces = 7;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "      x " +
            "  x   x " +
            "   x   o" +
            "  xoo o " +
            "  x     " +
            "o  o    " +
            "      o " +
            "     x  ";
        test.whiteBB = 18024295598915584L;
        test.blackBB = 2305843026461213760L;
        test.whiteClustersBB = 1067843878912L;
        test.blackClustersBB = 6640060774958L;
        test.whiteLegalMovesBB = 54032213347613440L;
        test.blackLegalMovesBB = 6790988914996805648L;
        test.whiteConnectedPieces = new long[] {0L,1099520016384L,18023194870939648L,1207959552L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {2305843009213693952L,17179869248L,540672L,67109888L,0L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = false;
        test.whiteNumPieces = 7;
        test.blackNumPieces = 7;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "      x " +
            "  x o x " +
            " o x   o" +
            "o x o o " +
            "  x     " +
            " o   o  " +
            " o    o " +
            "     x  ";
        test.whiteBB = 18614733225725952L;
        test.blackBB = 2305843026461213760L;
        test.whiteClustersBB = 5095213795818963712L;
        test.blackClustersBB = 13237264624174L;
        test.whiteLegalMovesBB = 53442418360822528L;
        test.blackLegalMovesBB = 6790390780671295504L;
        test.whiteConnectedPieces = new long[] {0L,25165824L,35184372224000L,18579547754594304L,1073741824L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,2305843009213710336L,525376L,17246978048L,0L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = false;
        test.whiteNumPieces = 10;
        test.blackNumPieces = 7;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "  x     " +
            "   xo x " +
            " o   x o" +
            "o  xo o " +
            "  x     " +
            " o   o  " +
            " ox  xo " +
            "   x    ";
        test.whiteBB = 18614733225725952L;
        test.blackBB = 586593868781209604L;
        test.whiteClustersBB = 7390923705936611072L;
        test.blackClustersBB = 1592035774727759482L;
        test.whiteLegalMovesBB = 43309319132653312L;
        test.blackLegalMovesBB = 6917617400888164352L;
        test.whiteConnectedPieces = new long[] {0L,25169920L,18612532835123200L,2200365432832L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,9007199256854528L,576460752303425540L,1125917220929536L,0L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = true;
        test.whiteNumPieces = 10;
        test.blackNumPieces = 9;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "  x     " +
            "   xo x " +
            " o   x o" +
            "o  xo o " +
            "  x     " +
            " o   o  " +
            " ox  xo " +
            "   x    ";
        test.whiteBB = 18614733225725952L;
        test.blackBB = 586593868781209604L;
        test.whiteClustersBB = 7390923705936611072L;
        test.blackClustersBB = 1592035774727759482L;
        test.whiteLegalMovesBB = 43309319132653312L;
        test.blackLegalMovesBB = 6917617400888164352L;
        test.whiteConnectedPieces = new long[] {0L,25169920L,18612532835123200L,2200365432832L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,9007199256854528L,576460752303425540L,1125917220929536L,0L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = true;
        test.whiteNumPieces = 10;
        test.blackNumPieces = 9;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "  x     " +
            " x xo x " +
            " o   x o" +
            "o  xo o " +
            "  x     " +
            " o   o  " +
            " ox  xo " +
            "   x    ";
        test.whiteBB = 18614733225725952L;
        test.blackBB = 586593868781210116L;
        test.whiteClustersBB = 7390923705936610560L;
        test.blackClustersBB = 1592035774727824762L;
        test.whiteLegalMovesBB = 43309319132652800L;
        test.blackLegalMovesBB = 8509639981474521210L;
        test.whiteConnectedPieces = new long[] {0L,25169920L,18612532835123200L,2200365432832L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,9007199256854528L,576460752303423488L,1125917086714372L,134217728L,0L,0L,0L,0L};
        test.whiteNetwork = true;
        test.blackNetwork = true;
        test.whiteNumPieces = 10;
        test.blackNumPieces = 10;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

        test = new BoardTest(1);
        test.board =
            "  x     " +
            " x xo x " +
            " o   x o" +
            "o  xo o " +
            "  x     " +
            " ox  o  " +
            " ox   o " +
            "   x    ";
        test.whiteBB = 18614733225725952L;
        test.blackBB = 577591067572980228L;
        test.whiteClustersBB = 7399926507144840448L;
        test.blackClustersBB = 1592031376681313658L;
        test.whiteLegalMovesBB = 52312120340882688L;
        test.blackLegalMovesBB = 8516395337965903994L;
        test.whiteConnectedPieces = new long[] {0L,598134350680064L,18016597801304064L,1073741824L,0L,0L,0L,0L,0L};
        test.blackConnectedPieces = new long[] {0L,2113536L,577591050256777216L,17179871748L,134217728L,0L,0L,0L,0L};
        test.whiteNetwork = false;
        test.blackNetwork = false;
        test.whiteNumPieces = 10;
        test.blackNumPieces = 10;
        test.passedBitBoardTests = true;
        test.passedGoalTests = true;
        test.passedPieceCountTests = true;

    }

    public static void main(String[] args){
        Board b = new Board(black,
                            "        " +
                            "  o     " +
                            "        " +
                            "o ox  x " +
                            "  ox    " +
                            "o    x  " +
                            "    ox  " +
                            "        "
                            );

        PrintBoard pb = b.toPrintBoard();

        //left/right and upper/lower goals masks are switched
        // System.out.println(b.bitBoardToString(b.leftGoalMask));
        // System.out.println(b.bitBoardToString(b.ourBitBoard & b.leftGoalMask));
        //b.test();
        b.interactiveDebug();
        //
    }
}
