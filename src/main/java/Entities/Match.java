package Entities;

import Helpers.DateTransformer;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;


@Entity
@Table(name = "lab2test.matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "rival")
    private String rival;

    @Column(name = "date")
    private String date;

    @Column(name = "missed_goals_count")
    private int missedGoalsCount;

    @OneToMany(mappedBy = "match", cascade = CascadeType.REMOVE)
    private List<Goal> scoredGoals = new LinkedList<>();

    public int getScoredGoalsCount() {
        return scoredGoals.size();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRival() {
        return rival;
    }

    public void setRival(String rival) {
        this.rival = rival;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMissedGoalsCount() {
        return missedGoalsCount;
    }

    public void setMissedGoalsCount(int missedGoalsCount) {
        this.missedGoalsCount = missedGoalsCount;
    }

    public List<Goal> getScoredGoals() {
        return scoredGoals;
    }

    public void setScoredGoals(List<Goal> scoredGoals) {
        this.scoredGoals = scoredGoals;
    }

    public String getDateWithRival() {
        return "(" + DateTransformer.normalDateFromDBDate(date) + ") " + rival;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", rival='" + rival + '\'' +
                ", date='" + date + '\'' +
                ", missedGoalsCount=" + missedGoalsCount +
                ", scoredGoals=" + scoredGoals +
                '}';
    }
}
