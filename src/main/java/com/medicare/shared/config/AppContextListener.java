package com.medicare.shared.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String appDataPath = System.getProperty("user.home") + File.separator + ".medicare";
        try {
            DatabaseConfig.initialize(appDataPath);
            SchemaInit.createTables();
            SchemaInit.seedDefaultAdmin();
            logger.info("Medicare HMS initialized. Database: " + appDataPath + "/medicare.db");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Medicare HMS", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("Medicare HMS shutting down.");
    }
}
