import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrimGUI extends JFrame {
    // JFrame - это контейнер верхнего уровня, который предоставляет окно на экране
    private CardLayout cardLayout; // Карточный компоновщик для переключения между панелями
    private JPanel mainPanel; // Основная панель, содержащая панели с различными способами ввода
    private AdjacencyMatrixPanel matrixPanel; // Панель для ввода матрицы смежности
    private GraphDrawingPanel drawingPanel; // Панель для рисования графа
    private AdjMatrixFromFilePanel filePanel;

    public PrimGUI() {
        setTitle("Prim's Algorithm GUI"); // Устанавливаем заголовок окна
        setSize(700, 700); // Устанавливаем размер окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Задаем действие при закрытии окна
        setLocationRelativeTo(null);

        cardLayout = new CardLayout(); // Инициализируем карточный компоновщик
        mainPanel = new JPanel(cardLayout); // Создаем основную панель с карточным компоновщиком

        matrixPanel = new AdjacencyMatrixPanel(); // Создаем панель для ввода матрицы смежности
        drawingPanel = new GraphDrawingPanel(); // Создаем панель для рисования графа
        filePanel = new AdjMatrixFromFilePanel(); // Создаем панель для ввода матрицы из файла

        // Добавляем панели на основную панель с указанием имен для карточного компоновщика
        mainPanel.add(drawingPanel, "Drawing Input");
        mainPanel.add(matrixPanel, "Matrix Input");
        mainPanel.add(filePanel, "File Input");

        // Создаем кнопки для переключения между панелями
        RoundedButton drawButton = new RoundedButton("Drawing Input", 20, new Color(50, 98, 255), 14);
        RoundedButton matrixButton = new RoundedButton("Matrix Input", 20, new Color(50, 98, 255), 14);
        RoundedButton fileButton = new RoundedButton("File Input", 20, new Color(50, 98, 255), 14);
        RoundedButton helpButton = new RoundedButton("?", 20, new Color(50, 98, 255), 14);

        // Добавляем обработчики событий для кнопок
        drawButton.addActionListener(e -> cardLayout.show(mainPanel, "Drawing Input"));
        matrixButton.addActionListener(e -> cardLayout.show(mainPanel, "Matrix Input"));
        fileButton.addActionListener(e -> cardLayout.show(mainPanel, "File Input"));
        // Добавление слушателя на кнопку "Q"
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelpWindow();
            }
        });

        // Панель для размещения кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(drawButton);
        buttonPanel.add(matrixButton);
        buttonPanel.add(fileButton);
        buttonPanel.add(helpButton);

        // Добавляем панель с кнопками на север, а основную панель на центр окна
        add(buttonPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void showHelpWindow() {
        JFrame helpFrame = new JFrame("Help");
        helpFrame.setSize(400, 450);
        helpFrame.setLayout(new BorderLayout());

        JTextPane helpTextPane = new JTextPane();
        helpTextPane.setContentType("text/html");
        helpTextPane.setText("<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Help:</h2>" +
                "<p><b>1. Drawing Input:</b><br>" +
                "Left click to create vertex<br>" +
                "To move a vertex, hold down the vertex with the left mouse button and drag<br>" +
                "Right click selects vertices or edges: to draw an edge, select 2 edges in succession<br>" +
                "To delete a vertex/edge, you must first select them, and then click the corresponding button from below</p>" +
                "<p><b>2. Matrix Input:</b><br>" +
                "1) Choose the matrix size<br>" +
                "2) Fill out the adjacency matrix<br>" +
                "3) Click the \"Draw graph\" button<br>" +
                "4) Now you can draw the MST</p>" +
                "<p><b>3. File Input:</b><br>" +
                "Select a file with a graph in the form of an adjacency matrix and click the \"Draw graph\" button<br>" +
                "! Elements of the adjacency matrix must be separated by a space</p>" +
                "</body>" +
                "</html>");
        helpTextPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(helpTextPane);
        helpFrame.add(scrollPane, BorderLayout.CENTER);

        helpFrame.setLocationRelativeTo(null); // Центрируем окно на экране
        helpFrame.setVisible(true);
    }


    public static void main(String[] args) {
        // Запускаем GUI в потоке обработки событий
        SwingUtilities.invokeLater(() -> {
            PrimGUI gui = new PrimGUI(); // Создаем экземпляр PrimGUI
            gui.setVisible(true); // Делаем окно видимым
        });
    }
}
