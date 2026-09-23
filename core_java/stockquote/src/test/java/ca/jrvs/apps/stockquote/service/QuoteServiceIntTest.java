package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.stockquote.model.Quote;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.LoadProperties;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteServiceIntTest {

  QuoteHttpHelper httpHelper = mock();

  QuoteDao quoteDao;
  QuoteService quoteService;

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

    quoteDao = new QuoteDao(connection);
    quoteService = new QuoteService(quoteDao, httpHelper);
  }

  @Test
  void fetchQuoteDataFromAPI_save_upsert_delete() {
    String ticker = "LOOL";
    int volume = 30;

    Quote quote = new Quote();
    quote.setTicker(ticker);
    quote.setVolume(volume);
    quote.setChangePercent("15");
    quote.setLatestTradingDay(Date.valueOf(LocalDate.now()));
    quote.setTimestamp(Timestamp.from(Instant.now()));

    when(httpHelper.fetchQuoteInfo(anyString())).thenReturn(quote);

    // Save
    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    assertTrue(optQuote.isPresent());
    assertEquals(ticker, optQuote.get().getTicker());
    assertEquals(volume, optQuote.get().getVolume());

    // Upsert
    volume = 33;
    quote.setVolume(volume);

    optQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    assertTrue(optQuote.isPresent());
    assertEquals(ticker, optQuote.get().getTicker());
    assertEquals(volume, optQuote.get().getVolume());

    // Delete
    quoteDao.deleteById(ticker);
    assertTrue(quoteDao.findById(ticker).isEmpty());
  }

  @Test
  void fetchQuoteDataFromAPI_null() {
    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI(null);
    assertTrue(optQuote.isEmpty());
  }

  @Test
  void fetchQuoteDataFromAPI_empty() {
    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI("");
    assertTrue(optQuote.isEmpty());
  }

}