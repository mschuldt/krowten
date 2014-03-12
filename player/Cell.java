// player/Cell.java

package player;

public class Cell{
    int x;
    int y;
    String row1, row2, row3;
    String defaultChar = " ";
    boolean mark = false, showIndex = false;
    String markChar = "~";
    String emptyMarkChar = "$";
    boolean isEdge = false;

    public Cell(int x, int y, String c){
        assert c.length() == 1 : "Error - Cell.Cell: invalid init string length";
        assert x >= 0 && x <= 7 : "Error - Cell.Cell: invalid x index:" + x;
        assert y >= 0 && y <= 7 : "Error - Cell.Cell: invalid y index:" + y;
        this.x = x;
        this.y = y;
        defaultChar = c;
    }
    public Cell(int x, int y){
        this(x, y, " ");
    }

    //write this cell to the array
    public void write(char[][] array){
        //array is an array of rows
        int xInd = 4+8*x,
            yInd = 2+4*y;

        assert x >= 0 && xInd < 65 : "Error - Cell.write: invalid x index: " + xInd;
        assert y >= 0 && yInd < 33 : "Error - Cell.write: invalid x index: " + yInd;
        if (isEdge){
            mark = showIndex = false;
        }
        
        row3 = buildStr(7, defaultChar);
        
        if (mark){
            String s = defaultChar + defaultChar;
            String m = (defaultChar==" " ? emptyMarkChar : markChar);
            row2 = s + buildStr(3, m) + s;
        }else{
            row2 = row3;
        }
        row1 = (showIndex ? x +""+ y + buildStr(5,defaultChar) : row3);

        assert row1.length() == 7 : "Error - Cell.write: invalid row1 length. row1 = '" + row1 + "'";
        assert row2.length() == 7 : "Error - Cell.write: invalid row2 length. row1 = '" + row2 + "'";
        assert row3.length() == 7 : "Error - Cell.write: invalid row3 length. row1 = '" + row3 + "'";

        char[] r1 = row1.toCharArray();
        char[] r2 = row2.toCharArray();
        char[] r3 = row3.toCharArray();

        for (int x = xInd-3,i=0;  x< xInd+4; x++,i++){
            array[yInd-1][x]= r1[i];
            array[yInd][x]   = r2[i];
            array[yInd+1][x] = r3[i];
        }
    }

    private String buildStr(int n, String c){
        String ret = "";
        for (int i=0;i < n; i++){
            ret += c;
        }
        return ret;
    }
}

