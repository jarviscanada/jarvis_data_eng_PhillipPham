package ca.jrvs.apps.stockquote.controller;

import ca.jrvs.apps.stockquote.model.Position;
import ca.jrvs.apps.stockquote.model.Quote;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteService;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteController {

  private static final Logger logger = LoggerFactory.getLogger(StockQuoteController.class);

  private final QuoteService quoteService;
  private final PositionService positionService;

  public StockQuoteController(QuoteService quoteService, PositionService positionService) {
    this.quoteService = quoteService;
    this.positionService = positionService;
  }

  /**
   * User interface for the stock quote application.
   */
  public void initClient() {
    logger.info("Client Start");
    try (Scanner scanner = new Scanner(System.in)) {
      boolean run = true;
      while (run) {
        System.out.println();
        System.out.println("=== Stock Quote App ===");
        System.out.println("--- Menu ---");
        System.out.println("1. View stock quote");
        System.out.println("2. Buy shares");
        System.out.println("3. Sell shares");
        System.out.println("4. View portfolio");
        System.out.println("Q. Quit");
        System.out.println("Enter choice:");
        String input = scanner.nextLine().trim().toUpperCase();

        switch (input) {
          case "1":
            handleViewQuote(scanner);
            break;
          case "2":
            handleBuy(scanner);
            break;
          case "3":
            handleSell(scanner);
            break;
          case "4":
            handleViewPortfolio();
            break;
          case "Q":
            System.out.println("Exiting Program");
            run = false;
            break;
          default:
            System.out.println("Invalid Selection");
            break;
        }
      }
    }
  }

  /**
   * Handle viewing a stock quote.
   */
  private void handleViewQuote(Scanner scanner) {
    logger.info("Viewing Quote");
    System.out.println("Enter Ticker Symbol: ");
    String ticker = scanner.nextLine().trim().toUpperCase();

    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    if (optQuote.isEmpty()) {
      System.out.println("Could not find quote for: " + ticker);
      return;
    }
    Quote q = optQuote.get();
    String output = String.format(
        "Ticker: %s\tPrice: $%.2f\tOpen: $%.2f\tHigh: $%.2f\tLow: $%.2f\tVolume: %d\t" +
        "Previous Close: $%.2f\tChange: $%.2f\tChange Percent: %s%%\tLatest Trading Day: %s",
        q.getTicker(), q.getPrice(), q.getOpen(), q.getHigh(), q.getLow(), q.getVolume(),
        q.getPreviousClose(), q.getChange(), q.getChangePercent(), q.getLatestTradingDay()
    );
    System.out.println(output);
  }

  /**
   * Handle buying shares.
   */
  private void handleBuy(Scanner scanner) {
    logger.debug("Handling Buy");
    System.out.println("Enter Ticker Symbol: ");
    String ticker = scanner.nextLine().trim().toUpperCase();

    Optional<Quote> optQuote = quoteService.fetchQuoteDataFromAPI(ticker);
    if (optQuote.isEmpty()) {
      System.err.println("Error getting quote for " + ticker);
      return;
    }
    Quote q = optQuote.get();
    double price = q.getPrice();
    System.out.printf("Current price for %s is $%.2f%n\n", ticker, price);
    System.out.println("Enter number of shares to buy:");
    int numShares = scanner.nextInt();

    try {
      Position position = positionService.buy(ticker, numShares, price);
      System.out.println("Purchase successful");
      System.out.printf("Total Position for %s:\n", ticker);
      System.out.printf("Number of Shares: %d\n", position.getNumOfShares());
      System.out.printf("Total Value Paid: $%.2f\n", position.getValuePaid());
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Handle selling shares.
   */
  private void handleSell(Scanner scanner) {
    logger.debug("Handling Sell");
    System.out.println("Enter Ticker Symbol: ");
    String ticker = scanner.nextLine().trim().toUpperCase();
    try {
      positionService.sell(ticker);
      System.out.println("Successfully sold " + ticker);
    } catch (IllegalArgumentException e){
      System.err.println("Sell failed: you do not own any shares of " + ticker);
    }
  }

  /**
   * Handle viewing the portfolio.
   */
  private void handleViewPortfolio() {
    logger.debug("Viewing Portfolio");
    Iterable<Position> positions = positionService.viewPortfolio();
    if (!positions.iterator().hasNext()){
      System.out.println("No positions held");
      return;
    }
    System.out.println("Positions: (Ticker/Shares/Value)");
    String formattedLine;
    for (Position p : positions) {
      formattedLine = String.format("Ticker: %s\tShares: %d\tValue: $%,.2f",
          p.getTicker(), p.getNumOfShares(), p.getValuePaid());
      System.out.println(formattedLine);
    }
  }
}
