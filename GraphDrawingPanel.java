import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class GraphDrawingPanel extends JPanel {
    private ArrayList<Point> points;
    private ArrayList<Edge> edges;
    private Point tempPoint;
    private int[][] graph;
    private JPanel graphMSTPanel; // Панель для отображения графа

    public GraphDrawingPanel() {
        points = new ArrayList<>();
        edges = new ArrayList<>();
        tempPoint = null;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
        });
    }
    // Метод ищет ближайшую вершину к точке клика на расстоянии менее 15 пикселей от точки клика
    private int findPointIndex(Point point) {
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (p.distance(point) < 15) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем ребра
        // g2.setColor(Color.RED); - чтобы цифры и ребра были разного цвета
        for (Edge edge : edges) {
            g2.setColor(Color.BLACK);
            Point p1 = points.get(edge.src);
            Point p2 = points.get(edge.dest);
            g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
            int weightX = (p1.x + p2.x) / 2;
            int weightY = (p1.y + p2.y) / 2;
            g2.setColor(Color.BLUE);
            g2.drawString(String.valueOf(edge.weight), weightX, weightY);
        }
        // Рисуем вершины
        g2.setColor(Color.getHSBColor(0.65206814F, 0.5829787F, 0.92156863F));
        int i = 0;
        for (Point point : points) {
            g2.fillOval(point.x - 15, point.y - 15, 30, 30);
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(i), point.x - 5, point.y + 5);
            g2.setColor(Color.getHSBColor(0.65206814F, 0.5829787F, 0.92156863F));
            i++;
        }
    }
    // преобразование ребер в матрицу смежности
    private void convertEdgesToAdjacencyMatrix() {
        int vertexCount = points.size(); // Количество вершин в графе
        graph = new int[vertexCount][vertexCount]; // Создаем пустую матрицу смежности

        // Заполняем матрицу смежности на основе списка ребер
        for (Edge edge : edges) {
            int start = edge.src;
            int end = edge.dest;
            int weight = edge.weight;
            graph[start][end] = weight;
            graph[end][start] = weight; // Если граф неориентированный
        }
    }

    private void drawMST(){
        setLayout(new GridBagLayout()); // Устанавливаем компоновщик GridBagLayout для главной панели
        GridBagConstraints gbc = new GridBagConstraints();
        int size = points.size(); // Количество вершин в графе
        convertEdgesToAdjacencyMatrix();

        graphMSTPanel = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(graphMSTPanel, gbc);

        graphMSTPanel.removeAll(); // Удаляем все компоненты с панели графа
        graphMSTPanel.setLayout(new BorderLayout()); // Устанавливаем компоновщик BorderLayout для панели графа
        graphMSTPanel.add(new GraphPanel(graph, size)); // Добавляем панель графа с новой матрицей
        graphMSTPanel.revalidate(); // Перекомпоновываем компоненты
        graphMSTPanel.repaint(); // Перерисовываем компоненты
    }
}
