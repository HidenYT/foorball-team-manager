package UI.controller;

import Entities.EfficiencyIndex;
import Exceptions.EmptyFieldException;
import Helpers.ColumnIndexFinder;
import Helpers.InputValidationHelper;
import Helpers.QueryHelper;
import UI.ComboboxItem.ComboBoxEINameItem;
import UI.ComboboxItem.ComboBoxPlayerItem;
import UI.ObjectCreationPanel;
import UI.view.EfficiencyIndexesWorkingPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import static UI.view.EfficiencyIndexesWorkingPanel.*;

public class EfficiencyIndexController extends AbstractTableController implements ObjectCreationPanel.CreateObjectClickListener {
    private final EfficiencyIndexesWorkingPanel workingPanel;
    public EfficiencyIndexController(JFrame parentFrame, EntityManager entityManager) {
        super(parentFrame, entityManager);
        workingPanel = new EfficiencyIndexesWorkingPanel(getData(), this, entityManager);
    }

    private Object[][] getData() {
        List<EfficiencyIndex> efficiencyIndexList = QueryHelper.get(getEntityManager()).getEfficiencyIndexesList();
        Object[][] result = new Object[efficiencyIndexList.size()][COLUMNS.length];
        for(int i = 0; i < efficiencyIndexList.size(); i++) {
            EfficiencyIndex index = efficiencyIndexList.get(i);
            result[i] = createRow(index);
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
                ComboBoxEINameItem eiNameItem = (ComboBoxEINameItem)
                        workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NAME));
                String value = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_VALUE)).toString();
                InputValidationHelper.validatePositiveDouble(value);

                long id = (long)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
                EfficiencyIndex efficiencyIndex = getEntityManager().find(EfficiencyIndex.class, id);

                efficiencyIndex.getPlayer().getEfficiencyIndexes().remove(efficiencyIndex);
                efficiencyIndex.setPlayer(playerItem.getPlayer());
                efficiencyIndex.getPlayer().getEfficiencyIndexes().add(efficiencyIndex);

                efficiencyIndex.getName().getEfficiencyIndexes().remove(efficiencyIndex);
                efficiencyIndex.setName(eiNameItem.getName());
                efficiencyIndex.getName().getEfficiencyIndexes().add(efficiencyIndex);

                efficiencyIndex.setValue(Float.parseFloat(value));
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
                long id = (Long) workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
                System.out.println(id);
                EfficiencyIndex index = getEntityManager().find(EfficiencyIndex.class, id);
                index.getPlayer().getEfficiencyIndexes().remove(index);
                index.getName().getEfficiencyIndexes().remove(index);
                getEntityManager().remove(index);
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

    @Override
    public void onClick(Component[] values) {
        getEntityManager().getTransaction().begin();
        try {
            JComboBox playerCB = ((JComboBox)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_PLAYER)]);
            JComboBox nameCB = ((JComboBox)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NAME)]);
            ComboBoxEINameItem eiNameItem = (ComboBoxEINameItem) nameCB.getSelectedItem();
            ComboBoxPlayerItem playerItem = (ComboBoxPlayerItem) playerCB.getSelectedItem();
            String value = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_VALUE)]).getText();
            if(eiNameItem == null) throw new EmptyFieldException(COLUMN_NAME);
            if(playerItem == null) throw new EmptyFieldException(COLUMN_PLAYER);
            if(value == null || value.equals("")) throw new EmptyFieldException(COLUMN_VALUE);
            InputValidationHelper.validatePositiveDouble(value);

            EfficiencyIndex efficiencyIndex = new EfficiencyIndex();

            efficiencyIndex.setPlayer(playerItem.getPlayer());
            efficiencyIndex.getPlayer().getEfficiencyIndexes().add(efficiencyIndex);

            efficiencyIndex.setName(eiNameItem.getName());
            efficiencyIndex.getName().getEfficiencyIndexes().add(efficiencyIndex);

            efficiencyIndex.setValue(Float.parseFloat(value));
            getEntityManager().persist(efficiencyIndex);
            workingPanel.addRow(createRow(efficiencyIndex));
            workingPanel.clearInputInObjectCreationPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }

    private Object[] createRow(EfficiencyIndex efficiencyIndex) {
        Object[] result = new Object[COLUMNS.length];
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_ID)] = efficiencyIndex.getId();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_NAME)] = new ComboBoxEINameItem(efficiencyIndex.getName());
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_PLAYER)] = new ComboBoxPlayerItem(efficiencyIndex.getPlayer());
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_VALUE)] = efficiencyIndex.getValue();
        return result;
    }
}
