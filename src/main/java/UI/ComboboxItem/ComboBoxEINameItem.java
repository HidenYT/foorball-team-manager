package UI.ComboboxItem;

import Entities.EfficiencyIndexName;

public class ComboBoxEINameItem {
    private EfficiencyIndexName name;
    public ComboBoxEINameItem(EfficiencyIndexName name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ComboBoxEINameItem))
            return false;
        ComboBoxEINameItem other = (ComboBoxEINameItem) o;
        return other.name.equals(this.name);
    }

    public EfficiencyIndexName getName() {
        return name;
    }

    public void setName(EfficiencyIndexName name) {
        this.name = name;
    }
}
