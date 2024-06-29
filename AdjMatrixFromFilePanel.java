import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class AdjMatrixFromFilePanel extends JPanel {
    private int[][] matrix;
    private JTextArea outputArea; // Область для вывода результата выполнения алгоритма Прима
    private JPanel graphPanel; // Панель для отображения графа
    private JPanel mstPanel; // Панель для отображения МОД
    private int size; // Размер матрицы

    public AdjMatrixFromFilePanel() {
        setLayout(new GridBagLayout()); // Устанавливаем компоновщик GridBagLayout для главной панели
        GridBagConstraints gbc = new GridBagConstraints();

        // Панель для кнопки загрузки
        JPanel loadPanel = new JPanel();
        JButton loadButton = new JButton("Load Matrix from File");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMatrixFromFile();
                repaint();
            }
        });
        loadPanel.add(loadButton);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(loadPanel, gbc);

        // Область для вывода результата
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false); // Запрещаем редактирование области вывода
        JScrollPane scrollPane = new JScrollPane(outputArea); // Добавляем область вывода в прокручиваемую панель
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(scrollPane, gbc); // Добавляем прокручиваемую панель на восток главной панели

        // Панель для отображения графа
        graphPanel = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(graphPanel, gbc);

        // Панель отображения мод
        mstPanel = new JPanel();
        gbc.gridx = 1; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! если написать = 0, нарисует поверх
        add(mstPanel, gbc);

        // Перемещаем кнопку Calculate MST и Draw Graph в отдельную панель снизу
        JPanel bottomPanel = new JPanel();
        JButton calculateButton = new JButton("Calculate MST");
        calculateButton.addActionListener(e -> calculateMST()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(calculateButton);
        JButton drawGraphButton = new JButton("Draw Graph");
        drawGraphButton.addActionListener(e -> drawGraph()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(drawGraphButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);
    }

    // Метод для вычисления MST и вывода результата
    private void calculateMST() {
        PrimAlgorithm prim = new PrimAlgorithm(size); // Создаем объект алгоритма Прима
        int[][] mstTree = prim.primMST(matrix, outputArea); // Вычисляем MST и выводим результат в outputArea

        mstPanel.removeAll(); // Удаляем все компоненты с панели графа
        mstPanel.setLayout(new BorderLayout()); // Устанавливаем компоновщик BorderLayout для панели графа
        mstPanel.add(new GraphPanel(mstTree, size)); // Добавляем панель графа с новой матрицей
        mstPanel.revalidate(); // Перекомпоновываем компоненты
        mstPanel.repaint(); // Перерисовываем компоненты
    }

    // Метод для рисования графа по матрице смежности
    private void drawGraph() {
        graphPanel.removeAll(); // Удаляем все компоненты с панели графа
        graphPanel.setLayout(new BorderLayout()); // Устанавливаем компоновщик BorderLayout для панели графа
        graphPanel.add(new GraphPanel(matrix, size)); // Добавляем панель графа с новой матрицей
        graphPanel.revalidate(); // Перекомпоновываем компоненты
        graphPanel.repaint(); // Перерисовываем компоненты
    }

    private void loadMatrixFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                int lineCount = 0;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(" ");
                    if (matrix == null) {
                        size = values.length;
                        matrix = new int[size][size];
                    }
                    for (int i = 0; i < values.length; i++) {
                        matrix[lineCount][i] = Integer.parseInt(values[i]);
                    }
                    lineCount++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
