package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.model.Position;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String> {

  private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);

  private final Connection c;

  private static final String UPSERT = "INSERT INTO position " +
      "(symbol, number_of_shares, value_paid) " +
      "VALUES (?, ?, ?) " +
      "ON CONFLICT (symbol) DO UPDATE SET " +
      "number_of_shares = EXCLUDED.number_of_shares, " +
      "value_paid = EXCLUDED.value_paid;";

  private static final String FIND_BY_ID = "SELECT * FROM position WHERE symbol = ?;";

  private static final String FIND_ALL = "SELECT * FROM position;";

  private static final String DELETE_BY_ID = "DELETE FROM position WHERE symbol = ?;";

  private static final String DELETE_ALL = "DELETE FROM position;";


  public PositionDao(Connection c) {
    this.c = c;
  }

  /**
   * Save (upsert) a position to the database. Note: the symbol must already exist in the quote
   * table (FK constraint).
   */
  @Override
  public Position save(Position entity) throws IllegalArgumentException {
    logger.debug("Saving position");
    if (entity == null) {
      logger.warn("Null entity on Saving Position");
      throw new IllegalArgumentException("Invalid entity");
    }
    if (entity.getTicker() == null || entity.getTicker().isEmpty()) {
      logger.warn("Invalid ticker symbol in Saving Position");
      throw new IllegalArgumentException("Invalid symbol");
    }

    try (PreparedStatement ps = c.prepareStatement(UPSERT)) {
      ps.setString(1, entity.getTicker());
      ps.setInt(2, entity.getNumOfShares());
      ps.setDouble(3, entity.getValuePaid());
      ps.executeUpdate();

      logger.debug("Successful Upsert on Position");
      return entity;

    } catch (SQLException e) {
      logger.error("Exception saving position", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Find a position by its ticker symbol.
   */
  @Override
  public Optional<Position> findById(String id) throws IllegalArgumentException {
    logger.debug("Finding position by id {}", id);
    if (id == null || id.isEmpty()) {
      logger.warn("Invalid ticker symbol in Finding position");
      throw new IllegalArgumentException("Invalid id");
    }

    try (PreparedStatement ps = c.prepareStatement(FIND_BY_ID)) {
      ps.setString(1, id);
      ResultSet rs = ps.executeQuery();

      Position position;
      if (rs.next()) {
        position = mapRowToPosition(rs);
        logger.debug("Successfully found position");
        return Optional.of(position);
      } else {
        logger.debug("Position with ticker {} not found", id);
        return Optional.empty();
      }
    } catch (SQLException e) {
      logger.error("Exception in finding position", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Find all positions.
   */
  @Override
  public Iterable<Position> findAll() {
    logger.debug("Finding all positions");
    List<Position> ls = new ArrayList<>();
    try (PreparedStatement ps = c.prepareStatement(FIND_ALL)) {
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        ls.add(mapRowToPosition(rs));
      }
    } catch (SQLException e) {
      logger.error("Exception in finding all positions", e);
      throw new RuntimeException(e.getMessage());
    }
    logger.debug("Found {} positions", ls.size());
    return ls;
  }

  /**
   * Delete a position by its ticker symbol.
   */
  @Override
  public void deleteById(String id) throws IllegalArgumentException {
    logger.debug("Deleting position with id {}", id);
    if (id == null) {
      logger.warn("Invalid ticker symbol in deleting position");
      throw new IllegalArgumentException("Invalid id");
    }

    try (PreparedStatement ps = c.prepareStatement(DELETE_BY_ID)) {
      ps.setString(1, id);
      int rowsDeleted = ps.executeUpdate();

      if (rowsDeleted >= 1) {
        logger.debug("Deleted position with id: {}", id);
      } else {
        logger.debug("No position deleted with id: {}", id);
      }
    } catch (SQLException e) {
      logger.error("Exception in deleting position", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Delete all positions.
   */
  @Override
  public void deleteAll() {
    logger.debug("Deleting all positions");
    try (PreparedStatement ps = c.prepareStatement(DELETE_ALL)) {
      int rowsDeleted = ps.executeUpdate();
      logger.debug("Successfully deleted {} rows", rowsDeleted);
    } catch (SQLException e) {
      logger.error("Exception in deleting all positions", e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Map a ResultSet row to a Position object.
   */
  private Position mapRowToPosition(ResultSet rs) throws SQLException {
    Position position = new Position();
    position.setTicker(rs.getString("symbol"));
    position.setNumOfShares(rs.getInt("number_of_shares"));
    position.setValuePaid(rs.getDouble("value_paid"));
    return position;
  }
}
