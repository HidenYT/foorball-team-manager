package Helpers;

import Entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class QueryHelper {
    private static QueryHelper helper;
    private EntityManager entityManager;
    private QueryHelper(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public static QueryHelper get(EntityManager entityManager) {
        if(helper == null) helper = new QueryHelper(entityManager);
        return helper;
    }

    public List<Player> getPlayersList() {
        return entityManager.createQuery("SELECT player FROM Entities.Player player").getResultList();
    }

    public List<Player> getPlayersList(String where) {
        return entityManager.createQuery("SELECT player FROM Entities.Player player WHERE " + where).getResultList();
    }

    public List<Position> getPositionsList() {
        return entityManager.createQuery("SELECT pos FROM Entities.Position pos").getResultList();
    }

    public List<Position> getPositionsList(String where) {
        return entityManager.createQuery("SELECT pos FROM Entities.Position pos WHERE " + where).getResultList();
    }

    public List<Match> getMatchesList() {
        return entityManager.createQuery("SELECT match FROM Entities.Match match").getResultList();
    }

    public List<Match> getMatchesList(String where) {
        return entityManager.createQuery("SELECT match FROM Entities.Match match WHERE " + where).getResultList();
    }

    public List<EfficiencyIndex> getEfficiencyIndexesList() {
        return entityManager.createQuery("SELECT ei FROM Entities.EfficiencyIndex ei").getResultList();
    }

    public List<EfficiencyIndex> getEfficiencyIndexesList(String where) {
        return entityManager.createQuery("SELECT ei FROM Entities.EfficiencyIndex ei WHERE " + where).getResultList();
    }

    public List<EfficiencyIndexName> getEfficiencyIndexesNamesList() {
        return entityManager.createQuery("SELECT ein FROM Entities.EfficiencyIndexName ein").getResultList();
    }

    public List<EfficiencyIndexName> getEfficiencyIndexesNamesList(String where) {
        return entityManager.createQuery("SELECT ein FROM Entities.EfficiencyIndexName ein WHERE " + where).getResultList();
    }

    public List<Goal> getGoalsList(String where) {
        return entityManager.createQuery("SELECT goal FROM Entities.Goal goal WHERE " + where).getResultList();
    }

    public List<Goal> getGoalsList() {
        return entityManager.createQuery("SELECT goal FROM Entities.Goal goal").getResultList();
    }
}
