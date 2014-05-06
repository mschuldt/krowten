    // public Move chooseMove() {
    //     double chooseMoveStart = System.currentTimeMillis();
    //     int depth = searchDepth;
    //     if (depth == VAR_DEPTH){
    //         if (board.getNumPieces(ourColor) < 10-STEP_DEPTH){
    //             depth = ADD_DEPTH;
    //         }else{
    //             depth = STEP_DEPTH;
    //         }
    //     }
    //     double endTime = 0.0;
    //     Best bestMove = null;
    //     Best tmpBest = null;
    //     Best tmp = null;
    //     int d=0;
    //     startTime = System.currentTimeMillis();
    //     long beforeHash = board.hash();
    //     while (true){
    //         //startTime = System.currentTimeMillis();
    //         tmpBest = minimax(ourColor, -100000, 100000, depth);
    //         // startTime = System.currentTimeMillis();
    //         // tmp = minimax(ourColor, -100000, 100000, depth);
    //         // if (tmpBest.move == null ){
    //         //     System.out.println("Error: null moves (tmpBest)");
    //         // } else if (tmp.move == null){
    //         //     System.out.println("Error: null moves (tmp)");

    //         // } else if (!tmp.move.toString().equals(tmpBest.move.toString())){
    //         //     System.out.println("Error: failure to check move");
    //         //     System.out.println("  move 1:  " + tmpBest.move.toString());
    //         //     System.out.println("  move 2: " + tmp.move.toString());
    //         // }
    //         // tmp.move = copyMove(tmpBest.move);
    //         tmpBest.move = copyMove(tmpBest.move);
    //         //if (true){bestMove = tmpBest; break;};

    //         if ((tmpBest.score == OUT_OF_TIME) && (bestMove == null)){
    //             System.out.println("ran out of time. never found a move");
    //         }
    //         if (tmpBest.score != OUT_OF_TIME){
    //             bestMove = tmpBest;
    //             d = depth;
    //         }else{
    //             System.out.println("ran out of time");
    //             break;
    //         }
    //         if ((System.currentTimeMillis() - startTime)/1000.0 > 4.9){
    //             break;
    //         }
    //         depth++;
    //         if (depth >= MAX_DEPTH){
    //             System.out.println("error: exceeded max depth in choose move(" +depth+")");
    //         }

    //     }

    //     // startTime = System.currentTimeMillis();
    //     // Best vBest = minimax(ourColor, -100000, 100000, d);
    //     // vBest.move = copyMove(vBest.move);
    //     // if (beforeHash != board.hash()){
    //     //     System.out.println("error: board has been illegally modified");
    //     // }

    //     // if ((vBest.move == null) && (bestMove.move != null)){

    //     //     System.out.println("inconsistent move choosing (null 1)");
    //     // }
    //     // if ((vBest.move != null) && (bestMove.move == null)){
    //     //     System.out.println("inconsistent move choosing (null 2)");
    //     // }

    //     // if ((vBest.move != null) && (bestMove.move != null)){
    //     //     if (vBest.move.moveKind != bestMove.move.moveKind){
    //     //         System.out.println("inconsistent move choosing (type)");
    //     //     }
    //     //     if (! vBest.move.toString().equals(bestMove.move.toString())){
    //     //         //DOING: this error
    //     //         System.out.println("inconsistent move choosing(no match)");
    //     //         System.out.println("  chosen move:  " + bestMove.move.toString());
    //     //         System.out.println("  verification: " + vBest.move.toString());
    //     //         //bestMove.move = vBest.move;
    //     //     }
    //     // }

    //     System.out.println("searched to depth " + depth);

    //     if (bestMove.move == null){ //this happens sometimes...why?
    //         MoveList validmoves = new MoveList();
    //         board.validMoves(ourColor, validmoves);
    //         if (validmoves.length() == 0){
    //             System.out.println("no more moves");
    //             Move ret = new Move(0,0);
    //             ret.moveKind = Move.QUIT;
    //             System.out.println("move time: " + timeSince(chooseMoveStart));
    //             return ret;
    //         }
    //         bestMove.move = validmoves.get(0);
    //         System.out.println("fixed null move");
    //     }

    //     //make the move here instead of calling this.forceMove if we know that the move is valid
    //     board.move(bestMove.move, ourColor);
    //     System.out.println("move time: " + timeSince(chooseMoveStart));
    //     return bestMove.move;
    // }
