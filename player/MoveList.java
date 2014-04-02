package player;

//At instantiation, this list will fill itself with Move objects
//whose fields will be set when MoveList.add is called with
//values instead of a Move object.
//When MoveList.clear is called, the Move objects are not freed
//and will be reused the next time Move.add is called.
//If used correctly, this list may be reused while consuming a constant
//amount of memory

public class MoveList extends AList<Move>{

    public MoveList(){
        super(440);
        allocateMoves();
    }

    public MoveList(int n){
        super(n);
        allocateMoves();
    }

    private void allocateMoves(){
        for (int i = 0; i < maxSize;i++){
            //array[i] = new Move(0,0);
            super.add(new Move(0,0));
        }
        clear();
    }

    public void add(int x1, int y1){
        if (len < maxSize){
            Move m = get(len);
            m.x1 = x1;
            m.y1 = y1;
            m.moveKind = Move.ADD;
            len++;
        }else{
            //TODO: throw error (or resize)
            System.out.println("Error -- MoveList.add: overflow");
        }
    }

    public void add(int x1, int y1, int x2, int y2){
        if (len < maxSize){
            Move m = get(len);
            m.x1 = x1;
            m.y1 = y1;
            m.x2 = x2;
            m.y2 = y2;
            m.moveKind = Move.STEP;
            len++;
        }else{
            //TODO: throw error (or resize)
            System.out.println("Error -- MoveList.add: overflow");
        }
    }
}
