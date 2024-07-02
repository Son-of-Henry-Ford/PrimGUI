import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;


public class RoundedButton extends JButton {
    private int cornerRadius;
    private Color buttonColor;
    //private Color textColor;

    public RoundedButton(String text, int radius, Color buttonColor, int size) {
        super(text);
        this.cornerRadius = radius;
        this.buttonColor = buttonColor;
        //this.textColor = textColor;
        setContentAreaFilled(false); // Отключаем стандартное заполнение кнопки
        setFocusPainted(false); // Убираем рамку фокуса
        setBorderPainted(false); // Убираем стандартную границу
        setFont(new Font("", Font.BOLD, size)); // Устанавливаем размер текста
    }

    public RoundedButton(ImageIcon icon, int radius, Color color) {
        super(icon);
        this.cornerRadius = radius;
        this.buttonColor = color;
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем закругленную кнопку
        if (getModel().isArmed()) {
            g2.setColor(buttonColor.darker());
        } else {
            g2.setColor(buttonColor);
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Рисуем текст кнопки
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D textRect = fm.getStringBounds(getText(), g2);
        int textX = (int) ((getWidth() - textRect.getWidth()) / 2);
        int textY = (int) ((getHeight() - textRect.getHeight()) / 2 + fm.getAscent());
        //g2.setColor(textColor);
        g2.drawString(getText(), textX, textY);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Оставляем пустым, чтобы не рисовать стандартную границу
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = Math.max(size.width, size.height);
        return size;
    }
}
