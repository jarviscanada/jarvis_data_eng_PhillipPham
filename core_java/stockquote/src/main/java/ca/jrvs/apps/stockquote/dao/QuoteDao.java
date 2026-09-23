package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.model.Quote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteDao implements CrudDao<Quote, String> {

  private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);

  private final Connection c;

  private static final String UPSERT = "INSERT INTO quote " +
      "(symbol, open, high, low, price, volume, latest_trading_day, " +
      "previous_close, change, change_percent, timestamp) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
      "ON CONFLICT (symbol) DO UPDATE SET " +
      "open = EXCLUDED.open, " +
      "high = EXCLUDED.high, " +
      "low = EXCLUDED.low, " +
      "price = EXCLUDED.price, " +
      "volume = EXCLUDED.volume, " +
      "latest_trading_day = EXCLUDED.latest_trading_day, " +
      "previous_close = EXCLUDED.previous_close, " +
      "change = EXCLUDED.change, " +
      "change_percent = EXCLUDED.change_percent, " +
      "timestamp = EXCLUDED.timestamp;";

  private static final String FIND_BY_ID = "SELECT * FROM quote WHERE symbol = ?;";

  private static final String FIND_ALL = "SELECT * FROM quote;";

  private static final String DELETE_BY_ID = "DELETE FROM quote WHERE symbol = ?;";

  private static final String DELETE_ALL = "DELETE FROM quote;";

  public QuoteDao(Connection c) {
    this.c = c;
  }

  /**
   * Save (upsert) a quote to the database.
   */
  @Override
  public Quote save(Quote entity) throws IllegalArgumentException {
    logger.debug("Saving Quote");
    if (entity == null) {
      logger.warn("Null entity on Saving Quote");
      throw new IllegalArgumentException("Invalid entity");
    }
    if (entity.getTicker() == null || entity.getTicker().isEmpty()) {
      logger.warn("Invalid ticker symbol in Saving Quote");
      throw new IllegalArgumentException("Invalid symbol");
    }

    try (PreparedStatement ps = c.prepareStatement(UPSERT)) {
      ps.setString(1, entity.getTicker());
      ps.setDouble(2, entity.getOpen());
      ps.setDouble(3, entity.getHigh());
      ps.setDouble(4, entity.getLow());
      ps.setDouble(5, entity.getPrice());
      ps.setInt(6, entity.getVolume());
      ps.setDate(7, entity.getLatestTradingDay());
      ps.setDouble(8, entity.getPreviousClose());
      ps.setDouble(9, entity.getChange());
      ps.setString(10, entity.getChangePercent());
      ps.setTimestamp(11, entity.getTimestamp());
      ps.executeUpdate();

      logger.debug("Successful Upsert on quote");
      return entity;

    } catch (SQLException e) {
      logger.error("Exception saving quote", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Find a quote by its ticker symbol.
   */
  @Override
  public Optional<Quote> findById(String id) throws IllegalArgumentException {
    logger.debug("Finding quote by id {}", id);
    if (id == null || id.isEmpty()) {
      logger.warn("Invalid ticker symbol in Finding quote");
      throw new IllegalArgumentException("Invalid id");
    }

    try (PreparedStatement ps = c.prepareStatement(FIND_BY_ID)) {
      ps.setString(1, id);
      ResultSet rs = ps.executeQuery();

      Quote quote;
      if(rs.next()){
        quote = mapRowToQuote(rs);
        logger.debug("Successfully found quote");
        return Optional.of(quote);
      } else {
        logger.debug("Quote with ticker {} not found", id);
        return Optional.empty();
      }
    } catch (SQLException e) {
      logger.error("Exception in finding quote", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Find all quotes in the database.
   */
  @Override
  public Iterable<Quote> findAll() {
    logger.debug("Finding all quotes");
    List<Quote> ls = new ArrayList<>();
    try (PreparedStatement ps = c.prepareStatement(FIND_ALL)) {
      ResultSet rs = ps.executeQuery();
      while(rs.next()){
        ls.add(mapRowToQuote(rs));
      }
    } catch (SQLException e) {
      logger.error("Exception in finding all quotes", e);
      throw new RuntimeException(e.getMessage());
    }
    logger.debug("Found {} quotes", ls.size());
    return ls;
  }

  /**
   * Delete a quote by its ticker symbol.
   * Note: If the symbol doesn't exist, executeUpdate() returns 0 - that's fine,
   * silently ignore it (per CrudDao contract).
   */
  @Override
  public void deleteById(String id) throws IllegalArgumentException {
    logger.debug("Deleting quote with id {}", id);
    if (id == null) {
      logger.warn("Invalid ticker symbol in deleting quote");
      throw new IllegalArgumentException("Invalid id");
    }

    try (PreparedStatement ps = c.prepareStatement(DELETE_BY_ID)) {
      ps.setString(1, id);
      int rowsDeleted = ps.executeUpdate();

      if (rowsDeleted >= 1) {
        logger.debug("Deleted quote with id: {}", id);
      } else {
        logger.debug("No quote deleted with id: {}", id);
      }
    } catch (SQLException e) {
      logger.error("Exception in deleting quote", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Delete all quotes from the database.
   * Warning: This will fail if any positions still reference quotes (FK constraint).
   * Always delete positions first!
   */
  @Override
  public void deleteAll() {
    logger.debug("Deleting all quotes");
    try (PreparedStatement ps = c.prepareStatement(DELETE_ALL)) {
      int rowsDeleted = ps.executeUpdate();
      logger.debug("Successfully deleted {} rows", rowsDeleted);
    } catch (SQLException e) {
      logger.error("Exception deleting all quotes", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Map a ResultSet row to a Quote object.
   */
  private Quote mapRowToQuote(ResultSet rs) throws SQLException {
    Quote quote = new Quote();
    quote.setTicker(rs.getString("symbol"));
    quote.setOpen(rs.getDouble("open"));
    quote.setHigh(rs.getDouble("high"));
    quote.setLow(rs.getDouble("low"));
    quote.setPrice(rs.getDouble("price"));
    quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
    quote.setPreviousClose(rs.getDouble("previous_close"));
    quote.setChange(rs.getDouble("change"));
    quote.setChangePercent(rs.getString("change_percent"));
    quote.setTimestamp(rs.getTimestamp("timestamp"));
    return quote;
  }
}