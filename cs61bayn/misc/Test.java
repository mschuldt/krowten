public class Test{
    long a,b,c;

    public static void main (String[] args){
        double startTime;
        int size = 5000000;

        System.out.println("timing 2d array creation: ");
        startTime = System.currentTimeMillis();
        long[][] array = new long[size][];
        for (int i = 0; i < size;i ++){
            array[i] = new long[3];
        }
        System.out.println("  time: "
                           + (System.currentTimeMillis() - startTime)/1000.0);

        System.out.println("timing object creation: ");
        startTime = System.currentTimeMillis();
        Test[] Oarray   = new Test[size];
        for (int i = 0; i < size;i ++){
            Oarray[i] = new Test();
        }
        System.out.println("  time: "
                           + (System.currentTimeMillis() - startTime)/1000.0);


        System.out.println("timing 2d array access: ");
        startTime = System.currentTimeMillis();
        for (int i = 0 ; i < size;i++){
            array[i][0] = (long)i;
            array[i][1] = array[i][0];
            array[i][2] = array[i][1];
        }
        System.out.println("  time: "
                           + (System.currentTimeMillis() - startTime)/1000.0);


        System.out.println("timing object array access: ");
        startTime = System.currentTimeMillis();
        for (int i = 0 ; i < size;i++){
            Oarray[i].a = (long)i;
            Oarray[i].b = Oarray[i].a;
            Oarray[i].c = Oarray[i].c;
        }
        System.out.println("  time: "
                           + (System.currentTimeMillis() - startTime)/1000.0);

    }
}
