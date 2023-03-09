package Entities;

import javax.persistence.*;

@Entity
@Table(name = "lab2test.efficiency_indexes")
public class EfficiencyIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "name_id")
    private EfficiencyIndexName name;

    @Column(name = "value")
    private float value;

    public EfficiencyIndexName getName() {
        return name;
    }

    public void setName(EfficiencyIndexName name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name.getName() + ": " + value;
    }

}
