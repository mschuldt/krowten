package player;

public class PieceList extends AList<Piece>{
    //add a piece to the back of this list if it is not null and not an edge piece
    public PieceList(){
        super(8);//motel
    }

    public void addIfPiece(Piece p){
        if (p == null || p == Board.edge){
            return;
        }
        add(p);
    }
    public void addIfPiece(Piece[] pieces){
        for (Piece p : pieces){
            addIfPiece(p);
        }
    }
    //add Piece P if piece has color COLOR
    //if P is null, it is not added to the list
    public void addIfColor(Piece p, int color){
        if (p != null && p.color == color && p != Board.edge){
            add(p);
        }
    }
    public void addIfColor(Piece pieces[], int color){
        Piece p;
        for (Object obj : pieces){
            p = (Piece) obj;
            addIfColor(p, color);
        }
    }
}
