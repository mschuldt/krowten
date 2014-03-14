package player;

public class PieceList extends List<Piece>{
    //add a piece to the back of this list if it is not null and not an edge piece
    public void addIfPiece(Piece p){
        if (p == null || p == Board.edge){
            return;
        }
        insertBack(p);
    }
    public void addIfPiece(Piece[] pieces){
        for (Piece p : pieces){
            addIfPiece(p);
        }
    }
}
