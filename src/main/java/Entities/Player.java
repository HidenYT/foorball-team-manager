package Entities;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "lab2test.players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "height")
    private float height;

    @Column(name = "weight")
    private float weight;

    @Column(name = "foot_size")
    private int footSize;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @OneToMany(mappedBy = "player", cascade = CascadeType.REMOVE)
    private List<EfficiencyIndex> efficiencyIndexes = new LinkedList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.REMOVE)
    private List<Goal> goals = new LinkedList<>();

    @Column(name = "number")
    private int number;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getSecondName() {
        return secondName;
    }
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }
    public int getFootSize() {
        return footSize;
    }
    public void setFootSize(int footSize) {
        this.footSize = footSize;
    }
    public List<EfficiencyIndex> getEfficiencyIndexes() {
        return efficiencyIndexes;
    }
    public void setEfficiencyIndexes(List<EfficiencyIndex> efficiencyIndexes) {
        this.efficiencyIndexes = efficiencyIndexes;
    }
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPlayerNameWithNumber(){
        return "(" + number + ") " + firstName + " " + secondName;
    }

    @Override
    public String toString() {
        return "Entities.Player{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", footSize=" + footSize +
                ", position=" + position +
                ", efficiencyIndexes=" + efficiencyIndexes +
                '}';
    }
}
