import javax.swing.*;
import java.awt.*;


class AdjacencyMatrixPanel extends JPanel {
    private JTextField[][] matrixFields; // Массив для хранения полей ввода матрицы
    private RoundedTextArea outputArea; // Область для вывода результата выполнения алгоритма Прима
    private JPanel matrixPanel; // Панель для размещения полей ввода матрицы
    private RoundedPanel graphPanel; // Панель для отображения графа
    private int size; // Размер матрицы
    private GraphPanel graphDraw;

    public AdjacencyMatrixPanel() {
        setLayout(new GridBagLayout()); // Устанавливаем компоновщик GridBagLayout для главной панели
        GridBagConstraints gbc = new GridBagConstraints();

        // Панель для ввода размера матрицы
        JPanel sizePanel = new JPanel();
        JLabel sizeLabel = new JLabel("Enter size of matrix:");
        RoundedTextArea sizeField = new RoundedTextArea(1, 4, 15);
        //JButton setSizeButton = new JButton("Set Size");
        RoundedButton setSizeButton = new RoundedButton("Set Size", 25, new Color(154, 154, 154));
        //sizePanel.setBackground(Color.LIGHT_GRAY);

        // Добавляем элементы на панель ввода размера матрицы
        sizePanel.add(sizeLabel);
        sizePanel.add(sizeField);
        sizePanel.add(setSizeButton);

        // Добавляем панель ввода размера матрицы на север главной панели
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(sizePanel, gbc);

        // Панель для размещения полей ввода матрицы
        matrixPanel = new JPanel();
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(matrixPanel, gbc);

        // Область для вывода результата
        //gbc.insets = new Insets(10, 10, 10, 10);
        outputArea = new RoundedTextArea(10, 30, 25);
        outputArea.setEditable(false); // Запрещаем редактирование области вывода
        JScrollPane scrollPane = new JScrollPane(outputArea); // Добавляем область вывода в прокручиваемую панель
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Убираем черную рамку вокруг области
        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridx = 1;
        add(scrollPane, gbc); // Добавляем прокручиваемую панель

        //RoundedPanel textPanel = new RoundedPanel(new BorderLayout(), 25, Color.GRAY);
        //add(textPanel, gbc); //

        // Панель для отображения графа
        graphPanel = new RoundedPanel(new FlowLayout(), 25, Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        //graphPanel.setBackground(Color.LIGHT_GRAY);
        add(graphPanel, gbc);

        // Обработчик нажатия кнопки установки размера
        setSizeButton.addActionListener(e -> {
            try {
                size = Integer.parseInt(sizeField.getText()); // Преобразуем текст из поля ввода в целое число
                createMatrix(size); // Создаем матрицу указанного размера
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
        matrixFields = new JTextField[size][size]; // Инициализируем массив полей ввода

        Dimension cellSize = new Dimension(30, 30); // Размер ячейки
        Dimension panelSize = new Dimension(size * cellSize.width, size * cellSize.height); // Размер панели в зависимости от количества ячеек
        matrixPanel.setPreferredSize(panelSize); // Устанавливаем предпочтительный размер панели

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrixFields[i][j] = new JTextField(); // Создаем новое поле ввода
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
        //JButton calculateButton = new JButton("Calculate MST");
        RoundedButton calculateButton = new RoundedButton("Calculate MST", 20, new Color(154, 154, 154));
        calculateButton.addActionListener(e -> calculateMST()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(calculateButton);
        //JButton drawGraphButton = new JButton("Draw Graph");
        RoundedButton drawGraphButton = new RoundedButton("Draw Graph", 20, new Color(154, 154, 154));
        drawGraphButton.addActionListener(e -> drawGraph()); // Добавляем обработчик нажатия кнопки
        bottomPanel.add(drawGraphButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
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
                    matrix[i][j] = Integer.parseInt(matrixFields[i][j].getText()); // Преобразуем текст из полей ввода в числа
                    matrix[j][i] = Integer.parseInt(matrixFields[i][j].getText()); //
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

        PrimAlgorithm prim = new PrimAlgorithm(size); // Создаем объект алгоритма Прима
        int[][] mstTree = prim.primMST(matrix); // Вычисляем MST и выводим результат в outputArea

        Graphics g = graphPanel.getGraphics();

        for (int i = 0; i < mstTree.length; i++) {
            for (int j = i; j < mstTree[i].length; j++) {
                if (mstTree[i][j] > 0){
                    graphDraw.drawEdge(g, i, j);
                }
            }
        }
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
                    matrix[i][j] = Integer.parseInt(matrixFields[i][j].getText()); // Преобразуем текст из полей ввода в числа
                    matrix[j][i] = Integer.parseInt(matrixFields[i][j].getText()); //
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
