// player/Board.java

package player;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Deque;
import java.util.ArrayDeque;

import java.io.*;

public class Board{
    Piece[][] pieceArray;
    int ourColor;
    int opponentColor;
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
        ourColor = color; //0 for black, 1 for white
        opponentColor = 1-color;
        //TODO: assign goal masks
        edge = new Piece(0,0,0,0);
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
    }

    //returns the binary representation of the piece at (X, Y)
    private long getBitRep(int x,int y){
        //NOTE: (x,y) is the ACTUAL position of the piece on the board. Not the 1+ thing
        assert x >= 0 && y >= 0 && (x*8 + y) < 64 : "invalid index (Board.getBitRep)";
        return bitReps[x*8 + y];
    }

    //This assumes that MOVE is valid

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
            }else{
                opponentBitBoard |= bitRep;
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

            if (color == ourColor){
                //remove old location
                ourBitBoard ^= pieceArray[fromX][fromY].bitRep;
                //add new location
                ourBitBoard |= bitRep;

            }else{
                opponentBitBoard ^= pieceArray[fromX][fromY].bitRep;
                opponentBitBoard |= bitRep;
            }
            pieceArray[toX][toY] = pieceArray[fromX][fromY];
            pieceArray[toX][toY].bitRep = getBitRep(toX-1, toY-1);
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
        switch (move.moveKind){
        case Move.ADD :
            int x = move.x1 + 1,
                y = move.y1 + 1;
            //TODO: asserts to check index validity
            assert pieceArray[x][y] != null : "square should not be empty";
            ourBitBoard ^= pieceArray[x][y].bitRep;
            pieceArray[x][y] = null;
            break;
        case Move.STEP :
            int toX = move.x2 + 1,
                toY = move.y2 + 1,
                fromX = move.x1 + 1,
                fromY = move.y1 + 1;
            long toBitRep = getBitRep(toX-1, toY-1);

            assert pieceArray[toX][toY] == null : "square is already full";
            assert pieceArray[fromX][fromY] != null : "square is empty";

            ourBitBoard ^= pieceArray[fromX][fromY].bitRep;
            ourBitBoard &= toBitRep;

            pieceArray[toX][toY] = pieceArray[fromX][fromY];
            pieceArray[toX][toY].bitRep = toBitRep;
            pieceArray[fromX][fromY] = null;
            break;
        case Move.QUIT :
            //TODO
            break;
        }
    }

    private int countPieces (Piece[] pieces){
        int c = 0;
        for (Piece p : pieces){
            if ((p != edge) && (p != null)) {
                c+=1;
            }
        }
        return c;
    }

    //this should be fixed to prevent the double traversal
    private Piece[] removeNonPieces(Piece[] pieces){
        Piece [] ret = new Piece[countPieces(pieces)];
        int i=0;
        for (Piece p : pieces){
            if ((p != edge) && (p != null)){
                ret[i++] = p;
            }
        }
        return ret;
    }

    /** Board.adjacentPieces(int,int) returns an array of pieces
     * that are adjacent to the pieces at coordinates (X,Y) on
     * the board.
     *
     * The x,y coordinates are indexed from the top left with x
     * increasing to the right and y increasing down.
     *
     *  If there is no piece at (x,y) but (x,y) is still a valid
     *  board location, then this method works as expected.
     *
     * Unusual conditions:
     *  if (x,y) is not a valid location on the, then this
     *  method will likely cause the program to crash.
     *  The behavior in this case is undefined (but the corner
     *  pieces are ok).
     *
     *  @param x the x-coordinate location of the square
     *  @param y the y-coordinate location of the square
     *
     *  @returns an array of pieces adjacent to location (x,y) on the board
     *
     */
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

        return removeNonPieces(pieces);
    }

    /** Board.adjacentPieces(Piece) returns an array of pieces
     * that are adjacent to PIECE on the board
     *
     * Unusual conditions:
     *  The behavior of this method is undefined if piece is not
     *  actually on the board.
     *
     *  @param piece a piece on the board whose adjacent pieces will be returned
     *
     *  @returns an array of pieces adjacent to PIECE on the board
     */
    public Piece[] adjacentPieces(Piece piece){
        return adjacentPieces(piece.x, piece.y);
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
        //TODO: bounds checking
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
    public Piece[] connectedPieces(Piece piece){
        return connectedPieces(piece.x, piece.y);
    }

    //returns a piece from goalA
    private Piece getGoalPiece(){
        //TODO:

        //?? what is the orientation of the board during play
        //are we or a color always centered up-down or can it vary?
        return new Piece(0,0,0,0);
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
     *
     **/
    public boolean hasNetwork(){
        if (((ourBitBoard & ourGoalMaskA) != 0)
            && ((ourBitBoard & ourGoalMaskB) != 0)){
            return hasNetwork(getGoalPiece(), ourColor, ourGoalMaskA, 11, 60); //11x+60: just an impossible line
        }
        return false; //does not have at least one piece in each goal
    }

    public boolean hasNetwork(int color){
        long bitBoard = (color == ourColor ? ourBitBoard : opponentBitBoard);
        long goalA = (color == ourColor ? ourGoalMaskA : opponentGoalMaskA);
        long goalB = (color == ourColor ? ourGoalMaskB : opponentGoalMaskB);

        if (((bitBoard & goalA) != 0) && ((bitBoard & goalB) != 0)){
            return hasNetwork(getGoalPiece(), color, ourGoalMaskA, 11, 60); //11x+60: just an impossible line
        }
        return false; //does not have at lease one piece in each goal
    }

    //==========================================================================
    // Verification and testing code ===========================================
    //==========================================================================

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
                    continue;
                case 'o' :
                    m = new Move(x,y);
                    if (ourColor == white){
                        move(m);
                    }else{
                        opponentMove(m);
                    }
                    continue;
                default:
                    System.out.println("Error - Board.Board(int, String)- invalid char");
                }
            }
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
                    cell.defaultChar = "-";
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
        for (int y = 0; y<8; y++){
            for (int x = 0; x <8; x++){
                str += ((getBitRep(x,y) & bitBoard) != 0) ? "X" : "_";
            }
            str += "\n";
        }
        return str;
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
        if (i < 0 || i > 76 || x > 7 || y > 7
            || (x == 0 && y == 0)
            || (x == 7 && y == 0)
            || (x == 0 && y == 7)
            || (x == 7 && y == 7)){
            if (printMessages){
                System.out.println("Invalid Index: ("+ x +"," +y+")");
            }
            return false;
        }
        return true;
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
        //TODO:
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
        while (loop){
            pb = toPrintBoard();

            System.out.print(">>> ");

            if (!fakeInput){
                try{
                    input = keybd.readLine();
                }catch (IOException err){
                    System.out.println("Error reading input");
                }
                if (input.equals("")){
                    System.out.println(pb.toString());
                    continue;
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
            case "us":
                //highlight our pieces
                break;
            case "them":
                // highlight their pieces
                break;
            case "shownums":
                //toggle box numberings;
                pb.showNumbers();
                break;

            case "add": case "a": //ok
                //g&t 9.24
                if (arg1isRef){
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
                    if (color == ourColor){
                        move(m);
                        history.push(m);
                    }else {
                        opponentMove(m);
                    }
                    pb = toPrintBoard(); //update board
                    messages.add("Made move: " + m.toString());
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
                    pb.mark(connectedPieces(argX1, argY1));
                }
                break;

            case "adjacent": case "around": case "surround": case "s":
                if (arg1isRef){
                    pb.mark(adjacentPieces(argX1, argY1));
                }
                break;

                //idea: use transposition table from last move to help order the moves
            case "valid": case "v": //valid moves
                messages.add("Not Implemented");
                break;
            case "invalid": case "illegal": case "i": //invalid moves
                messages.add("Not Implemented");
                break;
            case "network": case "net": case "n":
                // visually show the detected network
                messages.add("Not Implemented");
                break;
            case "network?": case "net?": case "n?":
                System.out.println((hasNetwork(color) ? "YES": "NO"));
                break;
            case "exit": case "quit": case "done":
                loop = false;
                break;
            default:
                messages.add("Invalid Command");
            }

            if (!inhibitBoardPrint){
                System.out.println("\n\n\n\n\n");
                System.out.println(pb.toString());

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
            "\n\nAvailable commands -----------------------",
            "'add' <num>  ",
            "'move' <from> <to> ",
            "'undo'",
            "'black'     run commands as the black player",
            "'white'     run commands as the white player"};

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
        Board b = new Board(white,
                            "    x   " +
                            "      o " +
                            " xx  o  " +
                            "        " +
                            " o   x  " +
                            " x      " +
                            " x      " +
                            "        ");

        PrintBoard pb = b.toPrintBoard();

        //System.out.println(pb.toString());
        b.interactiveDebug();
    }
}

