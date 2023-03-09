package UI.ComboboxItem;

import Entities.Player;

public class ComboBoxPlayerItem {
    private Player player;
    public ComboBoxPlayerItem(Player player) {
        this.player = player;
    }
    @Override
    public String toString() {
        return player.getPlayerNameWithNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ComboBoxPlayerItem))
            return false;
        ComboBoxPlayerItem other = (ComboBoxPlayerItem) o;
        return other.player.equals(this.player);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
