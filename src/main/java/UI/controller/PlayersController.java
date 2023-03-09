package UI.controller;

import Entities.EfficiencyIndex;
import Entities.Goal;
import Entities.Player;
import Entities.Position;
import Exceptions.EmptyFieldException;
import Exceptions.FieldMustHaveUniqueValueException;
import Exceptions.StringIsNotPositiveIntException;
import Helpers.ColumnIndexFinder;
import Helpers.InputValidationHelper;
import Helpers.QueryHelper;
import UI.ObjectCreationPanel;
import UI.view.PlayersWorkingPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static UI.view.PlayersWorkingPanel.*;

public class PlayersController extends AbstractTableController implements ObjectCreationPanel.CreateObjectClickListener {
    private final PlayersWorkingPanel workingPanel;
    public PlayersController(JFrame parentFrame, EntityManager entityManager) {
        super(parentFrame, entityManager);
        workingPanel = new PlayersWorkingPanel(getDataFromDB(), this, entityManager);
    }

    private Object[][] getDataFromDB() {
        List<Player> players = QueryHelper.get(getEntityManager()).getPlayersList();
        Object[][] result = new Object[players.size()][COLUMNS.length];
        for(int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            result[i] = createRow(p);
        }
        return result;
    }

    @Override
    public void saveTable() {
        getEntityManager().getTransaction().begin();
        try {
            validateNumbersUnique();
            for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
                String firstName = (String)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_FIRST_NAME));
                String secondName = (String)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_SECOND_NAME));
                String heightString = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_HEIGHT)).toString();
                String weightString = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_WEIGHT)).toString();
                String footSizeString = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_FOOT_SIZE)).toString();
                String numberString = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NUMBER)).toString();
                String position = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_POSITION)).toString();

                InputValidationHelper.validatePositiveInteger(numberString);
                InputValidationHelper.validateName(firstName);
                InputValidationHelper.validateName(secondName);
                InputValidationHelper.validatePositiveDouble(heightString);
                InputValidationHelper.validatePositiveDouble(weightString);
                InputValidationHelper.validatePositiveInteger(footSizeString);

                Player p = getEntityManager().find(Player.class, workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID)));
                p.setFirstName(firstName);
                p.setSecondName(secondName);
                p.setHeight(Float.parseFloat(heightString));
                p.setWeight(Float.parseFloat(weightString));
                p.setFootSize(Integer.parseInt(footSizeString));
                p.setNumber(Integer.parseInt(numberString));
                List<Position> positions1 = QueryHelper.get(getEntityManager()).getPositionsList("name='" + position + "'");

                p.getPosition().getPlayers().remove(p);
                p.setPosition(positions1.get(0));
                p.getPosition().getPlayers().add(p);
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
                long id = (long)workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
                Player player = getEntityManager().find(Player.class, id);
                for(EfficiencyIndex index : player.getEfficiencyIndexes()) {
                    index.getName().getEfficiencyIndexes().remove(index);
                }
                for(Goal goal : player.getGoals()) {
                    goal.getMatch().getScoredGoals().remove(goal);
                }
                player.getPosition().getPlayers().remove(player);
                getEntityManager().remove(player);
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
            long id = (long)workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
            Player player = getEntityManager().find(Player.class, id);
            if(player.getEfficiencyIndexes() != null && player.getEfficiencyIndexes().size() > 0
                    || player.getGoals()!=null && player.getGoals().size() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(Component[] values) {
        getEntityManager().getTransaction().begin();
        try {
            String firstName = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_FIRST_NAME)]).getText();
            String secondName = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_SECOND_NAME)]).getText();
            String height = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_HEIGHT)]).getText();
            String weight = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_WEIGHT)]).getText();
            String footSize = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_FOOT_SIZE)]).getText();
            Object positionItem = ((JComboBox)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_POSITION)]).getSelectedItem();
            String numberString = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_NUMBER)]).getText();
            if(numberString == null || numberString.equals("")) throw new EmptyFieldException(COLUMN_NUMBER);
            InputValidationHelper.validatePositiveInteger(numberString);
            validateNumberUnique(numberString);
            if(firstName == null || firstName.equals("")) throw new EmptyFieldException(COLUMN_FIRST_NAME);
            InputValidationHelper.validateName(firstName);
            if(secondName == null || secondName.equals("")) throw new EmptyFieldException(COLUMN_SECOND_NAME);
            InputValidationHelper.validateName(secondName);
            if(height == null || height.equals("")) throw new EmptyFieldException(COLUMN_HEIGHT);
            InputValidationHelper.validatePositiveDouble(height);
            if(weight == null || weight.equals("")) throw new EmptyFieldException(COLUMN_WEIGHT);
            InputValidationHelper.validatePositiveDouble(weight);
            if(footSize == null || footSize.equals("")) throw new EmptyFieldException(COLUMN_FOOT_SIZE);
            InputValidationHelper.validatePositiveInteger(footSize);
            if(positionItem == null) throw new EmptyFieldException(COLUMN_POSITION);

            String positionText = positionItem.toString();
            Position position1 = QueryHelper.get(getEntityManager()).getPositionsList("name='" + positionText + "'").get(0);

            Player player = new Player();
            player.setFirstName(firstName);
            player.setSecondName(secondName);
            player.setHeight(Float.parseFloat(height));
            player.setWeight(Float.parseFloat(weight));
            player.setFootSize(Integer.parseInt(footSize));
            player.setPosition(position1);
            player.setNumber(Integer.parseInt(numberString));
            player.getPosition().getPlayers().add(player);
            getEntityManager().persist(player);
            workingPanel.addRow(createRow(player));
            workingPanel.clearInputInObjectCreationPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }

    private Object[] createRow(Player player) {
        Object[] result = new Object[COLUMNS.length];
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_ID)] = player.getId();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_FIRST_NAME)] = player.getFirstName();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_SECOND_NAME)] = player.getSecondName();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_HEIGHT)] = player.getHeight();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_WEIGHT)] = player.getWeight();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_FOOT_SIZE)] = player.getFootSize();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_EFFICIENCY_INDEXES)] = player.getEfficiencyIndexes().toString();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_POSITION)] = player.getPosition().getName();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_NUMBER)] = player.getNumber();
        return result;
    }

    private void validateNumbersUnique() throws FieldMustHaveUniqueValueException, StringIsNotPositiveIntException {
        int[] numbers = new int[workingPanel.getModel().getRowCount()];
        for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
            String val = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_NUMBER)).toString();
            InputValidationHelper.validatePositiveInteger(val);
            numbers[i] = Integer.parseInt(val);
        }
        Arrays.sort(numbers);
        for(int i = 1; i < numbers.length; i++) {
            if(numbers[i]==numbers[i-1]) {
                throw new FieldMustHaveUniqueValueException(COLUMN_NUMBER, Integer.toString(numbers[i]));
            }
        }
    }

    private void validateNumberUnique(String number) throws FieldMustHaveUniqueValueException {
        List<Player> players = QueryHelper.get(getEntityManager()).getPlayersList("number='" + number + "'");
        if(players.size() != 0) throw new FieldMustHaveUniqueValueException(COLUMN_NUMBER, number);
    }
}
