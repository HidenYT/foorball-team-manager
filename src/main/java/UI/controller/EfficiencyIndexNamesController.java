package UI.controller;

import Entities.EfficiencyIndex;
import Entities.EfficiencyIndexName;
import Exceptions.EmptyFieldException;
import Exceptions.FieldMustHaveUniqueValueException;
import Helpers.ColumnIndexFinder;
import Helpers.InputValidationHelper;
import Helpers.QueryHelper;
import UI.ObjectCreationPanel;
import UI.view.EfficiencyIndexNamesWorkingPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static UI.view.EfficiencyIndexNamesWorkingPanel.*;

public class EfficiencyIndexNamesController extends AbstractTableController implements ObjectCreationPanel.CreateObjectClickListener {
    private final EfficiencyIndexNamesWorkingPanel workingPanel;
    public EfficiencyIndexNamesController(JFrame parentFrame, EntityManager entityManager) {
        super(parentFrame, entityManager);
        workingPanel = new EfficiencyIndexNamesWorkingPanel(getData(), this);
    }

    private Object[][] getData() {
        List<EfficiencyIndexName> efficiencyIndexNameList = QueryHelper.get(getEntityManager()).getEfficiencyIndexesNamesList();
        Object[][] result = new Object[efficiencyIndexNameList.size()][COLUMNS.length];
        for(int i = 0; i < efficiencyIndexNameList.size(); i++) {
            EfficiencyIndexName name = efficiencyIndexNameList.get(i);
            result[i] = createRow(name);
        }
        return result;
    }

    @Override
    public void saveTable() {
        getEntityManager().getTransaction().begin();
        try {
            validateNamesUnique();
            for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
                String name = (String)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NAME));
                InputValidationHelper.validateName(name);
                long id = (long) workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
                EfficiencyIndexName efficiencyIndexName = getEntityManager().find(EfficiencyIndexName.class, id);
                efficiencyIndexName.setName(name);
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
                EfficiencyIndexName efficiencyIndexName = getEntityManager().find(EfficiencyIndexName.class, id);
                for(EfficiencyIndex efficiencyIndex : efficiencyIndexName.getEfficiencyIndexes()) {
                    efficiencyIndex.getPlayer().getEfficiencyIndexes().remove(efficiencyIndex);
                    efficiencyIndex.setPlayer(null);
                }
                getEntityManager().remove(efficiencyIndexName);
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
        for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
            if(!workingPanel.getTable().isRowSelected(i)) continue;
            long id = (long)workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
            EfficiencyIndexName efficiencyIndexName = getEntityManager().find(EfficiencyIndexName.class, id);
            if(efficiencyIndexName.getEfficiencyIndexes() != null && efficiencyIndexName.getEfficiencyIndexes().size() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(Component[] values) {
        getEntityManager().getTransaction().begin();
        try {
            String name = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NAME)]).getText();
            if(name==null || name.equals("")) throw new EmptyFieldException(COLUMN_NAME);
            InputValidationHelper.validateName(name);
            validateNameUnique(name);

            EfficiencyIndexName efficiencyIndexName = new EfficiencyIndexName();
            efficiencyIndexName.setName(name);
            getEntityManager().persist(efficiencyIndexName);
            workingPanel.addRow(createRow(efficiencyIndexName));
            workingPanel.clearInputInObjectCreationPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }

    private Object[] createRow(EfficiencyIndexName efficiencyIndexName) {
        Object[] result = new Object[COLUMNS.length];
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_NAME)] = efficiencyIndexName.getName();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_ID)] = efficiencyIndexName.getId();
        return result;
    }

    private void validateNameUnique(String name) throws FieldMustHaveUniqueValueException {
        List<EfficiencyIndexName> names = QueryHelper.get(getEntityManager()).getEfficiencyIndexesNamesList("name='"+name+"'");
        if(names.size() != 0) throw new FieldMustHaveUniqueValueException(COLUMN_NAME, name);
    }

    private void validateNamesUnique() throws FieldMustHaveUniqueValueException {
        String[] names = new String[workingPanel.getModel().getRowCount()];
        for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
            names[i] = (String)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NAME));
        }
        Arrays.sort(names);
        for(int i = 1; i < names.length; i++) {
            if(names[i].equals(names[i-1])) {
                throw new FieldMustHaveUniqueValueException(COLUMN_NAME, names[i]);
            }
        }
    }
}
