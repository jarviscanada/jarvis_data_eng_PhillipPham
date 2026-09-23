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

import ca.jrvs.apps.stockquote.model.Quote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteDaoTest {

  Connection connection = mock();
  PreparedStatement preparedStatement = mock();
  ResultSet resultSet = mock();

  QuoteDao quoteDao;

  @BeforeEach
  void setup() {
    quoteDao = new QuoteDao(connection);
  }

  @Test
  void save_null_quote() {
    assertThrows(IllegalArgumentException.class, () -> quoteDao.save(null));
  }

  @Test
  void save_null_ticker_quote() {
    Quote quote = new Quote();
    quote.setTicker(null);
    assertThrows(IllegalArgumentException.class, () -> quoteDao.save(quote));
  }

  @Test
  void save_empty_ticker_quote() {
    Quote quote = new Quote();
    quote.setTicker("");
    assertThrows(IllegalArgumentException.class, () -> quoteDao.save(quote));
  }

  @Test
  void save_return_quote() throws SQLException {
    String ticker = "MSFT";
    int volume = 33;
    Quote quote = new Quote();
    quote.setTicker(ticker);
    quote.setVolume(volume);
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    assertEquals(quote, quoteDao.save(quote));
  }

  @Test
  void save_sql_exception() throws SQLException {
    Quote quote = new Quote();
    quote.setTicker("MSFT");
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> quoteDao.save(quote));
  }

  @Test
  void findById_null_ticker_quote() {
    assertThrows(IllegalArgumentException.class, () -> quoteDao.findById(null));
  }

  @Test
  void findById_empty_ticker_quote() {
    String ticker = "";
    assertThrows(IllegalArgumentException.class, () -> quoteDao.findById(ticker));
  }

  @Test
  void findById_found_quote() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);
    Optional<Quote> quote = quoteDao.findById(ticker);
    assertNotNull(quote);
    assertTrue(quote.isPresent());
  }

  @Test
  void findById_not_found_quote() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    Optional<Quote> quote = quoteDao.findById(ticker);
    assertNotNull(quote);
    assertTrue(quote.isEmpty());
  }

  @Test
  void findById_sql_exception() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> quoteDao.findById(ticker));
  }

  @Test
  void findAll_populated() throws SQLException {
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true, true, true, false);
    Iterable<Quote> quotes = quoteDao.findAll();
    int count = 0;
    for (Quote ignored : quotes) {
      count++;
    }
    assertEquals(3, count);
  }

  @Test
  void findAll_empty() throws SQLException {
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    Iterable<Quote> quotes = quoteDao.findAll();
    int count = 0;
    for (Quote ignored : quotes) {
      count++;
    }
    assertEquals(0, count);
  }

  @Test
  void findAll_sql_exception() throws SQLException {
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> quoteDao.findAll());
  }

  @Test
  void deleteById_null_ticker_quote() {
    assertThrows(IllegalArgumentException.class, () -> quoteDao.deleteById(null));
  }

  @Test
  void deleteById_good_quote() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    assertDoesNotThrow(() -> quoteDao.deleteById(ticker));
  }

  @Test
  void deleteById_sql_exception() throws SQLException {
    String ticker = "MSFT";
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> quoteDao.deleteById(ticker));
  }

  @Test
  void deleteAll_no_exception() throws SQLException {
    when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    assertDoesNotThrow(() -> quoteDao.deleteAll());
  }

  @Test
  void deleteAll_sql_exception() throws SQLException {
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
    assertThrows(RuntimeException.class, () -> quoteDao.deleteAll());
  }
}