import java.util.*;

/**
 * Класс алгоритма Прима построения МОД
 */

public class PrimAlgorithm {
    private final int[][] matrix;
    private final int size; // мб убрать и вычислить через matrix.length
    private Map<Edge, String> map = new LinkedHashMap<Edge, String>();

    /**
     * Конструктор класса
     * @param matrix - двумерный массив примитивных целочисленных значений (матрица смежности исходного графа)
     * @param size - размер матрицы смежности (количество вершин графа)
     */
    public PrimAlgorithm(int[][] matrix, int size) {
        this.matrix = matrix;
        this.size = size;
        // this.size = matrix.length;
    }

    /**
     * Метод, реализующий алгоритм Прима
     * @return Map<Edge, String> - ассоциативный массив, у которого ключ - ребро МОД выбранное на текущем шаге,
     * а значение - информация о текущем шаге
     */
    public Map<Edge, String> primMST() {
        // System.out.println("\nBUILDING MST:");
        int n = size;
        Set<Integer> visitedNodes = new HashSet<>();
        ArrayList<Edge> selectedEdges = new ArrayList<Edge>();
        //Set<Edge> selectedEdges = new HashSet<>(); // не сохраняет порядок
        // Integer[][] res = new Integer[n][n];
        int[][] res = new int[n][n]; // будущая матирца смежности МОД

        while (visitedNodes.size() != n) {
            // Сообщения об узлах можно не выводить, т.к. они буду наглядно раскрашены
            // System.out.printf("VISITED NODES = %s%n", visitedNodes);
            // System.out.printf("UNVISITED NODES = %s%n", getUnvisitedNodes(n, visitedNodes));
            // Float minEdge = Float.POSITIVE_INFINITY;
            int minEdge = Integer.MAX_VALUE;
            int start = 0, end = 0;
            StringBuilder curStep = new StringBuilder();

            /*if (!visitedNodes.isEmpty()) {
                System.out.print("UNSELECTED EDGES: ");
            }*/
            // System.out.print(!visitedNodes.isEmpty() ? "UNSELECTED EDGES: " : "\n");
            curStep.append(!visitedNodes.isEmpty() ? "UNSELECTED EDGES: " : "\n");
            for (int i = 0; i < n; i++) {
                if (!visitedNodes.contains(i)) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (visitedNodes.contains(j) || i == j) {
                        continue;
                    }
                    // System.out.printf("%d, %d = %d   ", i, j, matrix[i][j]);
                    curStep.append(i + "-" + j + " = " + matrix[i][j]+ ", ");
                    if (matrix[i][j] < minEdge) {
                        minEdge = matrix[i][j];
                        start = i;
                        end = j;
                    }
                }
            }

            if (minEdge != Integer.MAX_VALUE) {
                // System.out.printf("\nADD MIN EDGE = (%d, %d) = %d\n", start, end, minEdge);
                curStep.append("\nADD MIN EDGE = (" + start + ", " + end + ") = " + minEdge + "\n");
                selectedEdges.add(new Edge(start, end, minEdge));
                // System.out.printf("SELECTED EDGES = %s\n\n", selectedEdges);
                curStep.append("SELECTED EDGES = " + selectedEdges);
                map.put(selectedEdges.getLast(), curStep.toString());
            }
            visitedNodes.add(end);
            minEdge = minEdge == Integer.MAX_VALUE ? 0 : minEdge; // Проверка для первого шага
            res[start][end] = res[end][start] = minEdge;
        }

        // System.out.printf("VISITED NODES = %s\n", visitedNodes);
        // System.out.println("UNVISITED NODES = {}");
        // System.out.printf("SELECTED EDGES = %s\n", selectedEdges);

        /* // Вывод результата (матрицы смежности МОД)
        System.out.println("\nMST:");
        for (int[] row : res) {
            for (int value : row) {
                System.out.printf("%d ", value);
            }
            System.out.println();
        } */
        // System.out.println(map);
        return map;
    }

    /**
     * Метод (для отладки), возвращающий список непосещённых вершин в виде строки
     * @param n общее количество вершин
     * @param visitedNodes множество посещённых вершин
     * @return Строка с перечислением непосещенных вершин
     */
    private static String getUnvisitedNodes(int n, Set<Integer> visitedNodes) {
        StringBuilder unvisitedNodes = new StringBuilder("[");
        for (int i = 0; i < n; i++) {
            if (!visitedNodes.contains(i)) {
                unvisitedNodes.append(i).append(", ");
            }
        }
        unvisitedNodes.deleteCharAt(unvisitedNodes.length() - 1); // Удаление последней запятой
        unvisitedNodes.append("]");
        return unvisitedNodes.toString();
    }
}