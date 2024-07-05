import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

public class GraphPanel extends JPanel {
    private int[][] matrix; // матрица смежности графа
    private int vertexCount; // количество вершин в графе
    private Point[] points; // массив точек для хранения координат вершин

    public GraphPanel(int[][] matrix, int vertexCount) {
        this.matrix = matrix;
        this.vertexCount = vertexCount;
        points = new Point[vertexCount];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Вызов метода суперкласса для очистки панели

        Graphics2D g2 = (Graphics2D) g; // Преобразуем Graphics в Graphics2D для более точного рисования
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Включаем сглаживание для улучшения качества рисования

        int width = getWidth(); // Ширина панели
        int height = getHeight(); // Высота панели
        int radius = Math.min(width, height) / 2 - 50; // Радиус окружности, по которой будут расположены вершины
        int centerX = width / 2; // X-координата центра панели
        int centerY = height / 2; // Y-координата центра панели

        // Массив точек для хранения координат вершин
        Point[] points = new Point[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            // Вычисляем угол для текущей вершины
            double angle = 2 * Math.PI * i / vertexCount;
            // Вычисляем координаты вершины, расположенной на окружности
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            points[i] = new Point(x, y);
            this.points[i] = points[i];
        }

        // Рисуем область графа с закругленными краями
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(10, 10, getWidth() - 20, getHeight() - 20, 50, 50));

        // Рисуем ребра
        g2.setStroke(new BasicStroke(2)); // Устанавливаем толщину линии для ребер
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {
                if (matrix[i][j] > 0) { // Проверяем, есть ли ребро между вершинами i и j
                    // Рисуем линию (ребро) между вершинами
                    g2.setColor(new Color(115, 64, 254).darker()); // Устанавливаем цвет линии для ребер
                    g2.draw(new Line2D.Double(points[i].x, points[i].y, points[j].x, points[j].y));
                    // Вычисляем координаты для размещения веса ребра
                    double offsetFactor = 0.3; // Чем больше значение, тем ближе к первой вершине
                    int weightX = (int) ((1 - offsetFactor) * points[i].x + offsetFactor * points[j].x);
                    int weightY = (int) ((1 - offsetFactor) * points[i].y + offsetFactor * points[j].y);
                    // Рисуем вес ребра
                    //g2.setFont(new Font("Arial", Font.BOLD, 12));
                    g2.setColor(new Color(115, 64, 254));
                    g2.drawString(String.valueOf(matrix[i][j]), weightX, weightY);
                }
            }
        }
        // Рисуем вершины
        g2.setColor(new Color(115, 64, 254)); // Устанавливаем цвет для вершин
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i < vertexCount; i++) {
            // Рисуем вершину в виде круга
            g2.fillOval(points[i].x - 15, points[i].y - 15, 30, 30);
            g2.setColor(Color.BLACK); // Устанавливаем цвет для текста
            // Рисуем номер вершины
            g2.drawString(String.valueOf(i), points[i].x - 5, points[i].y + 5);
            g2.setColor(new Color(115, 64, 254)); // Возвращаем цвет для следующей вершины
        }
    }

    public void drawEdge(Graphics g, int i, int j, Color oldColor, Color newColor) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(newColor.darker()); // Устанавливаем цвет линии для ребер
        g2.setStroke(new BasicStroke(2)); // Устанавливаем толщину линии для ребер
        g2.draw(new Line2D.Double(points[i].x, points[i].y, points[j].x, points[j].y));
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        // Рисуем вершины в виде круга поверх ребра
        g2.setColor(oldColor); // Устанавливаем цвет для первой вершины
        g2.fillOval(points[i].x - 15, points[i].y - 15, 30, 30);

        g2.setColor(newColor); // Устанавливаем цвет для второй
        g2.fillOval(points[j].x - 15, points[j].y - 15, 30, 30);

        g2.setColor(Color.BLACK); // Устанавливаем цвет для текста
        // Рисуем номера вершин
        g2.drawString(String.valueOf(i), points[i].x - 5, points[i].y + 5);
        g2.drawString(String.valueOf(j), points[j].x - 5, points[j].y + 5);
    }
}