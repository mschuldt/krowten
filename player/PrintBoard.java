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
    
    public String toString(){
        char[][] charArray = blankCharArray();
        String ret = "";
        for (Cell[] row : cells){
            for (Cell c : row){
                c.showIndex = showNums;
                c.write(charArray);
            }
        }
        writeLines(charArray);
        return charArrayToString(charArray);
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

    private void writeLines(char[][] charArray){
        //TODO
        return;
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

