import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AdjMatrixFromFilePanel extends JPanel {
    private int[][] matrix;
    private RoundedTextArea outputArea; // Область для вывода результата выполнения алгоритма Прима
    private RoundedPanel graphPanel; // Панель для отображения графа
    private int size; // Размер матрицы
    private GraphPanel graphDraw;
    private java.util.List<Edge> edges = new ArrayList<>(); // список ребер мод
    private List<String> messages = new ArrayList<>(); // список сообщений мод
    private int currentStep = 0; // текущий шаг мод


    public AdjMatrixFromFilePanel() {
        setLayout(new GridBagLayout()); // Устанавливаем компоновщик GridBagLayout для главной панели
        GridBagConstraints gbc = new GridBagConstraints();

        // Панель для кнопки загрузки
        JPanel loadPanel = new JPanel();
        RoundedButton loadButton = new RoundedButton("Load Matrix from File", 25, new Color(50, 98, 255), 14);
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
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.CENTER;
        add(loadPanel, gbc);

        // Область для вывода результата
        outputArea = new RoundedTextArea(10, 35, 25);
        outputArea.setEditable(false); // Запрещаем редактирование области вывода
        JScrollPane scrollPane = new JScrollPane(outputArea); // Добавляем область вывода в прокручиваемую панель
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Убираем черную рамку вокруг области
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc); // Добавляем прокручиваемую панель на восток главной панели

        // Панель для отображения графа
        graphPanel = new RoundedPanel(new FlowLayout(), 25, Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
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
        gbc.gridy = 2;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        add(stepButtonPanel, gbc);

        JPanel graphButtonPanel = new JPanel();

        RoundedButton drawGraphButton = new RoundedButton("Draw Graph", 20, new Color(50, 98, 255), 14);
        drawGraphButton.addActionListener(e -> drawGraph()); // Добавляем обработчик нажатия кнопки
        graphButtonPanel.add(drawGraphButton);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 4;

        add(graphButtonPanel, gbc);
    }

    // Метод для вычисления MST и вывода результата
    private void calculateMST() {
        if (graphDraw == null){
            JOptionPane.showMessageDialog(this, "Please draw graph fist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Graphics g = graphPanel.getGraphics();

        // Создаем списки для ключей и значений
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            graphDraw.drawEdge(g, edge.src, edge.dest, new Color(49, 168, 116), new Color(65, 199, 139));
            outputArea.setText(messages.get(i));
            currentStep++;
        }
    }

    // Метод для рисования графа по матрице смежности
    private void drawGraph() {
        if (matrix != null && matrix.length > 0) {
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
            } catch (NumberFormatException e) {
                matrix = null;
                JOptionPane.showMessageDialog(this, "Please fill all matrix fields with valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                // return; // вроде не требуется
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void StepForward(){
        if (currentStep == edges.size()){
            return; // здесь просто ничего не делать или попросить нарисовать граф?
        }
        Graphics g = graphPanel.getGraphics();
        Edge edge = edges.get(currentStep);
        graphDraw.drawEdge(g, edge.src, edge.dest, new Color(65, 199, 139), new Color(65, 199, 139));
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
        graphDraw.drawEdge(g, edge.src, edge.dest, new Color(65, 199, 139), new Color(115, 64, 254));
        if (currentStep == 0){
            outputArea.setText("");
            return;
        }
        outputArea.setText(messages.get(currentStep-1));
    }
}
