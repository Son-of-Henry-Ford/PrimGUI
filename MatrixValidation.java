/**
 * Класс валидации матрицы
 */
public class MatrixValidation {
    /* private final int[][] matrix;

    MatrixValidation(int[][] matrix) {
        this.matrix = matrix;
    } */

    /**
     * Метод проверки валидности матрицы по всем параметрам
     * @param matrix - матрица смежности
     * @return - возвращает флаг наличия ошибок в матрице
     */
    public static int checkMatrix(int[][] matrix) {
        if (!checkValues(matrix))
            return 1;
        if (!checkSymmetry(matrix))
            return 2;
        if (!checkConnectivity(matrix))
            return 3;
        return 0;
   }

    /**
     * Метод проверки валидности значений в матрице (положительные значения, граф без петель)
     * @param matrix - матрица смежности
     * @return true - матрица допустима, false - матрица недопустима
     */
    private static boolean checkValues(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix.length; j++) {
                if (matrix[i][j] < 0 || (i == j && matrix[i][j] != 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Метод проверки симметричности матрицы
     * @param matrix - матрица смежности
     * @return true - матрица допустима, false - матрица недопустима
     */
    private static boolean checkSymmetry(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix.length; j++) {
                if (matrix[i][j] != matrix[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Метод проверки графа на связность
     * @param matrix - матрица смежности
     * @return true - матрица допустима, false - матрица недопустима
     */
    private static boolean checkConnectivity(int[][] matrix) {
        boolean[] visited = new boolean[matrix.length];
        return dfs(matrix, 0, visited) == matrix.length;
    }

    /**
     * Поиск в глубину (для проверки связности графа)
     * @param matrix - матрица смежности
     * @param vertex - номер вершины, с которой начинается поиск в глубину
     * @param visited - трассировочный массив (посещали вершину с соответствующим номером или нет)
     * @return количество посещённых вершин при обходе в глубину
     */
    private static int dfs(int[][] matrix, int vertex, boolean[] visited) {
        int visitedVertices = 1;
        visited[vertex] = true;
        for (int i = 0; i < visited.length; i++) {
            if (matrix[vertex][i] != 0 && !visited[i]) {
                visitedVertices += dfs(matrix, i, visited);
            }
        }
        return visitedVertices;
    }

    /*public static void main(String[] s) {
        int[][] mtrx = {{0, 33, 31, 45, 7, 43, 43},
                {33, 0, 2, 34, 27, 44, 43},
                {31, 2, 0, 34, 25, 43, 42},
                {45, 34, 34, 0, 38, 16, 15},
                {7, 27, 25, 38, 0, 37, 37},
                {43, 44, 43, 16, 37, 0, 1},
                {43, 43, 42, 15, 37, 1, 0}};
        System.out.println(MatrixValidation.checkMatrix(mtrx));
    }*/

}
