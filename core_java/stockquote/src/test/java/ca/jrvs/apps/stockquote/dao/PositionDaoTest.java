package ca.jrvs.apps.stockquote.dao;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.model.Position;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionDaoTest {

  Connection connection = mock();
  PreparedStatement preparedStatement = mock();
  ResultSet resultSet = mock();

  PositionDao positionDao;

  @BeforeEach
  void setup() {
    positionDao = new PositionDao(connection);
  }

  @Test
  void save_null_position() {
    assertThrows(IllegalArgumentException.class, () -> positionDao.save(null));
  }

  @Test
  void save_null_ticker_quote() {
    Position position = new Position();
    position.setTicker(null);
    assertThrows(IllegalArgumentException.class, () -> positionDao.save(position));
  }

  @Test
  void save_empty_ticker_quote() {
    Position position = new Position();
    position.setTicker("");
    assertThrows(IllegalArgumentException.class, () -> positionDao.save(position));
  }

  @Test
  void save_no_ticker_position() {
    Position position = new Position();
    position.setTicker(null);
    assertThrows(IllegalArgumentException.class, () -> positionDao.save(position));
  }

  @Test
  void save_return_position() throws SQLException {
    String ticker = "MSFT";
    int numOfShares = 33;
    Position position = new Position();
    position.setTicker(ticker);
    position.setNumOfShares(numOfShares);
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    assertEquals(position, positionDao.save(position));
  }

  @Test
  void save_sql_exception() throws SQLException {
    Position position = new Position();
    position.setTicker("MSFT");
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> positionDao.save(position));
  }

  @Test
  void save_empty_ticker_position() {
    Position position = new Position();
    position.setTicker("");
    assertThrows(IllegalArgumentException.class, () -> positionDao.save(position));
  }

  @Test
  void findById_null_ticker_quote() {
    assertThrows(IllegalArgumentException.class, () -> positionDao.findById(null));
  }

  @Test
  void findById_empty_ticker_quote() {
    String ticker = "";
    assertThrows(IllegalArgumentException.class, () -> positionDao.findById(ticker));
  }

  @Test
  void findById_found_quote() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);
    Optional<Position> position = positionDao.findById(ticker);
    assertNotNull(position);
    assertTrue(position.isPresent());
  }

  @Test
  void findById_not_found_quote() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    Optional<Position> position = positionDao.findById(ticker);
    assertNotNull(position);
    assertTrue(position.isEmpty());
  }

  @Test
  void findById_sql_exception() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> positionDao.findById(ticker));
  }

  @Test
  void findAll_populated() throws SQLException {
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true, true, true, false);
    Iterable<Position> positions = positionDao.findAll();
    int count = 0;
    for (Position ignored : positions) {
      count++;
    }
    assertEquals(3, count);
  }

  @Test
  void findAll_empty() throws SQLException {
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    Iterable<Position> positions = positionDao.findAll();
    int count = 0;
    for (Position ignored : positions) {
      count++;
    }
    assertEquals(0, count);
  }

  @Test
  void findAll_sql_exception() throws SQLException {
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> positionDao.findAll());
  }

  @Test
  void deleteById_null_ticker_quote() {
    assertThrows(IllegalArgumentException.class, () -> positionDao.deleteById(null));
  }

  @Test
  void deleteById_good_position() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    assertDoesNotThrow(() -> positionDao.deleteById(ticker));
  }

  @Test
  void deleteById_sql_exception() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> positionDao.deleteById(ticker));
  }

  @Test
  void deleteAll_no_exception() throws SQLException {
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    assertDoesNotThrow(() -> positionDao.deleteAll());
  }

  @Test
  void deleteAll_sql_exception() throws SQLException {
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> positionDao.deleteAll());
  }
}