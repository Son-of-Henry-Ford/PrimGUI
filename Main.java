import java.util.Map;


public class Main {
    public static void main(String[] args) {
        int[][] mtrx = {{0, 33, 31, 45, 7, 43, 43},
                        {33, 0, 2, 34, 27, 44, 43},
                        {31, 2, 0, 34, 25, 43, 42},
                        {45, 34, 34, 0, 38, 16, 15},
                        {7, 27, 25, 38, 0, 37, 37},
                        {43, 44, 43, 16, 37, 0, 1},
                        {43, 43, 42, 15, 37, 1, 0}};
        int size = 7;
        PrimAlgorithm primAlg = new PrimAlgorithm(mtrx, size);
        Map<Edge, String> resMap= primAlg.primMST();

        for (Map.Entry<Edge, String> entry : resMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}