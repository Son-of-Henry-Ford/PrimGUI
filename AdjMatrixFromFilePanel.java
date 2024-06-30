import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class AdjMatrixFromFilePanel extends JPanel {
    private int[][] matrix;
    private RoundedTextArea outputArea; // Область для вывода результата выполнения алгоритма Прима
    private RoundedPanel graphPanel; // Панель для отображения графа
    private int size; // Размер матрицы
    private GraphPanel graphDraw;
    private int speed = 1;

    public AdjMatrixFromFilePanel() {
        setLayout(new GridBagLayout()); // Устанавливаем компоновщик GridBagLayout для главной панели
        GridBagConstraints gbc = new GridBagConstraints();

        // Панель для кнопки загрузки
        JPanel loadPanel = new JPanel();
        RoundedButton loadButton = new RoundedButton("Load Matrix from File", 25, new Color(50, 98, 255));
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

        JPanel settingPanel = new JPanel();

        JLabel speedLabel = new JLabel("Speed of changing steps:");
        RoundedTextArea speedField = new RoundedTextArea(1, 3, 15);
        RoundedButton setSpeedButton = new RoundedButton("Set speed", 25, new Color(50, 98, 255));

        loadPanel.add(speedLabel);
        loadPanel.add(speedField);
        loadPanel.add(setSpeedButton);

        add(settingPanel, gbc);

        // Область для вывода результата
        outputArea = new RoundedTextArea(10, 35, 25);
        outputArea.setEditable(false); // Запрещаем редактирование области вывода
        JScrollPane scrollPane = new JScrollPane(outputArea); // Добавляем область вывода в прокручиваемую панель
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Убираем черную рамку вокруг области
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.CENTER;
        add(scrollPane, gbc); // Добавляем прокручиваемую панель на восток главной панели

        // Панель для отображения графа
        graphPanel = new RoundedPanel(new FlowLayout(), 25, Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(graphPanel, gbc);

        // Перемещаем кнопку Calculate MST и Draw Graph в отдельную панель снизу
        JPanel bottomPanel = new JPanel();
        RoundedButton calculateButton = new RoundedButton("Calculate MST", 25, new Color(50, 98, 255));
        calculateButton.addActionListener(e -> calculateMST()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(calculateButton);
        RoundedButton drawGraphButton = new RoundedButton("Draw Graph", 25, new Color(50, 98, 255));
        drawGraphButton.addActionListener(e -> drawGraph()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(drawGraphButton);

        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);

        // Обработчик нажатия кнопки установки скорости
        setSpeedButton.addActionListener(e -> {
            try {
                speed = Integer.parseInt(speedField.getText()); // Преобразуем текст из поля ввода в целое число
                if (speed < 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a number greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                // Если введено не число, показываем сообщение об ошибке
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Метод для вычисления MST и вывода результата
    private void calculateMST() {
        if (graphDraw == null){
            JOptionPane.showMessageDialog(this, "Please draw graph fist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PrimAlgorithm prim = new PrimAlgorithm(matrix, size); // Создаем объект алгоритма Прима
        Map<Edge, String> mstMap = prim.primMST();

        Graphics g = graphPanel.getGraphics();

        int delay = speed*1000; // Задержка в миллисекундах (2 секунды)
        // Создаем новый поток для выполнения операций с GUI
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (Edge edge : mstMap.keySet()) {
                    SwingUtilities.invokeAndWait(() -> {
                        graphDraw.drawEdge(g, edge.src, edge.dest);
                        outputArea.setText(mstMap.get(edge));
                    });
                    try {
                        Thread.sleep(delay); // Задержка между итерациями цикла
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                return null;
            }
        };
        worker.execute(); // Запускаем SwingWorker
    }

    // Метод для рисования графа по матрице смежности
    private void drawGraph() {
        graphPanel.removeAll(); // Удаляем все компоненты с панели графа
        graphPanel.setLayout(new BorderLayout()); // Устанавливаем компоновщик BorderLayout для панели графа
        graphDraw = new GraphPanel(matrix, size);
        graphPanel.add(graphDraw); // Добавляем панель графа с новой матрицей
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
