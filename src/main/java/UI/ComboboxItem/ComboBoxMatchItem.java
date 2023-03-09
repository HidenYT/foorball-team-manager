package UI.ComboboxItem;

import Entities.Match;

public class ComboBoxMatchItem {
    private Match match;
    public ComboBoxMatchItem(Match match) {
        this.match = match;
    }
    @Override
    public String toString() {
        return match.getDateWithRival();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ComboBoxMatchItem))
            return false;
        ComboBoxMatchItem other = (ComboBoxMatchItem) o;
        return other.match.equals(this.match);
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
