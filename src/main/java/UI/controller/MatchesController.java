package UI.controller;

import Entities.Goal;
import Entities.Match;
import Exceptions.EmptyFieldException;
import Exceptions.FieldMustHaveUniqueValueException;
import Helpers.ColumnIndexFinder;
import Helpers.DateTransformer;
import Helpers.InputValidationHelper;
import Helpers.QueryHelper;
import UI.ObjectCreationPanel;
import UI.view.MatchesWorkingPanel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static UI.view.MatchesWorkingPanel.*;

public class MatchesController extends AbstractTableController implements ObjectCreationPanel.CreateObjectClickListener, FilterMatchesClickListener {
    private final MatchesWorkingPanel workingPanel;
    private MatchesTab currentTab;
    private enum MatchesTab {
        ALL_MATCHES, FOLLOWING_MATCHES, PASSED_MATCHES
    }

    public MatchesController(JFrame parentFrame, EntityManager entityManager) {
        super(parentFrame, entityManager);
        Object[][] data = getData();
        int[] count = countPassedAndFollowingMatches(data);
        workingPanel = new MatchesWorkingPanel(getData(), this, this, count[0], count[1]);
        currentTab = MatchesTab.ALL_MATCHES;
    }

    private int[] countPassedAndFollowingMatches(Object[][] data) {
        Date currentDate = Calendar.getInstance().getTime();
        int following = 0, passed = 0;
        for(int i = 0; i < data.length; i++) {
            Date date;
            try {
                date = new SimpleDateFormat("dd.MM.yyyy").parse((String)data[i][ColumnIndexFinder.find(COLUMNS, COLUMN_DATE)]);
                if(date.after(currentDate)) following++;
                else passed++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new int[]{passed, following};
    }

    private Object[][] getData() {
        List<Match> matchList = QueryHelper.get(getEntityManager()).getMatchesList();
        Object[][] result = new Object[matchList.size()][COLUMNS.length];
        for(int i = 0; i < matchList.size(); i++) {
            Match m = matchList.get(i);
            result[i] = createRow(m);
        }
        return result;
    }

    private Object[][] getData(List<Match> matchList) {
        Object[][] result = new Object[matchList.size()][COLUMNS.length];
        for(int i = 0; i < matchList.size(); i++) {
            Match m = matchList.get(i);
            result[i] = createRow(m);
        }
        return result;
    }

    @Override
    public void saveTable() {
        getEntityManager().getTransaction().begin();
        try {
            validateDatesUnique();
            for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
                String date = (String) workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_DATE));
                String rival = (String) workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_RIVAL));
                String missedGoalsCntString = workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_MISSED_GOALS_COUNT)).toString();

                InputValidationHelper.validateDate(date);
                InputValidationHelper.validatePositiveInteger(missedGoalsCntString);

                long id = (Long)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
                Match m = getEntityManager().find(Match.class, id);
                m.setRival(rival);
                m.setDate(DateTransformer.DBDateFromNormalDate(date));
                m.setMissedGoalsCount(Integer.parseInt(missedGoalsCntString));
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
                Match match = getEntityManager().find(Match.class, id);
                for(Goal goal : match.getScoredGoals()) {
                    goal.getPlayer().getGoals().remove(goal);
                }
                getEntityManager().remove(match);
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
            long id = (Long) workingPanel.getTable().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_ID));
            Match match = getEntityManager().find(Match.class, id);
            if(match.getScoredGoals() != null && match.getScoredGoalsCount() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(Component[] values) {
        getEntityManager().getTransaction().begin();
        try {
            String date = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_DATE)]).getText();
            String rival = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_RIVAL)]).getText();
            String missedGoalsCntString = ((JTextField)values[ColumnIndexFinder.find(ADD_LABELS, COLUMN_MISSED_GOALS_COUNT)]).getText();
            if(date==null || date.equals("")) throw new EmptyFieldException(COLUMN_DATE);
            InputValidationHelper.validateDate(date);
            validateDateUnique(date);
            if(rival==null || rival.equals("")) throw new EmptyFieldException(COLUMN_RIVAL);
            if(missedGoalsCntString==null || missedGoalsCntString.equals(""))
                throw new EmptyFieldException(COLUMN_MISSED_GOALS_COUNT);
            InputValidationHelper.validatePositiveInteger(missedGoalsCntString);

            Match match = new Match();
            match.setRival(rival);
            match.setDate(DateTransformer.DBDateFromNormalDate(date));
            match.setMissedGoalsCount(Integer.parseInt(missedGoalsCntString));
            match.setScoredGoals(new LinkedList<>());
            getEntityManager().persist(match);
            workingPanel.clearInputInObjectCreationPanel();
            switch (currentTab) {
                case ALL_MATCHES:
                    onAllMatchesClicked();
                    break;
                case FOLLOWING_MATCHES:
                    onFollowingMatchesClicked();
                    break;
                case PASSED_MATCHES:
                    onPassedMatchesClicked();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            getEntityManager().getTransaction().commit();
        }
    }

    private Object[] createRow(Match match) {
        Object[] result = new Object[COLUMNS.length];
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_ID)] = match.getId();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_DATE)] = DateTransformer.normalDateFromDBDate(match.getDate());
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_RIVAL)] = match.getRival();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_MISSED_GOALS_COUNT)] = match.getMissedGoalsCount();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_SCORED_GOALS_COUNT)] = match.getScoredGoalsCount();
        result[ColumnIndexFinder.find(COLUMNS, COLUMN_GOALS)] = match.getScoredGoals().toString();
        return result;
    }

    @Override
    public void onFollowingMatchesClicked() {
        currentTab = MatchesTab.FOLLOWING_MATCHES;
        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        List<Match> matchList = QueryHelper.get(getEntityManager()).getMatchesList("date >= '" + currentTime + "'");
        workingPanel.setTableContent(getData(matchList));
        int[] count = countPassedAndFollowingMatches(getData());
        workingPanel.updateButtonsText(count[0], count[1]);

    }

    @Override
    public void onPassedMatchesClicked() {
        currentTab = MatchesTab.PASSED_MATCHES;
        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        List<Match> matchList = QueryHelper.get(getEntityManager()).getMatchesList("date < '" + currentTime + "'");
        //workingPanel.setModel(new DefaultTableModel(getData(matchList), COLUMNS));
        workingPanel.setTableContent(getData(matchList));
        int[] count = countPassedAndFollowingMatches(getData());
        workingPanel.updateButtonsText(count[0], count[1]);
    }

    @Override
    public void onAllMatchesClicked() {
        currentTab = MatchesTab.ALL_MATCHES;
        workingPanel.setTableContent(getData());
        int[] count = countPassedAndFollowingMatches(getData());
        workingPanel.updateButtonsText(count[0], count[1]);
    }

    private void validateDatesUnique() throws FieldMustHaveUniqueValueException {
        String[] dates = new String[workingPanel.getModel().getRowCount()];
        for(int i = 0; i < workingPanel.getModel().getRowCount(); i++) {
            dates[i] = (String)workingPanel.getModel().getValueAt(i, ColumnIndexFinder.find(COLUMNS, COLUMN_DATE));
        }
        Arrays.sort(dates);
        for(int i = 1; i < dates.length; i++) {
            if(dates[i].equals(dates[i-1])) {
                throw new FieldMustHaveUniqueValueException(COLUMN_DATE, dates[i]);
            }
        }
    }

    private void validateDateUnique(String date) throws FieldMustHaveUniqueValueException {
        List<Match> matchList = QueryHelper.get(getEntityManager()).getMatchesList("date='" + DateTransformer.DBDateFromNormalDate(date) + "'");
        if(matchList.size() != 0) throw new FieldMustHaveUniqueValueException(COLUMN_DATE, date);
    }
}
