package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.model.GlobalQuoteResponse;
import ca.jrvs.apps.stockquote.model.Quote;
import ca.jrvs.apps.stockquote.util.JsonParser;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteHttpHelper {

  private static final Logger logger = LoggerFactory.getLogger(QuoteHttpHelper.class);
  private static final String BASE_URL =
      "https://www.alphavantage.co/query?function=GLOBAL_QUOTE";

  private final String apiKey;
  private final OkHttpClient client;

  public QuoteHttpHelper(String apiKey, OkHttpClient client) {
    this.apiKey = apiKey;
    this.client = client;
  }

  /**
   * Fetch latest quote data from Alpha Vantage endpoint.
   *
   * @param symbol - stock ticker symbol (e.g. "AAPL", "MSFT")
   * @return Quote with latest data
   * @throws IllegalArgumentException if no data was found for the given symbol
   */
  public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
    logger.debug("Fetching quote");
    if (symbol == null || symbol.isEmpty()) {
      logger.warn("Quote symbol cannot be empty nor null");
      throw new IllegalArgumentException("Quote symbol cannot be empty nor null");
    }

    Request request = new Request.Builder()
        .url(BASE_URL + "&symbol=" + symbol + "&apikey=" + apiKey)
        .build();    // Create http request

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        logger.warn("API response unsuccessful, code: {}", response.code());
        throw new IllegalArgumentException("API response unsuccessful, code: " + response.code());
      }

      String resBody;
      if (response.body() != null) {
        resBody = response.body().string();
      } else {
        logger.warn("Bad response body");
        throw new IllegalArgumentException("Bad response body");
      }

      GlobalQuoteResponse quoteWrapper = JsonParser.toObjectFromJson(resBody,
          GlobalQuoteResponse.class);
      Quote quote = quoteWrapper.getQuote();

      if (quote == null) {
        logger.warn("Invalid quote");
        throw new IllegalArgumentException("Invalid quote");
      }

      if (quote.getTicker().isEmpty()) {
        logger.warn("Invalid symbol");
        throw new IllegalArgumentException("Invalid symbol");
      }

      quote.setTimestamp(Timestamp.from(Instant.now()));
      return quote;

    } catch (IOException e) {
      logger.error("API request unable to execute", e);
      throw new IllegalArgumentException("API request unable to execute: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Quote processing failure", e);
      throw new IllegalArgumentException("Quote processing failure: " + e.getMessage());
    }
  }
}