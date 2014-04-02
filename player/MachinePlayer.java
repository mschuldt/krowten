/* MachinePlayer.java */
// javac -g -cp ../ ../player/*.java ../list/*.java

package player;
import dict.*;

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
    int count=0;
    int generation;

    MoveList[] movesLists;
    //at depth 4 hash table can have over 280393 items
    HashTableChained ht = new HashTableChained(30000);

    // Creates a machine player with the given color.  Color is either 0 (black)
    // or 1 (white).  (White has the first move.)
    public MachinePlayer(int color) {
        this(color, 4);//TODO: determine suitable default
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

        movesLists = new MoveList[searchDepth+1];
        for (int i = 0; i <= searchDepth; i++){
            movesLists[i] = new MoveList();
        }
        generation = 0;
    }

    // Returns a new move by "this" player.  Internally records the move (updates
    // the internal game board) as a move by "this" player.
    public Move chooseMove() {
        //ht.makeEmpty();
        generation++;
        System.out.println("hash table has " + count + " items");

        count=0;
        Best bestMove = minimax(ourColor, -100000, 100000, searchDepth); //TODO: alpha, beta values ok?
        //make the move here instead of calling this.forceMove if we know that the move is valid
        board.move(bestMove.move, ourColor); //TODO: does minimax always return a valid move?
        return bestMove.move;
    }


    /**
     * Minimax algorithm with alpha-beta pruning which returns a Best object with a score and Move
     **/

    public Best minimax(int side, int alpha, int beta, int depth){
        Best myBest = new Best();
        Best reply;

        //check if opponent has a network first to prevent
        //us from unblocking an opponent network to create our own
        //TODO: how to prevent the opponent from making this mistake?
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

        //AList<Move> allValidMoves = board.validMoves(side);
        MoveList allValidMoves = movesLists[depth];
        board.validMoves2(side, allValidMoves);

        //is it possible to not have any valid moves?
        myBest.move = allValidMoves.get(0);

        long hashCode=0;
        Entry entry;
        int score=0;
        long ourBoard, oppBoard;
        for (Move m : allValidMoves){

            board.move(m,side);

            ///***  memoization code

            if (depth < 2){
                hashCode = board.hash();
                ourBoard = board.getOurBitBoard();
                oppBoard = board.getOpponentBitBoard();
                entry = ht.find(hashCode, ourBoard, oppBoard, generation);
                if (entry != null){
                    score = entry.score;
                    //System.out.println("found!");
                }else{
                    reply = minimax(1 - side, alpha, beta, depth - 1);
                    score = reply.score;
                    ht.insert(hashCode, score, ourBoard, oppBoard, generation);
                    count++;
                }
            }else{
                reply = minimax(1 - side, alpha, beta, depth - 1);
                score = reply.score;
            }


            ////*** normal code
            //reply = minimax(1 - side, alpha, beta, depth - 1);
            //score = reply.score;


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
        // p.forceBoard("        " +
        //              "ox  o   " +
        //              "        " +
        //              "o  xo x " +
        //              "  ox  o " +
        //              "o       " +
        //              " xo  x  " +
        //              "  x     ");

        // p.forceBoard("   x    " +
        //              "oxo   o " +
        //              " x xox  " +
        //              " oo o   " +
        //              " x x    " +
        //              "oxoxo   " +
        //              "        " +
        //              " x      ");

        runGame();
        //p.interactiveDebug();
    }
}
