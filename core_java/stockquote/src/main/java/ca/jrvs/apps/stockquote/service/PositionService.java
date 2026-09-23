package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.model.Position;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionService {

  private static final Logger logger = LoggerFactory.getLogger(PositionService.class);

  private final PositionDao dao;

  public PositionService(PositionDao dao) {
    this.dao = dao;
  }

  /**
   * Processes a buy order and updates the database accordingly.
   *
   * @param ticker         - stock symbol
   * @param numberOfShares - number of shares to buy
   * @param price          - current price per share
   * @return The position in our database after processing the buy
   */
  public Position buy(String ticker, int numberOfShares, double price) {
    logger.debug("Buying position");

    if (ticker == null || ticker.isEmpty()) {
      logger.warn("Invalid ticker symbol in buying position");
      throw new IllegalArgumentException("Invalid ticker symbol");
    }

    if (numberOfShares <= 0) {
      logger.warn("Non-positive share amount in buying position");
      throw new IllegalArgumentException("Number of shares must be positive");
    }

    if (price <= 0) {
      logger.warn("Non-positive price value in buying position");
      throw new IllegalArgumentException("Price must be positive");
    }

    double cost = price * numberOfShares;

    Optional<Position> pos = dao.findById(ticker);
    Position posUpdated;
    if (pos.isPresent()) {
      posUpdated = pos.get();
      posUpdated.setNumOfShares(posUpdated.getNumOfShares() + numberOfShares);
      posUpdated.setValuePaid(posUpdated.getValuePaid() + cost);
    } else {
      posUpdated = new Position();
      posUpdated.setTicker(ticker);
      posUpdated.setNumOfShares(numberOfShares);
      posUpdated.setValuePaid(cost);
    }
    dao.save(posUpdated);
    return posUpdated;
  }

  /**
   * Returns all positions in the portfolio.
   *
   * @return All positions
   */
  public Iterable<Position> viewPortfolio() {
    logger.debug("Viewing Portfolio");
    return dao.findAll();
  }

  /**
   * Sells all shares of the given ticker symbol.
   *
   * @param ticker - stock symbol to sell
   */
  public void sell(String ticker) {
    logger.debug("Selling shares");
    if (ticker == null || ticker.isEmpty()) {
      logger.warn("Invalid ticker symbol in selling position");
      throw new IllegalArgumentException("Invalid ticker symbol");
    }

    Optional<Position> pos = dao.findById(ticker);

    if (pos.isEmpty()) {
      logger.warn("Ticker position not found");
      throw new IllegalArgumentException("You do not own any shares of " + ticker);
    } else {
      dao.deleteById(ticker);
    }
  }
}