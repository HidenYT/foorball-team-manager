package UI.controller;

import UI.TableProcessor;

import javax.persistence.EntityManager;
import javax.swing.*;

public abstract class AbstractTableController implements TableProcessor {
    private JFrame parentFrame;
    private EntityManager entityManager;

    public AbstractTableController(JFrame parentFrame, EntityManager entityManager){
        this.parentFrame = parentFrame; this.entityManager = entityManager;
    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
