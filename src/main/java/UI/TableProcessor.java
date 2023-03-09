package UI;

import javax.swing.*;

public interface TableProcessor {
    void saveTable();
    void deleteObjects();
    JPanel getPanel();
    boolean isDeletionSafe();
}
