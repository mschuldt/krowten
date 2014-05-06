package cs61bayn.player;
import player.Move;

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
