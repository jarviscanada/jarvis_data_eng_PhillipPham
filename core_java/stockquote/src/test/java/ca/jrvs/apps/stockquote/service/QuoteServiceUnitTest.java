package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.stockquote.model.Quote;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteServiceUnitTest {

  QuoteDao quoteDao = mock();
  QuoteHttpHelper httpHelper = mock();

  QuoteService quoteService;

  @BeforeEach
  void setup() {
    quoteService = new QuoteService(quoteDao, httpHelper);
  }

  @Test
  void fetchQuoteDataFromAPI_return_quote() {
    String ticker = "MSFT";
    Quote quote = new Quote();
    quote.setTicker(ticker);
    when(httpHelper.fetchQuoteInfo(ticker)).thenReturn(quote);
    when(quoteDao.save(any())).thenReturn(quote);
    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    assertTrue(optQuote.isPresent());
    assertEquals(ticker, optQuote.get().getTicker());
    verify(quoteDao, times(1)).save(any());
  }

  @Test
  void fetchQuoteDataFromAPI_bad_fetch() {
    String ticker = "MSFT";
    when(httpHelper.fetchQuoteInfo(ticker)).thenThrow(IllegalArgumentException.class);
    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    assertTrue(optQuote.isEmpty());
    verify(quoteDao, never()).save(any());
  }

  @Test
  void fetchQuoteDataFromAPI_bad_save() {
    String ticker = "MSFT";
    Quote quote = new Quote();
    quote.setTicker(ticker);
    when(httpHelper.fetchQuoteInfo(ticker)).thenReturn(quote);
    when(quoteDao.save(any())).thenThrow(IllegalArgumentException.class);
    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    assertTrue(optQuote.isEmpty());
    verify(quoteDao, times(1)).save(any());
  }
}