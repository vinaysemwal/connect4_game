package com.gluck.gaming.objects.factory;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gluck.gaming.dao.GameDao;
import com.gluck.gaming.db.connection.MongoDbConnectionManager;
import com.gluck.gaming.domain.GameDaoImpl;
import com.gluck.gaming.service.Connect4Service;
import com.gluck.gaming.service.Connect4ServiceImpl;
import com.gluck.gaming.service.validator.Connect4ServiceValidator;

/**
 * Factory class to manage connect4 game object creation and management. The purpose of the class is to provide dependency injection feature to the connect4
 * game. It is an effective way of providing inversion of control without having to depend on heavy weight frameworks like spring
 *
 * @author Vinay Semwal
 */
public class Connect4Factory {

    private static final Logger logger = LogManager.getLogger(Connect4Factory.class);

    private static final String DB_NAME = "db.name";

    private static final String DB_PORT = "db.port";

    private static final String DB_HOST = "db.host";

    private static Connect4ServiceValidator connect4ServiceValidator;

    private static Connect4Service connect4Service;

    private static GameDao gameDao;

    private static MongoDbConnectionManager connectionManager;

    private static ConfigProvider configProvider = new ConfigProvider();

    /**
     * @return {@link MongoDbConnectionManager}
     */
    public static MongoDbConnectionManager getMongoDBConnectionManager() {
        if (Objects.isNull(connectionManager)) {
            logger.info(
                "Tring to initialize the DB connection manager with following connection details. Database name : {}, host : {}, port : {}",
                configProvider.getConfigurations().getProperty(DB_NAME),
                configProvider.getConfigurations().getProperty(DB_HOST),
                configProvider.getConfigurations().getProperty(DB_PORT));
            connectionManager = new MongoDbConnectionManager(
                configProvider.getConfigurations().getProperty(DB_NAME),
                configProvider.getConfigurations().getProperty(DB_HOST),
                Integer.valueOf(configProvider.getConfigurations().getProperty(DB_PORT)));
        }
        return connectionManager;
    }

    /**
     * @return the connect4Service
     */
    public static Connect4Service getConnect4Service() {
        if (Objects.isNull(connect4Service)) {
            connect4Service = new Connect4ServiceImpl(getConnect4ServiceValidator(), getGameDao());
        }
        return connect4Service;
    }

    /**
     * @return
     */
    private static GameDao getGameDao() {
        if (Objects.isNull(gameDao)) {
            gameDao = new GameDaoImpl(getMongoDBConnectionManager());
        }
        return gameDao;
    }

    /**
     * @return the connect4ServiceValidator
     */
    private static Connect4ServiceValidator getConnect4ServiceValidator() {
        if (Objects.isNull(connect4ServiceValidator)) {
            connect4ServiceValidator = new Connect4ServiceValidator();
        }
        return connect4ServiceValidator;
    }

}
