package ca.jrvs.apps.stockquote.controller;

import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteService;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.LoadProperties;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.debug("Program Start");

    Map<String, String> properties = LoadProperties.loadProperties();

    DatabaseConnectionManager dcm = new DatabaseConnectionManager(
        properties.get("db-host"),
        properties.get("db-port"),
        properties.get("db-name"),
        properties.get("db-user"),
        properties.get("db-password")
    );
    try (Connection connection = dcm.getConnection()) {
      // DAO later
      OkHttpClient httpClient = new OkHttpClient();
      QuoteHttpHelper httpHelper = new QuoteHttpHelper(properties.get("api-key"), httpClient);

      QuoteDao quoteDao = new QuoteDao(connection);
      PositionDao positionDao = new PositionDao(connection);

      // Service layer
      QuoteService quoteService = new QuoteService(quoteDao, httpHelper);
      PositionService positionService = new PositionService(positionDao);

      // Controller
      StockQuoteController controller = new StockQuoteController(quoteService, positionService);
      controller.initClient();
    } catch (SQLException e) {
      logger.error("Failed to connect to database: {}", e.getMessage());
      System.err.println("Failed to connect to database: " + e.getMessage());
    }
    logger.debug("Program End");
  }
}
