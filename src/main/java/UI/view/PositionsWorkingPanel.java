package UI.view;

import Helpers.ColumnIndexFinder;
import UI.ObjectCreationPanel;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PositionsWorkingPanel extends AbstractWorkingPanel {
    public static final String COLUMN_NAME = "Название";
    public static final String COLUMN_DESCRIPTION = "Описание";
    public static final String COLUMN_ID = "ID";
    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION};
    public static final String[] ADD_LABELS = new String[]{COLUMN_NAME, COLUMN_DESCRIPTION};
    private ObjectCreationPanel objectCreationPanel;

    private final Component[] editors;
    public PositionsWorkingPanel(Object[][] data, ObjectCreationPanel.CreateObjectClickListener clickListener) {
        super(COLUMNS, data);
        editors = new Component[] {new JTextField(), new JTextField()};
        setTable(new JTable() {
            @Override
            public Class<?> getColumnClass(int column) {
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_ID)) return Long.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != ColumnIndexFinder.find(COLUMNS, COLUMN_ID);
            }
        });
        setTableSorter();
        objectCreationPanel = new ObjectCreationPanel(ADD_LABELS, editors, clickListener);
        add(objectCreationPanel, BorderLayout.SOUTH);
    }

    public ObjectCreationPanel getObjectCreationPanel() {
        return objectCreationPanel;
    }

    public void setObjectCreationPanel(ObjectCreationPanel objectCreationPanel) {
        this.objectCreationPanel = objectCreationPanel;
    }

    @Override
    public void clearInputInObjectCreationPanel() {
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NAME)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_DESCRIPTION)]).setText("");
    }

    private void setTableSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(getTable().getModel());
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_ID),
                Comparator.comparingLong((Long o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_NAME),
                (Comparator<String>) String::compareTo);


        getTable().setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sorter.setSortKeys(sortKeys);
        for(int i = 0; i < COLUMNS.length; i++) {
            if(i==ColumnIndexFinder.find(COLUMNS, COLUMN_DESCRIPTION)) continue;
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
    }
}
