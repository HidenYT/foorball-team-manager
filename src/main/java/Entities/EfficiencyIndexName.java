package Entities;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "lab2test.efficiency_index_names")
public class EfficiencyIndexName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "name", cascade = CascadeType.REMOVE)
    private List<EfficiencyIndex> efficiencyIndexes = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<EfficiencyIndex> getEfficiencyIndexes() {
        return efficiencyIndexes;
    }

    public void setEfficiencyIndexes(List<EfficiencyIndex> efficiencyIndexes) {
        this.efficiencyIndexes = efficiencyIndexes;
    }
}
