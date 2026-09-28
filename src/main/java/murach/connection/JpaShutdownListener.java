package murach.connection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class JpaShutdownListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        JpaUtil.close();
    }
}
