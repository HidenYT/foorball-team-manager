package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ObjectCreationPanel extends JPanel {
    public interface CreateObjectClickListener {
        void onClick(Component[] values);
    }

    public ObjectCreationPanel(String[] fieldNames, Component[] editors, CreateObjectClickListener clickListener) {
        setLayout(new GridLayout(1, fieldNames.length+1));
        for(int i = 0; i < fieldNames.length; i++) {
            JPanel inputField = new JPanel();
            inputField.setLayout(new BoxLayout(inputField, BoxLayout.Y_AXIS));
            JLabel label = new JLabel(fieldNames[i]);
            label.setAlignmentX(0.5f);
            inputField.add(label);
            inputField.add(editors[i]);
            add(inputField);
        }
        JButton creationButton = new JButton("Добавить");
        creationButton.addActionListener(e -> clickListener.onClick(editors));
        add(creationButton);
    }
}
