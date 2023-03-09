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

public class EfficiencyIndexNamesWorkingPanel extends AbstractWorkingPanel {
    public static String COLUMN_NAME = "Название";
    public static String COLUMN_ID = "ID";
    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, };
    public static final String[] ADD_LABELS = {COLUMN_NAME};

    private final Component[] editors;
    private final ObjectCreationPanel objectCreationPanel;
    public EfficiencyIndexNamesWorkingPanel(Object[][] data, ObjectCreationPanel.CreateObjectClickListener clickListener) {
        super(COLUMNS, data);
        editors = new Component[]{new JTextField()};
        setTable(new JTable(){

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

    @Override
    public void clearInputInObjectCreationPanel() {
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NAME)]).setText("");
    }

    private void setTableSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(getTable().getModel());
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_ID),
                Comparator.comparingLong((Long o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_NAME), (Comparator<String>) String::compareTo);
        getTable().setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sorter.setSortKeys(sortKeys);
        for(int i = 0; i < COLUMNS.length; i++) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
    }
}
