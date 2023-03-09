import UI.MainFrame;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {
    public static void main(String[] args){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("lab02persistence");
        EntityManager em = emf.createEntityManager();
        new MainFrame(em).show();
    }
}
