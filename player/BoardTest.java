package player;

public class BoardTest{
    //version of this test (in case new fields/tests are added later)
    public int version;
    public String board; //string representation of the board to test
    public long whiteBB; //white bitboard
    public long blackBB; //black bitboard
    //all the squares that would form white/black clusters 
    public long whiteClustersBB; 
    public long blackClustersBB;
    //all the white/black legal moves
    public long whiteLegalMovesBB;
    public long blackLegalMovesBB;
    //length 8 array of bitboards, the ith bitboard represents all
    //pieces that have i connections
    public long[] whiteConnectedPieces;
    public long[] blackConnectedPieces;
    public boolean blackNetwork; //true if black has a network
    public boolean whiteNetwork; //true if white has a network
    public int blackNumPieces; //number of black pieces
    public int whiteNumPieces; //number of white pieces
    //flags indicating if the given tests where passed by this board
    public boolean passedBitBoardTests;
    public boolean passedGoalTests;
    public boolean passedPieceCountTests;
    private int id;//id number of this individual test
    private static int count = 0;
    public static AList<BoardTest> tests = new AList<BoardTest>(1000);

    public BoardTest(int v){
	version = v;
	id = count;
	count++;
	tests.add(this);
    }

    public int getId(){
	return id;
    }
    public int numTests(){
	return count;
    }
    public String toString(){
	return "TODO: BoardTest.toString()";
    }
    
}
