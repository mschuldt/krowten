/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
    Board board;
    int searchDepth;
    // Creates a machine player with the given color.  Color is either 0 (black)
    // or 1 (white).  (White has the first move.)
    public MachinePlayer(int color) {
	board = new Board(color);
	searchDepth = 3;//TODO: determine suitable default
    }

    // Creates a machine player with the given color and search depth.  Color is
    // either 0 (black) or 1 (white).  (White has the first move.)
    public MachinePlayer(int color, int searchDepth) {
	board = new Board(color);
	this.searchDepth = searchDepth;
    }

    // Returns a new move by "this" player.  Internally records the move (updates
    // the internal game board) as a move by "this" player.
    public Move chooseMove() {
        /* Boolean side;
        if (color == this.board.ourColor) { // need to figure out of color is even necessary or what
            side = true;
        }
        else{
            side = false;
        }
        Best bestMove = minimax(true, -1, 1, depth) //TODO: optimal depth and evaluation function
        return bestMove.Move;
        */
	return new Move();
    } 


    /**
     * Minimax algorithm with alpha-beta pruning which returns a Best object with a score and Move
     **/
    
    public Best minimax(Boolean side, int alpha, int beta, int depth){
        Best myBest = new Best();
        Best reply;
        if (this.board.hasNetwork()){ // with or without color argument? 
            return myBest; //not sure...
        }
        if (depth == 0){
            //myBest.score = this.board.scoreBoard(this.board, this); TODO
            return myBest;
        }
        if (side){
            myBest.score = alpha;
        }else{
            myBest.score = beta;
        }
        AList allValidMoves = this.board.validMoves();
        for (int i = 0; i < allValidMoves.length(); i++){ // validMoves returns a list
            Move m = (Move) allValidMoves.get(i); 
            this.board.move(m);
            reply = minimax(!side, alpha, beta, depth-1); // ummmmm
            this.board.unMove(m);
            if (side && (reply.score >= myBest.score)){
                myBest.move = m;
                myBest.score = reply.score;
                alpha = reply.score;
            } else if (!side && (reply.score <= myBest.score)){
                myBest.move = m;
                myBest.score = reply.score;
                beta = reply.score;
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
    public boolean opponentMove(Move m) {
	return false;
    }

    // If the Move m is legal, records the move as a move by "this" player
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method is used to help set up "Network problems" for your
    // player to solve.
    public boolean forceMove(Move m) {
	return false;
    }

}
