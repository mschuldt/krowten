// player/PrintBoard.java

package player;

import java.util.List;
import java.util.ArrayList;

public class PrintBoard{
    Cell[][] cells;
    boolean showNums = true;
    List<int[]> lines = new ArrayList<int[]>();

    public PrintBoard(Board b){
        cells = b.toCellArray();
    }

    public void drawLine(int x1, int y1, int x2, int y2){
        if (x1 < 0 || x2 < 0 || y1 < 0 || y2 < 0
            || x1 > 7 || y1 > 7 || y2 > 7 || y2 > 7){
            System.out.println("Error - PrintBoard.drawline: invalid line indexes");
        }
        lines.add(new int[] {x1, y1, x2, y2});
    }

    private char[][] toCharArray(){
        char[][] charArray = blankCharArray();
        String ret = "";
        for (Cell[] row : cells){
            for (Cell c : row){
                c.showIndex = showNums;
                c.write(charArray);
            }
        }
        writeLines(charArray);
        return charArray;
    }
    
    public String toString(){
        return charArrayToString(toCharArray());
    }
    
    public String toString(String ourBB, String oppBB){
        return charArrayToString(toCharArray(),ourBB, oppBB);
    }
    
    public void showNumbers(){
        showNums = true;
    }
    public void hideNumbers(){
        showNums = false;
    }
    
    public void markAll(){
        for (Cell[] row: cells){
            for (Cell cell : row){
                cell.mark = true;
            }
        }
    }
    public void mark(int x, int y){
        cells[x][y].mark = true;
    }
    public void mark(Piece[] pieces){
        for (Piece p : pieces){
            mark(p.x, p.y);
        }
    }

    private int toCharXIndex(int x){
        return 4+8*x;
    }
    private int toCharYIndex(int y){
        return 2+4*y;
    }
    //board char array is 64x33
    private boolean isValidCharIndex(int x,int y){
        boolean is = x>=0 && y>= 0 && x < 64 && y < 33;
        if (is){
            return true;
        }
        System.out.println("Invalid Char Index");
        return false;
    }
    
    private void writeLines(char[][] charArray){
        int n = lines.size();
        int[] line;
        int yInc = 0,xInc=0,startX=0, startY=0, endX=0, endY=0,tmp=0;
        boolean vert,horiz;
        double y;
        double m;
        for (int i = 0; i < n; i++){
            line = lines.get(i);
            startX = toCharXIndex(line[0]);
            startY = toCharYIndex(line[1]);
            endX = toCharXIndex(line[2]);
            endY = toCharYIndex(line[3]);
            
            //TODO: 'line 23 55' fails
            
            if ((startY > endY) || (startX > endX)){ //shitty bug fix
                tmp = startY;
                startY = endY;
                endY = tmp;
                tmp = startX;
                startX = endX;
                endX = tmp;

            }
            // System.out.println("startX = "+startX);
            // System.out.println("startY = "+startY);
            // System.out.println("endX = "+endX);
            // System.out.println("endY = "+endY);

            horiz = startY == endY;
            vert = startX == endX;
            yInc = (startY > endY ? -1 : 1);
            if (vert){
                //                System.out.println("yInc= " + yInc);
                while (startY != endY){
                    charArray[startY][startX] = '*';
                    startY += yInc;
                }
                charArray[startY][startX] = '*';
                continue;
            }
            xInc = (startX > endX ? -1 : 1);
            //            System.out.println("xInc= " + xInc);
            if (horiz){
                while (startX != endX){
                    charArray[startY][startX] = '*';
                    startX += xInc;
                }
                charArray[startY][startX] = '*';
                continue;
            }
            
            m = (endY - startY)/((double)(endX - startX));
            y = startY;
            //            System.out.println("m = "+m);
            //doing: 
            while (startX != endX && y != endY
                   && isValidCharIndex(startX, (int)y)){
                // System.out.print("("+ startX +", " + (int)y + ")") ;
                charArray[(int)Math.ceil(y)][startX] = '*';
                startX += xInc;
                y += m;
            }
            //charArray[(int)Math.ceil(y)][startX] = '*';
            
        }
    }
    
    private String charArrayToString(char[][] array){
        String ret = "";
        for (char[] row : array){
            for (char c : row){
                ret += c;
            }
            ret += "\n";
        }
        return ret;
    }
    private String charArrayToString(char[][] array,String ourBB, String opponentBB){
        String ret = "";
        int count = 0;
        int nrows = array.length;

        String[] ourRows = ourBB.split("\n");
        String[] oppRows = opponentBB.split("\n");
        
        for (char[] row : array){
            for (char c : row){
                ret += c;
            }
            count++;
            ret += (count == 11 ? " ours" : "");
            ret += (count == 22 ? " Opponents" : "");
            if (count > 11 && count < 20){
                ret += " " + ourRows[count-12];
            }
            if (count > 22 && count < 31){
                ret += " " + oppRows[count-23];
            }
            ret += "\n";
        }
        return ret;
    }

    // private char[][] blankCharArray(){
    //     Piece piece;
    //     char[][] rows = new char[33][];
    //     String sep = "-----------------------------------------------------------------";
    //     String bars = "|       |       |       |       |       |       |       |       |";
    //     rows[0] = sep.toCharArray();
    //     int rowNum = 1;
    //     String row = "";
    //     for (int y = 1; y < 9; y++){
    //         //cell size is 7 across
    //         for (int _ = 0; _ < 3; _++){
    //             rows[rowNum] = bars.toCharArray();
    //             rowNum ++;
    //         }
    //         rows[rowNum] = sep.toCharArray();
    //         rowNum++;
    //     }
    //     return rows;
    // }

    public char[][] blankCharArray(){
        char[][] board = {  // Oh, glorious Emacs keyboard macros!
            "        -------------------------------------------------        ".toCharArray(),
            "        |       |       |       |       |       |       |        ".toCharArray(),
            "        |       |       |       |       |       |       |        ".toCharArray(),
            "        |       |       |       |       |       |       |        ".toCharArray(),
            "-----------------------------------------------------------------".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "-----------------------------------------------------------------".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "-----------------------------------------------------------------".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "-----------------------------------------------------------------".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "-----------------------------------------------------------------".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "-----------------------------------------------------------------".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "|       |       |       |       |       |       |       |       |".toCharArray(),
            "-----------------------------------------------------------------".toCharArray(),
            "        |       |       |       |       |       |       |        ".toCharArray(),
            "        |       |       |       |       |       |       |        ".toCharArray(),
            "        |       |       |       |       |       |       |        ".toCharArray(),
            "        -------------------------------------------------        ".toCharArray()};
        return board;
    }
}
