package player;

public class PieceList extends AList<Piece>{
    //add a piece to the back of this list if it is not null and not an edge piece
    public PieceList(){
        super(8);//motel
    }
    public PieceList(int n){
        super(n);
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

    public boolean containsPiece(long bitRep){
        for (int i=0;i < len;i++){
            if (get(i).bitRep == bitRep){
                return true;
            }
        }
        return false;
    }
    public boolean containsPiece(Piece p){
        return p != null && containsPiece(p.bitRep);
    }

    public PieceList shuffled(){//for testing
        PieceList newList = new PieceList(len);
        if (len==0){
            return newList;
        }

        Piece tmp=null;
        int i=0;

        int max = len*3;
        while (true){
            i++;
            tmp = get(get(i%len).x*(get(i%len).y +13/*must be prime*/) % len);
            if (! newList.containsPiece(tmp)){
                newList.add(tmp);
            }
            if (newList.length() == len || i > max){
                break;
            }
        }
        if (newList.length() < len){
            for (Piece p : this){
                if (! newList.containsPiece(p)){
                    newList.add(p);
                }
            }
        }
        return newList;
    }
}
