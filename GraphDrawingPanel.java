import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphDrawingPanel extends JPanel {
    private ArrayList<Point> points;
    private ArrayList<Edge> edges;
    private Point tempRightPoint;    // выделенная вершина
    int tempRightPointIndex = -1;    // индекс выделенной вершины
    private Point draggedPoint = null; // вершина, которую зажали ЛКМ
    private int draggedPointIndex = -1; // индекс вершины, которую зажали ЛКМ
    private Edge tempEdge;
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
        tempRightPoint = null;

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
        deleteNodeButton.addActionListener(e -> deleteNode());
        RoundedButton deleteEdgeButton = new RoundedButton("Delete edge", 25, new Color(255, 67, 50), 14);
        deleteEdgeButton.addActionListener(e -> deleteEdge());
        RoundedButton deleteGraphButton = new RoundedButton("Delete graph", 25, new Color(255, 67, 50), 14);
        deleteGraphButton.addActionListener(e -> deleteGraph());

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
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                for (Edge edge : edges) {
                    Point p1 = points.get(edge.src);
                    Point p2 = points.get(edge.dest);
                    if (edge.src > edge.dest){ // чтобы веса были с одной стороны
                        p2 = p1;
                        p1 = points.get(edge.dest);
                    }
                    if (tempEdge != null && tempEdge.src == edge.src && tempEdge.dest == edge.dest) {
                        g2.setColor(new Color(253, 149, 74));
                        g2.setStroke(new BasicStroke(6)); // Устанавливаем толщину подсветки для ребра
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                    g2.setStroke(new BasicStroke(2)); // Устанавливаем толщину линии для ребер
                    g2.setColor(new Color(115, 64, 254).darker());
                    g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
                    int weightX = (int) (p1.x * 0.7 + p2.x * 0.3);
                    int weightY = (int) (p1.y * 0.7 + p2.y * 0.3);
                    g2.setColor(new Color(115, 64, 254));
                    g2.drawString(String.valueOf(edge.weight), weightX, weightY);
                }
                // Рисуем вершины
                g2.setColor(new Color(115, 64, 254));
                int i = 0;
                for (Point point : points) {
                    if (i == tempRightPointIndex){
                        g2.setColor(new Color(253, 149, 74));
                        g2.fillOval(point.x - 19, point.y - 19, 38, 38);
                        g2.setColor(new Color(115, 64, 254));
                    }
                    if (i == 0){
                        g2.setColor(new Color(65, 199, 139));
                        g2.fillOval(point.x - 15, point.y - 15, 30, 30);
                        g2.setColor(Color.BLACK);
                        g2.drawString(String.valueOf(i), point.x - 5, point.y + 5);
                        g2.setColor(new Color(115, 64, 254));
                    } else {
                        g2.fillOval(point.x - 15, point.y - 15, 30, 30);
                        g2.setColor(Color.BLACK);
                        g2.drawString(String.valueOf(i), point.x - 5, point.y + 5);
                        g2.setColor(new Color(115, 64, 254));
                    }
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
                        int clickedIndex = findPointIndex(e.getPoint());
                        if (clickedIndex != -1) {
                            draggedPoint = points.get(clickedIndex);
                            draggedPointIndex = clickedIndex;
                        } else {
                            points.add(e.getPoint());
                        }
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        Point clickedPoint = e.getPoint();
                        Edge clickedEdge = isPointOnEdge(clickedPoint);
                        if (clickedEdge != null && tempEdge != null && tempEdge.dest == clickedEdge.dest  && tempEdge.src == clickedEdge.src) {
                            String weightStr = JOptionPane.showInputDialog(
                                    GraphDrawingPanel.this,
                                    "Enter the weight of the edge:",
                                    "Input Edge Weight",
                                    JOptionPane.PLAIN_MESSAGE
                            );
                            if (weightStr != null) {
                                try {
                                    int weight = Integer.parseInt(weightStr);
                                    edges.remove(tempEdge);
                                    edges.add(new Edge(tempEdge.src, tempEdge.dest, weight));
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(
                                            GraphDrawingPanel.this,
                                            "Invalid input. Please enter an integer value.",
                                            "Input Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                            tempEdge = null; // после изменения веса отменяем выбор
                        }else {
                            tempEdge = isPointOnEdge(clickedPoint);
                        }

                        int clickedIndex = findPointIndex(clickedPoint);
                        if (clickedIndex != -1) {
                            tempEdge = null; // если выделена вершина, то ребро не выделено
                            if (tempRightPoint == null) {
                                tempRightPoint = clickedPoint;
                                tempRightPointIndex = findPointIndex(tempRightPoint);
                            } else {
                                int tempIndex = findPointIndex(tempRightPoint);
                                if (tempIndex != -1 && tempIndex != clickedIndex) { // исключаем петли
                                    String weightStr = JOptionPane.showInputDialog(
                                            GraphDrawingPanel.this,
                                            "Enter the weight of the edge:",
                                            "Input Edge Weight",
                                            JOptionPane.PLAIN_MESSAGE
                                    );

                                    if (weightStr != null) {
                                        try {
                                            int weight = Integer.parseInt(weightStr);
                                            edges.add(new Edge(tempIndex, clickedIndex, weight));
                                        } catch (NumberFormatException ex) {
                                            JOptionPane.showMessageDialog(
                                                    GraphDrawingPanel.this,
                                                    "Invalid input. Please enter an integer value.",
                                                    "Input Error",
                                                    JOptionPane.ERROR_MESSAGE
                                            );
                                        }
                                    }
                                }
                                tempRightPoint = null;
                                tempRightPointIndex = points.size() + 1;
                            }
                        } else {
                            tempRightPoint = null;
                            tempRightPointIndex = -1;
                        }
                    }
                    repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                draggedPoint = null;
                draggedPointIndex = -1;
            }
        });

        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedPoint != null && draggedPointIndex != -1) {
                    draggedPoint.setLocation(e.getPoint());
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

    public Edge isPointOnEdge(Point point) {
        double buffer = 5.0; // Допустимое отклонение для учета погрешности
        for (Edge edge : edges) {
            Point p1 = points.get(edge.src);
            Point p2 = points.get(edge.dest);

            // Проверка, что точка находится на линии
            double lineDistance = Line2D.ptSegDist(p1.x, p1.y, p2.x, p2.y, point.x, point.y);
            if (lineDistance > buffer) {
                continue;
            }
            // Проверка, что точка находится между p1 и p2
            if (Math.min(p1.x, p2.x) - buffer <= point.x && point.x <= Math.max(p1.x, p2.x) + buffer &&
                    Math.min(p1.y, p2.y) - buffer <= point.y && point.y <= Math.max(p1.y, p2.y) + buffer){
                return edge;
            }
        }
        return null;
    }

    private void StepForward(){
        if (currentStep == edgesMST.size()){
            return; // здесь просто ничего не делать или попросить нарисовать граф?
        }

        Edge edge = edgesMST.get(currentStep);
        drawEdge(edge.src, edge.dest, new Color(65, 199, 139), new Color(65, 199, 139));
        outputArea.setText(messages.get(currentStep));
        currentStep++;
    }

    private void StepBack(){
        if (currentStep < 1){
            return; // здесь просто ничего не делать или попросить нарисовать граф?
        }
        currentStep--;
        Edge edge = edgesMST.get(currentStep);
        drawEdge(edge.src, edge.dest, new Color(65, 199, 139), new Color(115, 64, 254));
        if (currentStep == 0){
            outputArea.setText("");
            return;
        }
        outputArea.setText(messages.get(currentStep-1));
    }

    public void drawEdge(int i, int j, Color oldColor, Color newColor) {
        Graphics2D g2 = (Graphics2D) drawingPanel.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(newColor.darker()); // Устанавливаем цвет линии для ребер
        g2.setStroke(new BasicStroke(2)); // Устанавливаем толщину линии для ребер
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.draw(new Line2D.Double(points.get(i).x, points.get(i).y, points.get(j).x, points.get(j).y));

        // Рисуем вершины в виде круга поверх ребра
        g2.setColor(oldColor); // Устанавливаем цвет для первой вершины
        g2.fillOval(points.get(i).x - 15, points.get(i).y - 15, 30, 30);

        g2.setColor(newColor); // Устанавливаем цвет для второй вершины
        g2.fillOval(points.get(j).x - 15, points.get(j).y - 15, 30, 30);
        g2.setColor(Color.BLACK); // Устанавливаем цвет для текста
        // Рисуем номера вершин
        g2.drawString(String.valueOf(i), points.get(i).x - 5, points.get(i).y + 5);
        g2.drawString(String.valueOf(j), points.get(j).x - 5, points.get(j).y + 5);

        add(drawingPanel, BorderLayout.CENTER);
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
            graph[start][start] = 0;
        }

        switch (MatrixValidation.checkMatrix(graph)) {
            case 0:
                break;
            case 1:
                JOptionPane.showMessageDialog(this, "Please fill all matrix fields with valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            case 2:
                JOptionPane.showMessageDialog(this, "The adjacency matrix must be symmetric.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            case 3:
                JOptionPane.showMessageDialog(this, "The graph must be connected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
        currentStep = 0;
        PrimAlgorithm prim = new PrimAlgorithm(graph, graph.length); // Создаем объект алгоритма Прима
        Map<Edge, String> mstMap = prim.primMST();

        // Создаем списки для ключей и значений
        edgesMST = new ArrayList<>(mstMap.keySet());
        messages = new ArrayList<>(mstMap.values());
    }
    // Метод сразу рисует МОД
    private void calculateMST() {
        // при пустом графе выдавать ошибку?
        /*if (edgesMST.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please draw graph fist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }*/
        currentStep = 0;
        // Создаем списки для ключей и значений
        for (int i = 0; i < edgesMST.size(); i++) {
            Edge edge = edgesMST.get(i);
            drawEdge(edge.src, edge.dest, new Color(65, 199, 139), new Color(65, 199, 139));
            outputArea.setText(messages.get(i));
            currentStep++;
        }
    }

    private void deleteGraph(){
        points.clear();
        edges.clear();
        edgesMST.clear();
        messages.clear();
        tempRightPoint = null;
        tempRightPointIndex = -1;
        draggedPoint = null;
        draggedPointIndex = -1;
        currentStep = 0;
        repaint();
        outputArea.setText("");
    }

    private void deleteNode(){
        if (tempRightPoint != null) {
            points.remove(tempRightPointIndex);
            edges.removeIf(edge -> edge.src == tempRightPointIndex || edge.dest == tempRightPointIndex);
            tempRightPoint = null;
            tempRightPointIndex = -1;
            draggedPoint = null;
            draggedPointIndex = -1;
            currentStep = 0;
            repaint();
        }
    }

    private void deleteEdge(){
        edges.remove(tempEdge);
        tempEdge = null;
        currentStep = 0;
        repaint();
    }
}