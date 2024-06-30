import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private int cornerRadius;

    public RoundedTextField(int columns, int cornerRadius) {
        super(columns);
        this.cornerRadius = cornerRadius;
        setOpaque(false); // Сделать компонент прозрачным для отображения закругленных углов
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Установить цвет фона
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-4, getHeight()-4, cornerRadius, cornerRadius));

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
        g2.draw(new RoundRectangle2D.Float(2, 2, getWidth()-4, getHeight()-4, cornerRadius, cornerRadius));

        g2.dispose();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
}
