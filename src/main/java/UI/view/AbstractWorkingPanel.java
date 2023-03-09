package UI.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class AbstractWorkingPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    public AbstractWorkingPanel(String[] headers, Object[][] data) {
        setLayout(new BorderLayout());
        table = new JTable();
        model = new DefaultTableModel(data, headers);
        table.setModel(model);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTableContent(Object[][] content) {
        while(model.getRowCount() > 0) model.removeRow(0);
        for (Object[] objects : content) {
            model.addRow(objects);
        }
    }

    public void setRowContent(int row, Object[] content) {
        for(int i = 0; i < content.length; i++) {
            table.setValueAt(content[i], row, i);
        }
    }

    public void addRow(Object[] content) {
        model.addRow(content);
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
        this.table.setModel(model);
        remove(scrollPane);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
        table.setModel(model);
    }

    public DefaultTableModel getModel() {
        return model;
    }

    abstract void clearInputInObjectCreationPanel();

}
