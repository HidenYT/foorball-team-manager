package UI.view;

import Entities.Position;
import Helpers.ColumnIndexFinder;
import Helpers.QueryHelper;
import UI.ObjectCreationPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayersWorkingPanel extends AbstractWorkingPanel {
    private final ObjectCreationPanel objectCreationPanel;
    private final Component[] editors;
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_FIRST_NAME = "Имя";
    public static final String COLUMN_SECOND_NAME = "Фамилия";
    public static final String COLUMN_HEIGHT = "Рост";
    public static final String COLUMN_WEIGHT = "Вес";
    public static final String COLUMN_FOOT_SIZE = "Размер ноги";
    public static final String COLUMN_POSITION = "Позиция";
    public static final String COLUMN_NUMBER = "Номер";
    public static final String COLUMN_EFFICIENCY_INDEXES = "Показатели эффективности";
    public static final String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_NUMBER,
            COLUMN_FIRST_NAME,
            COLUMN_SECOND_NAME,
            COLUMN_HEIGHT,
            COLUMN_WEIGHT,
            COLUMN_FOOT_SIZE,
            COLUMN_POSITION,
            COLUMN_EFFICIENCY_INDEXES,
    };
    public static final String[] ADD_LABELS = {COLUMN_NUMBER, COLUMN_FIRST_NAME,
            COLUMN_SECOND_NAME, COLUMN_HEIGHT, COLUMN_WEIGHT, COLUMN_FOOT_SIZE, COLUMN_POSITION};;
    private final EntityManager entityManager;

    public PlayersWorkingPanel(Object[][] data, ObjectCreationPanel.CreateObjectClickListener clickListener, EntityManager entityManager) {
        super(COLUMNS, data);
        this.entityManager = entityManager;
        setTable(new JTable() {

            @Override
            public String getToolTipText(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int column = columnAtPoint(e.getPoint());
                Object value = getValueAt(row, column);
                return value == null ? null : value.toString();
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_ID)) return Long.class;
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_NUMBER) ||
                column == ColumnIndexFinder.find(COLUMNS, COLUMN_FOOT_SIZE)) return Integer.class;
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_WEIGHT)
                        || column == ColumnIndexFinder.find(COLUMNS, COLUMN_HEIGHT)) return Float.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != ColumnIndexFinder.find(COLUMNS, COLUMN_ID) && column != ColumnIndexFinder.find(COLUMNS, COLUMN_EFFICIENCY_INDEXES);
            }
        });
        setTableSorter();
        getTable().getColumnModel().getColumn(ColumnIndexFinder.find(COLUMNS, COLUMN_POSITION)).setCellEditor(new DefaultCellEditor(createPositionCellComboBox()));
        editors = new Component[] {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), createPositionCellComboBox() };
        objectCreationPanel = new ObjectCreationPanel(ADD_LABELS, editors, clickListener);
        add(objectCreationPanel, BorderLayout.SOUTH);
    }

    private JComboBox createPositionCellComboBox() {
        JComboBox positionCellComboBox = new JComboBox<>();
        List<Position> positions = QueryHelper.get(entityManager).getPositionsList();
        for(Position p : positions) {
            positionCellComboBox.addItem(p.getName());
        }
        return positionCellComboBox;
    }

    @Override
    public void clearInputInObjectCreationPanel() {
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NUMBER)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_FIRST_NAME)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_SECOND_NAME)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_HEIGHT)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_WEIGHT)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_FOOT_SIZE)]).setText("");
        ((JComboBox)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_POSITION)]).setSelectedIndex(0);
    }

    private void setTableSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(getTable().getModel());
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_ID),
                Comparator.comparingLong((Long o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_NUMBER),
                Comparator.comparingInt((Integer o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_FIRST_NAME),
                (Comparator<String>) String::compareTo);
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_SECOND_NAME),
                (Comparator<String>) String::compareTo);
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_HEIGHT),
                (Comparator<Float>) (o1, o2) -> Math.round(o1-o2));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_WEIGHT),
                (Comparator<Float>) (o1, o2) -> Math.round(o1-o2));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_FOOT_SIZE),
                Comparator.comparingInt((Integer o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_POSITION),
                (Comparator<String>) String::compareTo);

        getTable().setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sorter.setSortKeys(sortKeys);
        for(int i = 0; i < COLUMNS.length; i++) {
            if(i==ColumnIndexFinder.find(COLUMNS, COLUMN_EFFICIENCY_INDEXES)) continue;
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
    }
}
