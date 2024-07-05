import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextArea extends JTextArea {
    private int cornerRadius;

    public RoundedTextArea(int rows, int columns, int cornerRadius) {
        super(rows, columns);
        this.cornerRadius = cornerRadius;
        setOpaque(false); // Сделать компонент прозрачным для отображения закругленных углов
        setMargin(new Insets(20, 20, 20, 20)); // Устанавливаем отступы в 10 пикселей со всех сторон
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Установить цвет фона
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(10, 10, getWidth()-20, getHeight()-20, cornerRadius, cornerRadius));

        // Нарисовать текст
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Установить цвет границы
        g2.setColor(getBackground());
        g2.draw(new RoundRectangle2D.Float(10, 10, getWidth()-20, getHeight()-20, cornerRadius, cornerRadius));

        g2.dispose();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        //setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}