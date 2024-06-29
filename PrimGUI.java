import javax.swing.*;
import java.awt.*;

public class PrimGUI extends JFrame {
    // JFrame - это контейнер верхнего уровня, который предоставляет окно на экране
    private CardLayout cardLayout; // Карточный компоновщик для переключения между панелями
    private JPanel mainPanel; // Основная панель, содержащая панели с различными способами ввода
    private AdjacencyMatrixPanel matrixPanel; // Панель для ввода матрицы смежности
    private GraphDrawingPanel drawingPanel; // Панель для рисования графа
    private AdjMatrixFromFilePanel filePanel;

    public PrimGUI() {
        setTitle("Prim's Algorithm GUI"); // Устанавливаем заголовок окна
        setSize(700, 600); // Устанавливаем размер окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Задаем действие при закрытии окна

        cardLayout = new CardLayout(); // Инициализируем карточный компоновщик
        mainPanel = new JPanel(cardLayout); // Создаем основную панель с карточным компоновщиком

        matrixPanel = new AdjacencyMatrixPanel(); // Создаем панель для ввода матрицы смежности
        drawingPanel = new GraphDrawingPanel(); // Создаем панель для рисования графа
        filePanel = new AdjMatrixFromFilePanel(); // Создаем панель для ввода матрицы из файла

        // Добавляем панели на основную панель с указанием имен для карточного компоновщика
        mainPanel.add(matrixPanel, "Matrix Input");
        mainPanel.add(drawingPanel, "Graph Drawing");
        mainPanel.add(filePanel, "File Input");

        // Создаем кнопки для переключения между панелями
        JButton matrixButton = new JButton("Matrix Input");
        JButton drawButton = new JButton("Graph Drawing");
        JButton fileButton = new JButton("File Input");

        // Добавляем обработчики событий для кнопок
        matrixButton.addActionListener(e -> cardLayout.show(mainPanel, "Matrix Input"));
        drawButton.addActionListener(e -> cardLayout.show(mainPanel, "Graph Drawing"));
        fileButton.addActionListener(e -> cardLayout.show(mainPanel, "File Input"));

        // Панель для размещения кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(matrixButton);
        buttonPanel.add(drawButton);
        buttonPanel.add(fileButton);

        // Добавляем панель с кнопками на север, а основную панель на центр окна
        add(buttonPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Запускаем GUI в потоке обработки событий
        SwingUtilities.invokeLater(() -> {
            PrimGUI gui = new PrimGUI(); // Создаем экземпляр PrimGUI
            gui.setVisible(true); // Делаем окно видимым
        });
    }
}
