package player; // Better place to put this??

public class Piece{
    public int color;
    public int x;
    public int y;
    public boolean isEdge;
    public Piece (int _color, int _x, int _y){
        isEdge = false;
        color = _color;
        x = _x;
        y = _y;
    }
}
