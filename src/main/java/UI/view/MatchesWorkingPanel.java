package UI.view;

import Helpers.ColumnIndexFinder;
import Helpers.DateTransformer;
import UI.ObjectCreationPanel;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MatchesWorkingPanel extends AbstractWorkingPanel {
    public interface FilterMatchesClickListener {
        void onFollowingMatchesClicked();
        void onPassedMatchesClicked();
        void onAllMatchesClicked();
    }
    public static final String COLUMN_RIVAL = "Соперник";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_DATE = "Дата";
    public static final String COLUMN_SCORED_GOALS_COUNT = "Число забитых мячей";
    public static final String COLUMN_MISSED_GOALS_COUNT = "Число пропущенных мячей";
    public static final String COLUMN_GOALS = "Забитые мячи";
    public static final String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_DATE,
            COLUMN_RIVAL,
            COLUMN_SCORED_GOALS_COUNT,
            COLUMN_MISSED_GOALS_COUNT,
            COLUMN_GOALS,
    };
    public static final String[] ADD_LABELS = new String[] {COLUMN_DATE, COLUMN_RIVAL, COLUMN_MISSED_GOALS_COUNT};
    private final ObjectCreationPanel objectCreationPanel;
    private final Component[] editors;
    private final JButton allMatchesButton;
    private final JButton passedMatchesButton;
    private final JButton followingMatchesButton;
    private final JPanel topPanel;

    public MatchesWorkingPanel(Object[][] data,
                               ObjectCreationPanel.CreateObjectClickListener createObjectClickListener,
                               FilterMatchesClickListener filterMatchesClickListener,
                               int passedMatchesCount,
                               int followingMatchesCount) {
        super(COLUMNS, data);
        editors = new Component[] {new JTextField(), new JTextField(), new JTextField()};
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
                if(column == ColumnIndexFinder.find(COLUMNS, COLUMN_MISSED_GOALS_COUNT)
                        || column == ColumnIndexFinder.find(COLUMNS, COLUMN_SCORED_GOALS_COUNT)) return Integer.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != ColumnIndexFinder.find(COLUMNS, COLUMN_ID) &&
                        column != ColumnIndexFinder.find(COLUMNS, COLUMN_GOALS) &&
                        column != ColumnIndexFinder.find(COLUMNS, COLUMN_SCORED_GOALS_COUNT);
            }
        });
        setTableSorter();
        objectCreationPanel = new ObjectCreationPanel(ADD_LABELS, editors, createObjectClickListener);
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 3));
        allMatchesButton = new JButton("Все матчи (" + (followingMatchesCount+passedMatchesCount) +")");
        passedMatchesButton = new JButton("Прошедшие матчи (" + passedMatchesCount + ")");
        followingMatchesButton = new JButton("Будущие матчи (" + followingMatchesCount + ")");
        allMatchesButton.addActionListener(e->filterMatchesClickListener.onAllMatchesClicked());
        passedMatchesButton.addActionListener(e->filterMatchesClickListener.onPassedMatchesClicked());
        followingMatchesButton.addActionListener(e->filterMatchesClickListener.onFollowingMatchesClicked());
        topPanel.add(allMatchesButton);
        topPanel.add(passedMatchesButton);
        topPanel.add(followingMatchesButton);
        add(topPanel, BorderLayout.NORTH);
        add(objectCreationPanel, BorderLayout.SOUTH);
    }

    @Override
    public void clearInputInObjectCreationPanel() {
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_DATE)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_RIVAL)]).setText("");
        ((JTextField)editors[ColumnIndexFinder.find(ADD_LABELS, COLUMN_MISSED_GOALS_COUNT)]).setText("");
    }

    public void updateButtonsText(int passed, int following) {
        allMatchesButton.setText("Все матчи (" + (following+passed) +")");
        passedMatchesButton.setText("Прошедшие матчи (" + passed + ")");
        followingMatchesButton.setText("Будущие матчи (" + following + ")");
    }

    private void setTableSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(getTable().getModel());
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_ID),
                Comparator.comparingLong((Long o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_DATE),
                Comparator.comparing(DateTransformer::DBDateFromNormalDate));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_RIVAL),
                (Comparator<String>) String::compareTo);
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_MISSED_GOALS_COUNT),
                Comparator.comparingInt((Integer o) -> o));
        sorter.setComparator(ColumnIndexFinder.find(COLUMNS, COLUMN_SCORED_GOALS_COUNT),
                Comparator.comparingInt((Integer o) -> o));
        getTable().setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sorter.setSortKeys(sortKeys);
        for(int i = 0; i < COLUMNS.length; i++) {
            if(i==ColumnIndexFinder.find(COLUMNS, COLUMN_GOALS)) continue;
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
    }
}
