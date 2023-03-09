package UI.view;

import Entities.Match;
import Entities.Player;
import Helpers.ColumnIndexFinder;
import Helpers.QueryHelper;
import UI.ComboboxItem.ComboBoxMatchItem;
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

public class GoalsWorkingPanel extends AbstractWorkingPanel {
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_PLAYER = "Игрок";
    public static final String COLUMN_MATCH = "Матч";
    public static final String COLUMN_TIME = "Время";
    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_MATCH, COLUMN_PLAYER, COLUMN_TIME};
    public static final String[] ADD_LABELS = {COLUMN_MATCH, COLUMN_PLAYER, COLUMN_TIME};

    private final Component[] editors;
    private final EntityManager entityManager;
    public GoalsWorkingPanel(Object[][] data,
                             ObjectCreationPanel.CreateObjectClickListener clickListener,
                             EntityManager entityManager) {
        super(COLUMNS, data);
        setTable(new JTable(){
            @Override
            public Class<?> getColumnClass(int column) {
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_ID)) return Long.class;
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_MATCH)) return ComboBoxMatchItem.class;
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER)) return ComboBoxPlayerItem.class;
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_TIME)) return String.class;
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
        getTable().getColumnModel().getColumn(ColumnIndexFinder.find(COLUMNS, COLUMN_MATCH)).setCellEditor(new DefaultCellEditor(createMatchesComboBox()));
        editors = new Component[] {createMatchesComboBox(), createPlayersComboBox(), new JTextField()};
        ObjectCreationPanel objectCreationPanel = new ObjectCreationPanel(ADD_LABELS, editors, clickListener);
        add(objectCreationPanel, BorderLayout.SOUTH);
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

    private JComboBox<ComboBoxMatchItem> createMatchesComboBox() {
        JComboBox<ComboBoxMatchItem> result = new JComboBox<>();
        List<Match> matchList = QueryHelper.get(entityManager).getMatchesList();
        for(Match match : matchList) {
            ComboBoxMatchItem mi = new ComboBoxMatchItem(match);
            result.addItem(mi);
        }
        return result;
    }

    @Override
    public void clearInputInObjectCreationPanel() {
        ((JComboBox)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_PLAYER)]).setSelectedIndex(0);
        ((JComboBox)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_MATCH)]).setSelectedIndex(0);
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_TIME)]).setText("");
    }

    private void setTableSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(getTable().getModel());
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_ID),
                Comparator.comparingLong((Long o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_TIME),
                (Comparator<String>) String::compareTo);
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER),
                Comparator.comparingInt((ComboBoxPlayerItem o) -> o.getPlayer().getNumber()));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_MATCH),
                Comparator.comparing((ComboBoxMatchItem o) -> o.getMatch().getDate()));
        getTable().setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sorter.setSortKeys(sortKeys);
        for(int i = 0; i < COLUMNS.length; i++) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
    }
}
