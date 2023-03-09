package UI.view;

import Entities.EfficiencyIndexName;
import Entities.Player;
import Helpers.ColumnIndexFinder;
import Helpers.QueryHelper;
import UI.ComboboxItem.ComboBoxEINameItem;
import UI.ComboboxItem.ComboBoxPlayerItem;
import UI.ObjectCreationPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EfficiencyIndexesWorkingPanel extends AbstractWorkingPanel {
    public static final String COLUMN_NAME = "Название";
    public static final String COLUMN_VALUE = "Значение";
    public static final String COLUMN_PLAYER = "Игрок";
    public static final String COLUMN_ID = "ID";
    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_PLAYER, COLUMN_NAME, COLUMN_VALUE};

    public static final String[] ADD_LABELS = {COLUMN_PLAYER, COLUMN_NAME, COLUMN_VALUE};
    private final Component[] editors;
    private final EntityManager entityManager;
    public EfficiencyIndexesWorkingPanel(Object[][] data,
                                         ObjectCreationPanel.CreateObjectClickListener clickListener,
                                         EntityManager entityManager) {
        super(COLUMNS, data);
        editors = new Component[] {createPlayersComboBox(), createNamesComboBox(), new JTextField()};
        setTable(new JTable(){
            @Override
            public Class<?> getColumnClass(int column) {
                if(ColumnIndexFinder.find(COLUMNS, COLUMN_ID) == column) return Long.class;
                if(ColumnIndexFinder.find(COLUMNS, COLUMN_VALUE) == column) return Float.class;
                if(ColumnIndexFinder.find(COLUMNS, COLUMN_NAME) == column) return ComboBoxEINameItem.class;
                if(ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER) == column) return ComboBoxPlayerItem.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != ColumnIndexFinder.find(COLUMNS, COLUMN_ID);
            }
        });
        this.entityManager = entityManager;
        setTableSorter();
        getTable().getColumnModel().getColumn(ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER)).setCellEditor(new DefaultCellEditor(createPlayersComboBox()));
        getTable().getColumnModel().getColumn(ColumnIndexFinder.find(COLUMNS, COLUMN_NAME)).setCellEditor(new DefaultCellEditor(createNamesComboBox()));
        ObjectCreationPanel objectCreationPanel = new ObjectCreationPanel(ADD_LABELS, editors, clickListener);
        add(objectCreationPanel, BorderLayout.SOUTH);
    }

    @Override
    public void clearInputInObjectCreationPanel() {
        ((JComboBox)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_PLAYER)]).setSelectedIndex(0);
        ((JComboBox)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NAME)]).setSelectedIndex(0);
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_VALUE)]).setText("");
    }

    private JComboBox<ComboBoxEINameItem> createNamesComboBox() {
        JComboBox<ComboBoxEINameItem> result = new JComboBox<>();
        java.util.List<EfficiencyIndexName> efficiencyIndexNames = QueryHelper.get(entityManager).getEfficiencyIndexesNamesList();
        for(EfficiencyIndexName name : efficiencyIndexNames) {
            ComboBoxEINameItem ni = new ComboBoxEINameItem(name);
            result.addItem(ni);
        }
        return result;
    }

    private JComboBox<ComboBoxPlayerItem> createPlayersComboBox() {
        JComboBox<ComboBoxPlayerItem> result = new JComboBox<>();
        List<Player> playerList = QueryHelper.get(entityManager).getPlayersList();
        for(Player player : playerList) {
            ComboBoxPlayerItem pi = new ComboBoxPlayerItem(player);
            result.addItem(pi);
        }
        return result;
    }

    private void setTableSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(getTable().getModel());
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_ID),
                Comparator.comparingLong((Long o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_VALUE),
                (Comparator<Float>) (o1, o2) -> Math.round(o1 - o2));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_NAME),
                Comparator.comparing((ComboBoxEINameItem o) -> o.getName().getName()));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER),
                Comparator.comparing((ComboBoxPlayerItem o) -> o.getPlayer().getNumber()));

        getTable().setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sorter.setSortKeys(sortKeys);
        for(int i = 0; i < COLUMNS.length; i++) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
    }
}
