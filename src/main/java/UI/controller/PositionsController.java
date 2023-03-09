package UI.controller;

import Entities.EfficiencyIndex;
import Entities.Goal;
import Entities.Player;
import Entities.Position;
import Exceptions.EmptyFieldException;
import Exceptions.FieldMustHaveUniqueValueException;
import Helpers.ColumnIndexFinder;
import Helpers.InputValidationHelper;
import Helpers.QueryHelper;
import UI.ObjectCreationPanel;
import UI.view.PositionsWorkingPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static UI.view.PositionsWorkingPanel.*;

public class PositionsController extends AbstractTableController implements ObjectCreationPanel.CreateObjectClickListener {
    private final PositionsWorkingPanel workingPanel;
    public PositionsController(JFrame parentFrame, EntityManager entityManager) {
        super(parentFrame, entityManager);
        workingPanel = new PositionsWorkingPanel(getDataFromDB(), this);
    }

    @Override
    public void saveTable() {
        getEntityManager().getTransaction().begin();
        try {
            validateNamesUnique();
            for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
                InputValidationHelper.validateName((String)
                        workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NAME)));
                Position p = getEntityManager().find(Position.class, workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID)));
                p.setName((String) workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NAME)));
                p.setDescription((String)workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_DESCRIPTION)));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }

    @Override
    public void deleteObjects() {
        getEntityManager().getTransaction().begin();
        int i = 0;
        while(i < workingPanel.getModel().getRowCount()) {
            if(workingPanel.getTable().isRowSelected(i)) {
                Position position = getEntityManager().find(Position.class,
                        workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID)));
                for(Player player : position.getPlayers()) {
                    for(Goal goal : player.getGoals()) {
                        goal.getMatch().getScoredGoals().remove(goal);
                    }
                    for(EfficiencyIndex efficiencyIndex : player.getEfficiencyIndexes()) {
                        efficiencyIndex.getName().getEfficiencyIndexes().remove(efficiencyIndex);
                    }
                    getEntityManager().remove(player);
                }
                getEntityManager().remove(position);
            }
            i++;
        }
        getEntityManager().getTransaction().commit();
        workingPanel.setTableContent(getDataFromDB());
    }

    @Override
    public JPanel getPanel() {
        return workingPanel;
    }

    @Override
    public boolean isDeletionSafe() {
        for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
            if(!workingPanel.getTable().isRowSelected(i)) continue;
            Position position = getEntityManager().find(Position.class,
                    workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID)));
            if(position.getPlayers() != null && position.getPlayers().size() > 0) {
                return false;
            }
        }
        return true;
    }

    private Object[][] getDataFromDB() {
        List<Position> positions = QueryHelper.get(getEntityManager()).getPositionsList();
        Object[][] data = new Object[positions.size()][COLUMNS.length];
        for(int i = 0; i < positions.size(); i++) {
            data[i] = createRow(positions.get(i));
        }
        return data;
    }

    @Override
    public void onClick(Component[] values) {
        getEntityManager().getTransaction().begin();
        try {
            String positionName = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NAME)]).getText();
            String description = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_DESCRIPTION)]).getText();
            if(positionName == null || positionName.equals("")) throw new EmptyFieldException(COLUMN_NAME);
            InputValidationHelper.validateName(positionName);
            validateNameUnique(positionName);


            Position position = new Position();
            position.setName(positionName);
            position.setDescription(description);
            getEntityManager().persist(position);
            workingPanel.getModel().addRow(createRow(position));
            workingPanel.clearInputInObjectCreationPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }

    private Object[] createRow(Position position) {
        Object[] result = new Object[COLUMNS.length];
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_ID)] = position.getId();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_NAME)] = position.getName();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_DESCRIPTION)] = position.getDescription();
        return result;
    }

    private void validateNameUnique(String name) throws FieldMustHaveUniqueValueException {
        List<Position> positions = QueryHelper.get(getEntityManager()).getPositionsList("name='"+name+"'");
        if(positions.size() != 0) throw new FieldMustHaveUniqueValueException(COLUMN_NAME, name);
    }

    private void validateNamesUnique() throws FieldMustHaveUniqueValueException {
        String[] positions = new String[workingPanel.getModel().getRowCount()];
        for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
            positions[i] = (String)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NAME));
        }
        Arrays.sort(positions);
        for(int i = 1; i < positions.length; i++) {
            if(positions[i].equals(positions[i-1])) {
                throw new FieldMustHaveUniqueValueException(COLUMN_NAME, positions[i]);
            }
        }
    }
}
