package ca.jrvs.apps.stockquote.service;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.stockquote.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QuoteService {

  private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

  private final QuoteDao dao;
  private final QuoteHttpHelper httpHelper;

  public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper) {
    this.dao = dao;
    this.httpHelper = httpHelper;
  }

  /**
   * Fetches latest quote data from Alpha Vantage API and persists it to the database.
   *
   * @param ticker - stock symbol
   * @return Latest quote information or empty optional if ticker symbol not found
   */
  public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
    logger.debug("Fetching quote data from API");
    Quote quote;
    try {
      quote = httpHelper.fetchQuoteInfo(ticker);
      quote = dao.save(quote);
      logger.debug("Success fetching quote data");
      return Optional.of(quote);
    } catch (IllegalArgumentException e) {
      logger.warn("Unsuccessful in fetching quote data");
      return Optional.empty();
    }
  }
}