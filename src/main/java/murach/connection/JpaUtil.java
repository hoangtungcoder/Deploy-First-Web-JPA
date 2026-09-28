package murach.connection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public final class JpaUtil {

    private static final EntityManagerFactory EMF =
            createEntityManagerFactory();

    private JpaUtil() {
    }

    public static EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }

    public static void close() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, Object> properties = new HashMap<>();
        putEnvironmentValue(
                properties,
                "jakarta.persistence.jdbc.url",
                "DB_URL"
        );
        putEnvironmentValue(
                properties,
                "jakarta.persistence.jdbc.user",
                "DB_USERNAME"
        );
        putEnvironmentValue(
                properties,
                "jakarta.persistence.jdbc.password",
                "DB_PASSWORD"
        );

        return Persistence.createEntityManagerFactory(
                "emailListPU", properties
        );
    }

    private static void putEnvironmentValue(
            Map<String, Object> properties,
            String propertyName,
            String environmentName
    ) {
        String value = System.getenv(environmentName);

        if (value != null && !value.trim().isEmpty()) {
            properties.put(propertyName, value.trim());
        }
    }
}
