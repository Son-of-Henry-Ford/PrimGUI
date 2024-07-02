import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphDrawingPanel extends JPanel {
    private ArrayList<Point> points;
    private ArrayList<Edge> edges;
    private Point tempPoint;
    private int[][] graph;
    private RoundedTextArea outputArea;     // Область для вывода результата выполнения алгоритма Прима
    private RoundedPanel drawingPanel;
    private static final int PADDING = 20; //отступ (в пикселях) от краёв панели
    private int currentStep = 0;        // текущий шаг мод
    private List<Edge> edgesMST = new ArrayList<>(); // список ребер мод
    private List<String> messages = new ArrayList<>(); // список сообщений мод


    public GraphDrawingPanel() {
        setLayout(new BorderLayout());

        points = new ArrayList<>();
        edges = new ArrayList<>();
        tempPoint = null;

        outputArea = new RoundedTextArea(10, 35, 25);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.NORTH);

        JPanel stepButtonPanel = new JPanel();
        stepButtonPanel.setLayout(new BoxLayout(stepButtonPanel, BoxLayout.Y_AXIS));
        stepButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Устанавливаем отступы

        RoundedButton setStepButton = new RoundedButton(" \u276F ", 25, new Color(50, 98, 255), 20);
        setStepButton.addActionListener(e -> StepForward());
        RoundedButton setBackButton = new RoundedButton(" \u276E ", 25, new Color(50, 98, 255), 20);
        setBackButton.addActionListener(e -> StepBack());
        RoundedButton calculateButton = new RoundedButton("\u276F\u276F", 25, new Color(50, 98, 255), 20);
        calculateButton.addActionListener(e -> calculateMST());

        stepButtonPanel.add(Box.createVerticalStrut(20));
        stepButtonPanel.add(setStepButton);
        stepButtonPanel.add(Box.createVerticalStrut(20));
        stepButtonPanel.add(setBackButton);
        stepButtonPanel.add(Box.createVerticalStrut(20));
        stepButtonPanel.add(calculateButton);
        stepButtonPanel.add(Box.createVerticalStrut(20));

        add(stepButtonPanel, BorderLayout.EAST);


        JPanel graphButtonPanel = new JPanel();

        RoundedButton saveGraphButton = new RoundedButton("Save graph", 25, new Color(50, 98, 255), 14);
        saveGraphButton.addActionListener(e -> SaveGraph()); // Добавляем обработчик нажатия кнопки
        RoundedButton deleteNodeButton = new RoundedButton("Delete node", 25, new Color(255, 67, 50), 14);
        //deleteNodeButton.addActionListener(e -> deleteNode());
        RoundedButton deleteEdgeButton = new RoundedButton("Delete edge", 25, new Color(255, 67, 50), 14);
        //deleteEdgeButton.addActionListener(e -> deleteEdge());
        RoundedButton deleteGraphButton = new RoundedButton("Delete graph", 25, new Color(255, 67, 50), 14);
        //deleteGraphButton.addActionListener(e -> deleteGraph());

        graphButtonPanel.add(saveGraphButton);
        graphButtonPanel.add(deleteNodeButton);
        graphButtonPanel.add(deleteEdgeButton);
        graphButtonPanel.add(deleteGraphButton);

        add(graphButtonPanel, BorderLayout.SOUTH);

        drawingPanel = new RoundedPanel(new FlowLayout(), 25, Color.WHITE) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Рисуем ребра
                g2.setStroke(new BasicStroke(2)); // Устанавливаем толщину линии для ребер
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                for (Edge edge : edges) {
                    g2.setColor(Color.BLACK);
                    Point p1 = points.get(edge.src);
                    Point p2 = points.get(edge.dest);
                    if (edge.src > edge.dest){ // чтобы веса были с одной стороны
                        p2 = p1;
                        p1 = points.get(edge.dest);
                    }
                    g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
                    int weightX = (int) (p1.x * 0.75 + p2.x * 0.25);
                    int weightY = (int) (p1.y * 0.75 + p2.y * 0.25);
                    g2.setColor(Color.BLUE);
                    g2.drawString(String.valueOf(edge.weight), weightX, weightY);
                }
                // Рисуем вершины
                g2.setColor(new Color(115, 64, 254));
                int i = 0;
                for (Point point : points) {
                    g2.fillOval(point.x - 15, point.y - 15, 30, 30);
                    g2.setColor(Color.BLACK);
                    g2.drawString(String.valueOf(i), point.x - 5, point.y + 5);
                    g2.setColor(new Color(115, 64, 254));
                    i++;
                }
            }
        };
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isWithinDrawingArea(e.getX(), e.getY())) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        points.add(e.getPoint());
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        Point clickedPoint = e.getPoint();
                        int clickedIndex = findPointIndex(clickedPoint);
                        if (clickedIndex != -1) {
                            if (tempPoint == null) {
                                tempPoint = clickedPoint;
                            } else {
                                int tempIndex = findPointIndex(tempPoint);
                                if (tempIndex != -1) {
                                    edges.add(new Edge(tempIndex, clickedIndex, 1));
                                }
                                tempPoint = null;
                            }
                        } else {
                            tempPoint = null;
                            JOptionPane.showMessageDialog(GraphDrawingPanel.this, "Please click on an existing vertex to create an edge.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    repaint();
                }
            }
        });
        add(drawingPanel, BorderLayout.CENTER);
    }

    private boolean isWithinDrawingArea(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        return x >= PADDING && x <= width - PADDING && y >= PADDING && y <= height - PADDING;
    }

    private int findPointIndex(Point point) {
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (p.distance(point) < 15) {
                return i;
            }
        }
        return -1;
    }

    private void StepForward(){
        if (currentStep == edgesMST.size()){
            return; // здесь просто ничего не делать или попросить нарисовать граф?
        }

        Edge edge = edgesMST.get(currentStep);
        drawEdge(edge.src, edge.dest, new Color(49, 168, 116), new Color(65, 199, 139));
        outputArea.setText(messages.get(currentStep));
        currentStep++;
    }

    private void StepBack(){
        if (currentStep < 1){
            return; // здесь просто ничего не делать или попросить нарисовать граф?
        }
        currentStep--;
        Edge edge = edgesMST.get(currentStep);
        drawEdge(edge.src, edge.dest, Color.BLACK,new Color(115, 64, 254));
        if (currentStep == 0){
            outputArea.setText("");
            return;
        }
        outputArea.setText(messages.get(currentStep-1));
    }

    public void drawEdge(int i, int j, Color edgeColor, Color vertexColor) {
        if (i > j){ // чтобы с одной стороны цифры писались
            int k = i;
            i = j;
            j = k;
        }
        Graphics2D g2 = (Graphics2D) drawingPanel.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(edgeColor); // Устанавливаем цвет линии для ребер
        g2.setStroke(new BasicStroke(2)); // Устанавливаем толщину линии для ребер
        g2.draw(new Line2D.Double(points.get(i).x, points.get(i).y, points.get(j).x, points.get(j).y));

        // Вычисляем координаты для размещения веса ребра
        double offsetFactor = 0.25; // Чем больше значение, тем ближе к первой вершине
        int weightX = (int) ((1 - offsetFactor) * points.get(i).x + offsetFactor * points.get(j).x);
        int weightY = (int) ((1 - offsetFactor) * points.get(i).y + offsetFactor * points.get(j).y);
        // Рисуем вес ребра
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        if (i < j) { // Чтобы вес рисовался только с одной стороны
            g2.drawString(String.valueOf(graph[i][j]), weightX, weightY);
        }else {
            g2.drawString(String.valueOf(graph[j][i]), weightX, weightY);
        }

        g2.setColor(vertexColor); // Устанавливаем цвет для вершин
        // Рисуем вершины в виде круга поверх ребра
        g2.fillOval(points.get(i).x - 15, points.get(i).y - 15, 30, 30);
        g2.fillOval(points.get(j).x - 15, points.get(j).y - 15, 30, 30);
        g2.setColor(Color.BLACK); // Устанавливаем цвет для текста
        // Рисуем номера вершин
        g2.drawString(String.valueOf(i), points.get(i).x - 5, points.get(i).y + 5);
        g2.drawString(String.valueOf(j), points.get(j).x - 5, points.get(j).y + 5);
    }

    private void SaveGraph(){
        int vertexCount = points.size(); // Количество вершин в графе
        graph = new int[vertexCount][vertexCount]; // Создаем пустую матрицу смежности

        // Заполняем матрицу смежности на основе списка ребер
        for (Edge edge : edges) {
            int start = edge.src;
            int end = edge.dest;
            int weight = edge.weight;
            graph[start][end] = weight;
            graph[end][start] = weight; // Граф симметричный
        }

        PrimAlgorithm prim = new PrimAlgorithm(graph, graph.length); // Создаем объект алгоритма Прима
        Map<Edge, String> mstMap = prim.primMST();

        // Создаем списки для ключей и значений
        edgesMST = new ArrayList<>(mstMap.keySet());
        messages = new ArrayList<>(mstMap.values());
    }
    // Метод для вывода результата
    private void calculateMST() {
        // при пустом графе выдавать ошибку?
        /*if (edgesMST.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please draw graph fist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }*/

        // Создаем списки для ключей и значений
        for (int i = 0; i < edgesMST.size(); i++) {
            Edge edge = edgesMST.get(i);
            drawEdge(edge.src, edge.dest, new Color(49, 168, 116), new Color(65, 199, 139));
            outputArea.setText(messages.get(i));
            currentStep++;
        }
    }
}
