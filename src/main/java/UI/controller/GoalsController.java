package UI.controller;

import Entities.Goal;
import Exceptions.EmptyFieldException;
import Helpers.ColumnIndexFinder;
import Helpers.InputValidationHelper;
import Helpers.QueryHelper;
import UI.ComboboxItem.ComboBoxMatchItem;
import UI.ComboboxItem.ComboBoxPlayerItem;
import UI.ObjectCreationPanel;
import UI.view.GoalsWorkingPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import static UI.view.GoalsWorkingPanel.*;

public class GoalsController extends AbstractTableController implements ObjectCreationPanel.CreateObjectClickListener {
    private final GoalsWorkingPanel workingPanel;
    public GoalsController(JFrame parentFrame, EntityManager entityManager) {
        super(parentFrame, entityManager);
        workingPanel = new GoalsWorkingPanel(getData(), this, entityManager);
    }

    private Object[][] getData() {
        List<Goal> goals = QueryHelper.get(getEntityManager()).getGoalsList();
        Object[][] result = new Object[goals.size()][COLUMNS.length];
        for(int i = 0; i < goals.size(); i++) {
            result[i] = createRow(goals.get(i));
        }
        return result;
    }

    @Override
    public void saveTable() {
        getEntityManager().getTransaction().begin();
        try {
            for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
                ComboBoxPlayerItem playerItem = (ComboBoxPlayerItem)
                        workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER));
                ComboBoxMatchItem matchItem = (ComboBoxMatchItem)
                        workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_MATCH));
                String time = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_TIME)).toString();
                InputValidationHelper.validateTime(time);

                long id = (long)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
                Goal goal = getEntityManager().find(Goal.class, id);

                goal.getPlayer().getGoals().remove(goal);
                goal.setPlayer(playerItem.getPlayer());
                goal.getPlayer().getGoals().add(goal);

                goal.getMatch().getScoredGoals().remove(goal);
                goal.setMatch(matchItem.getMatch());
                matchItem.getMatch().getScoredGoals().add(goal);

                goal.setTime(time);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }

    @Override
    public void deleteObjects() {
        int i = 0;
        getEntityManager().getTransaction().begin();
        while(i < workingPanel.getModel().getRowCount()) {
            if(workingPanel.getTable().isRowSelected(i)) {
                long id = (long)workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
                Goal goal = getEntityManager().find(Goal.class, id);
                goal.getPlayer().getGoals().remove(goal);
                goal.getMatch().getScoredGoals().remove(goal);
                getEntityManager().remove(goal);
            }
            i++;
        }
        getEntityManager().getTransaction().commit();
        workingPanel.setTableContent(getData());
    }

    @Override
    public JPanel getPanel() {
        return workingPanel;
    }

    @Override
    public boolean isDeletionSafe() {
        return true;
    }

    public Object[] createRow(Goal goal) {
        Object[] result = new Object[COLUMNS.length];
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_ID)] = goal.getId();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER)] = new ComboBoxPlayerItem(goal.getPlayer());
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_MATCH)] = new ComboBoxMatchItem(goal.getMatch());
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_TIME)] = goal.getTime();
        return result;
    }

    @Override
    public void onClick(Component[] values) {
        getEntityManager().getTransaction().begin();
        try {
            JComboBox playerComboBox = ((JComboBox) values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_PLAYER)]);
            JComboBox matchComboBox = ((JComboBox) values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_MATCH)]);
            ComboBoxMatchItem matchItem = (ComboBoxMatchItem) matchComboBox.getSelectedItem();
            ComboBoxPlayerItem playerItem = (ComboBoxPlayerItem) playerComboBox.getSelectedItem();
            String time = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_TIME)]).getText();
            if(matchItem == null) throw new EmptyFieldException(COLUMN_MATCH);
            if(playerItem == null) throw new EmptyFieldException(COLUMN_PLAYER);
            if(time==null || time.equals("")) throw new EmptyFieldException(COLUMN_TIME);
            InputValidationHelper.validateTime(time);

            Goal goal = new Goal();
            goal.setTime(time);
            goal.setMatch(matchItem.getMatch());
            goal.setPlayer(playerItem.getPlayer());
            goal.getPlayer().getGoals().add(goal);
            goal.getMatch().getScoredGoals().add(goal);
            getEntityManager().persist(goal);
            workingPanel.getModel().addRow(createRow(goal));
            workingPanel.clearInputInObjectCreationPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }
}
