package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.model.Position;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.LoadProperties;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionServiceIntTest {

  PositionDao positionDao;
  PositionService positionService;

  private static Map<String, String> properties;

  @BeforeAll
  static void setupAll() {
    properties = LoadProperties.loadProperties();
  }

  @BeforeEach
  void setup() throws SQLException {
    DatabaseConnectionManager dcm = new DatabaseConnectionManager(
        properties.get("db-host"),
        properties.get("db-port"),
        properties.get("db-name"),
        properties.get("db-user"),
        properties.get("db-password")
    );

    Connection connection = dcm.getConnection();

    positionDao = new PositionDao(connection);
    positionService = new PositionService(positionDao);

    positionDao.deleteAll();
  }

  @Test
  void buy_new() {
    String ticker = "MSFT";
    int numOfShares = 40;
    double price = 15;

    Position newPos = positionService.buy(ticker, numOfShares, price);
    assertNotNull(newPos);
    assertEquals(ticker, newPos.getTicker());
    assertEquals(numOfShares, newPos.getNumOfShares());
    assertEquals(numOfShares * price, newPos.getValuePaid());
  }

  @Test
  void buy_existing() {
    String ticker = "MSFT";
    int numOfShares = 40;
    double price = 15;

    positionService.buy(ticker, numOfShares, price);
    Position newPos = positionService.buy(ticker, numOfShares, price);
    assertNotNull(newPos);
    assertEquals(ticker, newPos.getTicker());
    assertEquals(numOfShares + numOfShares, newPos.getNumOfShares());
    assertEquals(2 * (numOfShares * price), newPos.getValuePaid());
  }

  @Test
  void viewPortfolio() {
    Position[] positions = new Position[2];
    positions[0] = new Position();
    positions[1] = new Position();
    positions[0].setTicker("MSFT");
    positions[0].setNumOfShares(30);
    positions[0].setValuePaid(400);
    positions[1].setTicker("AMZN");
    positions[1].setNumOfShares(30);
    positions[1].setValuePaid(400);

    positionDao.save(positions[0]);
    positionDao.save(positions[1]);

    for (Position p : positionService.viewPortfolio()) {
      if (positions[0].getTicker().equals(p.getTicker())) {
        assertEquals(positions[0].getNumOfShares(), p.getNumOfShares());
        assertEquals(positions[0].getValuePaid(), p.getValuePaid());
      } else {
        assertEquals(positions[1].getTicker(), p.getTicker());
        assertEquals(positions[1].getNumOfShares(), p.getNumOfShares());
        assertEquals(positions[1].getValuePaid(), p.getValuePaid());
      }
    }
  }

  @Test
  void sell() {
    String ticker = "MSFT";
    Position position = new Position();
    position.setTicker(ticker);
    position.setNumOfShares(30);
    position.setValuePaid(400);
    positionDao.save(position);

    Optional<Position> optPos = positionDao.findById(ticker);
    assertNotNull(optPos);
    assertTrue(optPos.isPresent());

    positionService.sell("MSFT");

    optPos = positionDao.findById(ticker);
    assertNotNull(optPos);
    assertTrue(optPos.isEmpty());
  }
}