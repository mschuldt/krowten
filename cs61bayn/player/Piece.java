package cs61bayn.player;

public class Piece{
    public int color, x, y;
    public long bitRep; //binary representation

    public int numConnected;
    public Piece[] connected;

    public Piece(){}

    public Piece (int _color, long _bitRep, int _x, int _y){
        set(_color, _bitRep, _x, _y);
    }

    public Piece set(int _color, long _bitRep, int _x, int _y){
        connected = new Piece[8];
        numConnected = 0;
        color = _color;
        bitRep = _bitRep;
        x = _x;
        y = _y;
        return this;
    }

    public String toString(){
        return "<" + (color == 0 ? "Black" : "White")
            +" Piece at (" + x +"," + y + ")>";
    }

    public Piece[] connectedPieces(){
        return connected;
    }
}

