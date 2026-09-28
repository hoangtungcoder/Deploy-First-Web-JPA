package murach.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import murach.business.User;
import murach.connection.JpaUtil;

public class UserDAO {

    public static boolean emailExists(String email) {
        EntityManager em = JpaUtil.createEntityManager();

        try {
            String jpql = "SELECT COUNT(u) "
                    + "FROM User u "
                    + "WHERE u.email = :email";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("email", email);

            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public static int insert(User user) {
        EntityManager em = JpaUtil.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(user);
            transaction.commit();
            return 1;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            em.close();
        }
    }
}
