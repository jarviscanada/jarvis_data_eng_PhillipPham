package ca.jrvs.apps.stockquote.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper for Alpha Vantage API response.
 * The JSON is wrapped in a "Global Quote" object:
 * {
 *   "Global Quote": {
 *     "01. symbol": "MSFT",
 *     ...
 *   }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalQuoteResponse {

  @JsonProperty("Global Quote")
  private Quote quote;

  public Quote getQuote() {
    return quote;
  }

  public void setQuote(Quote quote) {
    this.quote = quote;
  }
}