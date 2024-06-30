import javax.swing.*;
import java.awt.*;
import java.util.Map;


class AdjacencyMatrixPanel extends JPanel {
    private RoundedTextField[][] matrixFields; // Массив для хранения полей ввода матрицы
    private RoundedTextArea outputArea; // Область для вывода результата выполнения алгоритма Прима
    private RoundedPanel matrixPanel; // Панель для размещения полей ввода матрицы
    private RoundedPanel graphPanel; // Панель для отображения графа
    private int size; // Размер матрицы
    private GraphPanel graphDraw;
    private int speed = 1;

    public AdjacencyMatrixPanel() {
        setLayout(new GridBagLayout()); // Устанавливаем компоновщик GridBagLayout для главной панели
        GridBagConstraints gbc = new GridBagConstraints();

        // Панель для ввода размера матрицы
        JPanel settingPanel = new JPanel();

        JLabel sizeLabel = new JLabel("Enter size of matrix:");
        RoundedTextArea sizeField = new RoundedTextArea(1, 3, 15);
        RoundedButton setSizeButton = new RoundedButton("Set Size", 25, new Color(50, 98, 255));

        JLabel speedLabel = new JLabel("Speed of changing steps:");
        RoundedTextArea speedField = new RoundedTextArea(1, 3, 15);
        RoundedButton setSpeedButton = new RoundedButton("Set speed", 25, new Color(50, 98, 255));

        // Добавляем элементы на панель ввода размера матрицы
        settingPanel.add(sizeLabel);
        settingPanel.add(sizeField);
        settingPanel.add(setSizeButton);

        settingPanel.add(speedLabel);
        settingPanel.add(speedField);
        settingPanel.add(setSpeedButton);

        // Добавляем панель ввода размера матрицы
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.CENTER;
        add(settingPanel, gbc);

        // Панель для размещения полей ввода матрицы
        matrixPanel = new RoundedPanel(new FlowLayout(), 25, getBackground());
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        add(matrixPanel, gbc);

        // Область для вывода результата
        outputArea = new RoundedTextArea(10, 35, 25);
        outputArea.setEditable(false); // Запрещаем редактирование области вывода
        JScrollPane scrollPane = new JScrollPane(outputArea); // Добавляем область вывода в прокручиваемую панель
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Убираем черную рамку вокруг области
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(scrollPane, gbc); // Добавляем прокручиваемую панель

        // Панель для отображения графа
        graphPanel = new RoundedPanel(new FlowLayout(), 25, Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(graphPanel, gbc);

        // Обработчик нажатия кнопки установки размера
        setSizeButton.addActionListener(e -> {
            try {
                size = Integer.parseInt(sizeField.getText()); // Преобразуем текст из поля ввода в целое число
                if (size > 10 || size < 1) {
                    JOptionPane.showMessageDialog(this, "Please enter a number less 11 and greater than 1.", "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    createMatrix(size); // Создаем матрицу указанного размера
                }
            } catch (NumberFormatException ex) {
                // Если введено не число, показываем сообщение об ошибке
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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

    // Метод для создания матрицы заданного размера
    private void createMatrix(int size) {
        matrixPanel.removeAll(); // Удаляем все компоненты с панели матрицы
        matrixPanel.setLayout(new GridLayout(size, size)); // Устанавливаем компоновщик GridLayout для панели матрицы
        matrixFields = new RoundedTextField[size][size]; // Инициализируем массив полей ввода

        Dimension cellSize = new Dimension(30, 30); // Размер ячейки
        Dimension panelSize = new Dimension(size * cellSize.width, size * cellSize.height); // Размер панели в зависимости от количества ячеек
        matrixPanel.setPreferredSize(panelSize); // Устанавливаем предпочтительный размер панели

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrixFields[i][j] = new RoundedTextField(4, 5); // Создаем новое поле ввода
                matrixFields[i][j].setHorizontalAlignment(JTextField.CENTER); // Центрируем текст в поле ввода
                matrixFields[i][j].setPreferredSize(cellSize); // Устанавливаем предпочтительный размер
                matrixPanel.add(matrixFields[i][j]); // Добавляем поле ввода на панель матрицы
            }
        }
        // Перекомпоновываем и перерисовываем компоненты
        matrixPanel.revalidate();
        matrixPanel.repaint();

        // Перемещаем кнопку Calculate MST и Draw Graph в отдельную панель снизу
        JPanel bottomPanel = new JPanel();
        RoundedButton calculateButton = new RoundedButton("Draw MST", 20, new Color(50, 98, 255));
        calculateButton.addActionListener(e -> calculateMST()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(calculateButton);
        RoundedButton drawGraphButton = new RoundedButton("Draw Graph", 20, new Color(50, 98, 255));
        drawGraphButton.addActionListener(e -> drawGraph()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(drawGraphButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);
    }

    // Метод для вычисления MST и вывода результата
    private void calculateMST() {
        int[][] matrix = new int[size][size]; // Инициализируем массив для хранения значений матрицы
        try {
            for (int i = 0; i < size; i++) {
                for (int j = i; j < size; j++) {
                    if (i == j){
                        matrix[i][j] = 0;
                    }
                    int num = Integer.parseInt(matrixFields[i][j].getText()); // Преобразуем текст из полей ввода в числа
                    if (num >= 0) {
                        matrix[i][j] = num;
                        matrix[j][i] = num;
                    }else {
                        JOptionPane.showMessageDialog(this, "Please enter non-negative numbers", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } catch (NumberFormatException ex) {
            // Если введено не число, показываем сообщение об ошибке
            JOptionPane.showMessageDialog(this, "Please fill all matrix fields with valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        int[][] matrix = new int[size][size]; // Инициализируем массив для хранения значений матрицы
        try {
            for (int i = 0; i < size; i++) {
                for (int j = i; j < size; j++) {
                    if (i == j){
                        matrix[i][j] = 0;
                    }
                    int num = Integer.parseInt(matrixFields[i][j].getText()); // Преобразуем текст из полей ввода в числа
                    if (num >= 0) {
                        matrix[i][j] = num;
                        matrix[j][i] = num;
                    }else {
                        JOptionPane.showMessageDialog(this, "Please enter non-negative numbers", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } catch (NumberFormatException ex) {
            // Если введено не число, показываем сообщение об ошибке
            JOptionPane.showMessageDialog(this, "Please fill all matrix fields with valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        graphPanel.removeAll(); // Удаляем все компоненты с панели графа
        graphPanel.setLayout(new BorderLayout()); // Устанавливаем компоновщик BorderLayout для панели графа
        graphDraw = new GraphPanel(matrix, size);
        graphPanel.add(graphDraw); // Добавляем панель графа с новой матрицей
        graphPanel.revalidate(); // Перекомпоновываем компоненты
        graphPanel.repaint(); // Перерисовываем компоненты
    }
}
