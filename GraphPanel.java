import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class GraphPanel extends JPanel {
    private int[][] matrix; // матрица смежности графа
    private int vertexCount; // количество вершин в графе

    public GraphPanel(int[][] matrix, int vertexCount) {
        this.matrix = matrix;
        this.vertexCount = vertexCount;
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
        }
        // Рисуем ребра
        g2.setStroke(new BasicStroke(2)); // Устанавливаем толщину линии для ребер
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                if (matrix[i][j] > 0) { // Проверяем, есть ли ребро между вершинами i и j
                    // Рисуем линию (ребро) между вершинами
                    g2.setColor(Color.RED); // Устанавливаем цвет линии для ребер
                    g2.draw(new Line2D.Double(points[i].x, points[i].y, points[j].x, points[j].y));
                    // Вычисляем координаты для размещения веса ребра
                    int weightX = (points[i].x + points[j].x) / 2;
                    int weightY = (points[i].y + points[j].y) / 2;
                    // Рисуем вес ребра
                    g2.setColor(Color.BLACK);
                    g2.drawString(String.valueOf(matrix[i][j]), weightX, weightY);
                }
            }
        }
        // Рисуем вершины
        g2.setColor(Color.BLUE); // Устанавливаем цвет для вершин
        for (int i = 0; i < vertexCount; i++) {
            // Рисуем вершину в виде круга
            g2.fillOval(points[i].x - 15, points[i].y - 15, 30, 30);
            g2.setColor(Color.BLACK); // Устанавливаем цвет для текста
            // Рисуем номер вершины
            g2.drawString(String.valueOf(i), points[i].x - 5, points[i].y + 5);
            g2.setColor(Color.BLUE); // Возвращаем цвет для следующей вершины
        }
    }
}
