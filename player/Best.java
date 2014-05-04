package player;

public class Best{
    public int score;
    public Move move;
    public int evaledBoards;
    public Best(){
        score = 0;
        evaledBoards = 1;
        move = null;
    }
}
