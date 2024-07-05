import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class MyDocumentListener implements DocumentListener {
    private final JTextField sourceField;
    private final JTextField targetField;
    static boolean isMethodRunning = false;

    public MyDocumentListener(JTextField sourceField, JTextField targetField) {
        this.sourceField = sourceField;
        this.targetField = targetField;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if(isMethodRunning) {return;}
        updateTarget(sourceField.getText());
        //System.out.println("insertUpdate");
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if(isMethodRunning) {return;}
        updateTarget(sourceField.getText());
        //System.out.println("removeUpdate");
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if(isMethodRunning) {return;}
        updateTarget(sourceField.getText());
        //System.out.println("changedUpdate");

    }

    private void updateTarget(String text) {
        if (!isMethodRunning && targetField != null && !text.equals(targetField.getText())) {
            isMethodRunning = true;
            //SwingUtilities.invokeLater(() -> targetField.setText(text));
            targetField.setText(text);
            isMethodRunning = false;



        }
    }
}