/* MachinePlayer.java */
// javac -g -cp ../ ../player/*.java ../list/*.java

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
    Board board;
    int searchDepth;
    int ourColor, opponentColor;
    public static final int white = 1;
    public static final int black = 0;
    private static final int VAR_DEPTH = -1;
    private static final int ADD_DEPTH = 5;
    private static final int STEP_DEPTH = 4;

    MoveList[] movesLists;

    // Creates a machine player with the given color.  Color is either 0 (black)
    // or 1 (white).  (White has the first move.)
    public MachinePlayer(int color) {
        this(color, VAR_DEPTH);
    }

    // Creates a machine player with the given color and search depth.  Color is
    // either 0 (black) or 1 (white).  (White has the first move.)
    public MachinePlayer(int color, int searchDepth) {
        if (color != white && color != black){
            System.out.println("ERROR: invalid color: " + color);
        }
        ourColor = color;
        opponentColor = 1 - color;
        board = new Board(color);
        this.searchDepth = searchDepth;

        int depth = (searchDepth == VAR_DEPTH ? ADD_DEPTH : searchDepth);
        movesLists = new MoveList[depth+1];
        for (int i = 0; i <= depth; i++){
            movesLists[i] = new MoveList();
        }
    }

    // Returns a new move by "this" player.  Internally records the move (updates
    // the internal game board) as a move by "this" player.
    public Move chooseMove() {
        int depth = searchDepth;
        if (depth == VAR_DEPTH){
            if (board.getNumPieces(ourColor) < 10-STEP_DEPTH){
                depth = ADD_DEPTH;
            }else{
                depth = STEP_DEPTH;
            }
        }

        Best bestMove = minimax(ourColor, -100000, 100000, depth); //TODO: alpha, beta values ok?
        //make the move here instead of calling this.forceMove if we know that the move is valid
        board.move(bestMove.move, ourColor);
        return bestMove.move;
    }


    /**
     * Minimax algorithm with alpha-beta pruning which returns a Best object with a score and Move
     **/

    public Best minimax(int side, int alpha, int beta, int depth){
        Best myBest = new Best();
        Best reply;

        //TODO: avoid calculating all the moves before checking for networks
        //      (need this now so that we have we will always have a valid move)
        MoveList allValidMoves = movesLists[depth];
        board.validMoves(side, allValidMoves);

        myBest.move = allValidMoves.get(0);

        if (board.hasNetwork(opponentColor)){
            myBest.score = -10000 - 100*depth;
            return myBest;
        }

        if (board.hasNetwork(ourColor)){
            myBest.score = 10000+100*depth; //temp values for testing
            return myBest;
        }

        if (depth == 0){
            myBest.score = board.score();
            return myBest;
        }
        if (side == ourColor){
            myBest.score = alpha;
        }else{
            myBest.score = beta;
        }


        int score=0;
        for (Move m : allValidMoves){
            board.move(m, side);
            reply = minimax(1 - side, alpha, beta, depth - 1);
            score = reply.score;
            board.unMove(m);
            if ((side == ourColor) && (score > myBest.score)){
                myBest.move = m;
                myBest.score = score;
                alpha = score;
            } else if ((side == opponentColor) && (score < myBest.score)){
                myBest.move = m;
                myBest.score = score;
                beta = score;
            }
            if (alpha >= beta){
                return myBest;
            }
        }
        return myBest;
    }


    // If the Move m is legal, records the move as a move by the opponent
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method allows your opponents to inform you of their moves.
    public boolean opponentMove(Move m){
        if (board.isValidMove(m, opponentColor)){
            board.opponentMove(m);
            return true;
        }
        return false;
    }

    // If the Move m is legal, records the move as a move by "this" player
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method is used to help set up "Network problems" for your
    // player to solve.
    public boolean forceMove(Move m){
        if (board.isValidMove(m, ourColor)){
            board.move(m);
            return true;
        }
        return false;
    }

    public static void runGame(){

        MachinePlayer p1 = new MachinePlayer(white);
        MachinePlayer p2 = new MachinePlayer(black);
        Move m1, m2;
        int winner=0;
        while (true){
            m1 =  p1.chooseMove();
            System.out.println("player 1 moved: " + m1);
            p2.opponentMove(m1);

            if (p1.board.hasNetwork(white)){
                System.out.println("player 1(white) wins");
                break;
            }

            m2 =  p2.chooseMove();
            System.out.println("player 2 moved: " + m2);
            p1.opponentMove(m2);

            if (p1.board.hasNetwork(black)){
                System.out.println("player 2(black) wins");
                break;
            }

            if (!p1.board.verify()){
                System.out.println("player 1 has a corrupted board");
            }
            if (!p2.board.verify()){
                System.out.println("player 2 has a corrupted board");
            }
        }
    }
    /** use ForceMove to setup the board described by BOARDSTRING
     */
    public void forceBoard(String boardString){
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
                case 'x': case 'b':
                    m = new Move(x, y);
                    if (ourColor == black){
                        forceMove(m);
                    }else{
                        opponentMove(m);
                    }
                    blackCount++;
                    continue;
                case 'o': case 'w':
                    m = new Move(x,y);
                    if (ourColor == white){
                        forceMove(m);
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

    public void interactiveDebug(){
        board.interactiveDebug(this);
    }

    public static void benchMark(){
        int emptyDepth = 6;
        int fullDepth = 4;
        double start=0,end=0;
        for (int depth = 2; depth < 7; depth++){
            MachinePlayer p = new MachinePlayer(white, depth);

            p.forceBoard("        " +
                         "        " +
                         "        " +
                         "        " +
                         "        " +
                         "        " +
                         "        " +
                         "        "
                         );

            start = System.currentTimeMillis();
            p.chooseMove();
            end = System.currentTimeMillis();

            System.out.println("from empty [depth: " + depth + "]: "+ (end - start)/1000.0 + "s ");
        }

        for (int depth = 2; depth < 7; depth++){

            MachinePlayer p2 = new MachinePlayer(white, depth);
            p2.forceBoard("     x  " +
                          " oxxo   " +
                          "    o o " +
                          "  ox  x " +
                          "  oxoo  " +
                          "     x  " +
                          " x  oxo " +
                          " x      "
                          );

            start = System.currentTimeMillis();
            p2.chooseMove();
            end = System.currentTimeMillis();


            System.out.println("from full [depth: " + depth + "]: "+ (end - start)/1000.0 + "s ");
        }

    }

    public static void main(String[] args){

        MachinePlayer p = new MachinePlayer(white);
        p.forceBoard("        " +
                     "        " +
                     "        " +
                     "o ox  x " +
                     "  ox    " +
                     "o    x  " +
                     "    ox  " +
                     "        "
                     );
        //runGame();
        //p.interactiveDebug();
        benchMark();
    }
}
