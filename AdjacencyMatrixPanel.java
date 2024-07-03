import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;


class AdjacencyMatrixPanel extends JPanel {
    private RoundedTextField[][] matrixFields; // Массив для хранения полей ввода матрицы
    private RoundedTextArea outputArea; // Область для вывода результата выполнения алгоритма Прима
    private RoundedPanel matrixPanel;   // Панель для размещения полей ввода матрицы
    private RoundedPanel graphPanel;    // Панель для отображения графа
    private int size;                   // Размер матрицы
    private GraphPanel graphDraw;       // Область с графом
    private List<Edge> edges = new ArrayList<>(); // список ребер мод
    private List<String> messages = new ArrayList<>(); // список сообщений мод
    private int currentStep = 0;        // текущий шаг мод

    public AdjacencyMatrixPanel() {
        setLayout(new GridBagLayout()); // Устанавливаем компоновщик GridBagLayout для главной панели
        GridBagConstraints gbc = new GridBagConstraints();

        // Панель для ввода размера матрицы
        JPanel settingPanel = new JPanel();

        JLabel sizeLabel = new JLabel("Enter size of matrix:");
        RoundedTextArea sizeField = new RoundedTextArea(1, 3, 15);
        RoundedButton setSizeButton = new RoundedButton("Set Size", 25, new Color(50, 98, 255), 14);

        // Добавляем элементы на панель ввода размера матрицы
        settingPanel.add(sizeLabel);
        settingPanel.add(sizeField);
        settingPanel.add(setSizeButton);

        // Добавляем панель ввода размера матрицы
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.CENTER;
        add(settingPanel, gbc);

        // Панель для размещения полей ввода матрицы
        matrixPanel = new RoundedPanel(new FlowLayout(), 25, getBackground());
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
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
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        add(scrollPane, gbc); // Добавляем прокручиваемую панель

        // Панель для отображения графа
        graphPanel = new RoundedPanel(new FlowLayout(), 25, Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 5;
        gbc.gridheight = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(graphPanel, gbc);

        JPanel stepButtonPanel = new JPanel();
        stepButtonPanel.setLayout(new BoxLayout(stepButtonPanel, BoxLayout.Y_AXIS));
        stepButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Устанавливаем отступы

        RoundedButton setStepButton = new RoundedButton(" \u276F ", 25, new Color(50, 98, 255), 20);
        setStepButton.addActionListener(e -> StepForward()); // Добавляем обработчик нажатия кнопки
        RoundedButton setBackButton = new RoundedButton(" \u276E ", 25, new Color(50, 98, 255), 20);
        setBackButton.addActionListener(e -> StepBack()); // Добавляем обработчик нажатия кнопки
        RoundedButton calculateButton = new RoundedButton("\u276F\u276F", 25, new Color(50, 98, 255), 20);
        calculateButton.addActionListener(e -> calculateMST()); // Добавляем обработчик нажатия кнопки

        stepButtonPanel.add(Box.createVerticalStrut(30));
        stepButtonPanel.add(setStepButton);
        stepButtonPanel.add(Box.createVerticalStrut(30));
        stepButtonPanel.add(setBackButton);
        stepButtonPanel.add(Box.createVerticalStrut(30));
        stepButtonPanel.add(calculateButton);

        gbc.gridheight = 1;
        gbc.gridy = 4;
        gbc.gridx = 5;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        add(stepButtonPanel, gbc);

        JPanel graphButtonPanel = new JPanel();

        RoundedButton drawGraphButton = new RoundedButton("Draw Graph", 20, new Color(50, 98, 255), 14);
        drawGraphButton.addActionListener(e -> drawGraph()); // Добавляем обработчик нажатия кнопки
        graphButtonPanel.add(drawGraphButton);

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 6;

        add(graphButtonPanel, gbc);

        // Обработчик нажатия кнопки установки размера
        setSizeButton.addActionListener(e -> {
            try {
                size = Integer.parseInt(sizeField.getText()); // Преобразуем текст из поля ввода в целое число
                currentStep = 0; // обнулить текущий шаг
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
    }

    // Метод для создания матрицы заданного размера
    private void createMatrix(int size) {
        matrixPanel.removeAll(); // Удаляем все компоненты с панели матрицы
        matrixPanel.setLayout(new GridLayout(size, size)); // Устанавливаем компоновщик GridLayout для панели матрицы
        matrixFields = new RoundedTextField[size][size]; // Инициализируем массив полей ввода

        Dimension cellSize = new Dimension(25, 25); // Размер ячейки
        Dimension panelSize = new Dimension(size * cellSize.width, size * cellSize.height); // Размер панели в зависимости от количества ячеек
        matrixPanel.setPreferredSize(panelSize); // Устанавливаем предпочтительный размер панели

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrixFields[i][j] = new RoundedTextField(3, 5); // Создаем новое поле ввода
                matrixFields[i][j].setHorizontalAlignment(JTextField.CENTER); // Центрируем текст в поле ввода
                matrixFields[i][j].setPreferredSize(cellSize); // Устанавливаем предпочтительный размер
                matrixPanel.add(matrixFields[i][j]); // Добавляем поле ввода на панель матрицы
            }
        }
        // Перекомпоновываем и перерисовываем компоненты
        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    // Метод для вывода результата
    private void calculateMST() {
        if (graphDraw == null){
            JOptionPane.showMessageDialog(this, "Please draw graph fist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Graphics g = graphPanel.getGraphics();

        // Создаем списки для ключей и значений
        for (int i = currentStep; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            graphDraw.drawEdge(g, edge.src, edge.dest, new Color(49, 168, 116), new Color(65, 199, 139));
            outputArea.setText(messages.get(i));
            currentStep++;
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
                    int num = Integer.parseInt(matrixFields[i][j].getText()); // Преобразуем текст из полей ввода в числа
                    if (num >= 0) {
                        matrix[i][j] = num;
                        matrix[j][i] = num;
                    } else {
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
        // Проверка валидности матрицы перед отрисовкой графа
         switch (MatrixValidation.checkMatrix(matrix)) {
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
        graphPanel.removeAll(); // Удаляем все компоненты с панели графа
        graphPanel.setLayout(new BorderLayout()); // Устанавливаем компоновщик BorderLayout для панели графа
        graphDraw = new GraphPanel(matrix, size);
        graphPanel.add(graphDraw); // Добавляем панель графа с новой матрицей
        graphPanel.revalidate(); // Перекомпоновываем компоненты
        graphPanel.repaint(); // Перерисовываем компоненты

        // Вычисляем МОД

        PrimAlgorithm prim = new PrimAlgorithm(matrix, size); // Создаем объект алгоритма Прима
        Map<Edge, String> mstMap = prim.primMST();

        // Создаем списки для ключей и значений
        edges = new ArrayList<>(mstMap.keySet());
        messages = new ArrayList<>(mstMap.values());
    }

    private void StepForward(){
        if (currentStep == edges.size()){
            return; // здесь просто ничего не делать или попросить нарисовать граф?
        }
        Graphics g = graphPanel.getGraphics();

        Edge edge = edges.get(currentStep);
        graphDraw.drawEdge(g, edge.src, edge.dest, new Color(49, 168, 116), new Color(65, 199, 139));
        outputArea.setText(messages.get(currentStep));
        currentStep++;
    }

    private void StepBack(){
        if (currentStep < 1){
            return; // здесь просто ничего не делать или попросить нарисовать граф?
        }
        Graphics g = graphPanel.getGraphics();
        currentStep--;
        Edge edge = edges.get(currentStep);
        graphDraw.drawEdge(g, edge.src, edge.dest, Color.BLACK,new Color(115, 64, 254));
        if (currentStep == 0){
            outputArea.setText("");
            return;
        }
        outputArea.setText(messages.get(currentStep-1));
    }
}
