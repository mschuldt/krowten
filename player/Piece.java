package player;

public class Piece{
    public int color, x, y;
    public long bitRep; //binary representation
    public Piece (int _color, long _bitRep, int _x, int _y){
        color = _color;
	bitRep = _bitRep;
        x = _x;
        y = _y;
    }
}
