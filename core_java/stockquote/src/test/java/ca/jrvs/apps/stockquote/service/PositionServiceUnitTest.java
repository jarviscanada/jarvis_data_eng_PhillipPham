package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.model.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionServiceUnitTest {

  PositionDao positionDao = mock();

  PositionService positionService;

  @BeforeEach
  void setup() {
    positionService = new PositionService(positionDao);
  }

  @Test
  void buy_ticker_null() {
    assertThrows(IllegalArgumentException.class, () -> positionService.buy(null, 3, 5.5));
  }

  @Test
  void buy_ticker_empty() {
    assertThrows(IllegalArgumentException.class, () -> positionService.buy("", 3, 5.5));
  }

  @Test
  void buy_shares_non_positive() {
    assertThrows(IllegalArgumentException.class, () -> positionService.buy("MSFT", 0, 5.5));
    assertThrows(IllegalArgumentException.class, () -> positionService.buy("MSFT", -1, 5.5));
    assertThrows(IllegalArgumentException.class, () -> positionService.buy("MSFT", -100, 5.5));
  }

  @Test
  void buy_price_non_positive() {
    assertThrows(IllegalArgumentException.class, () -> positionService.buy("MSFT", 3, 0));
    assertThrows(IllegalArgumentException.class, () -> positionService.buy("MSFT", 3, -1.0));
    assertThrows(IllegalArgumentException.class, () -> positionService.buy("MSFT", 3, -100.0));
  }

  @Test
  void buy_new() {
    String ticker = "MSFT";
    int numOfShares = 40;
    double price = 15;

    when(positionDao.findById(ticker)).thenReturn(Optional.empty());

    Position newPos = positionService.buy(ticker, numOfShares, price);
    assertNotNull(newPos);
    assertEquals(ticker, newPos.getTicker());
    assertEquals(numOfShares, newPos.getNumOfShares());
    assertEquals(numOfShares * price, newPos.getValuePaid());
    verify(positionDao, times(1)).findById(any());
    verify(positionDao, times(1)).save(any());
  }

  @Test
  void buy_existing() {
    String ticker = "MSFT";
    int numOfShares = 40;
    double price = 15;
    double valuePaid = 400;
    Position position = new Position();
    position.setTicker(ticker);
    position.setNumOfShares(numOfShares);
    position.setValuePaid(valuePaid);

    when(positionDao.findById(ticker)).thenReturn(Optional.of(position));

    Position newPos = positionService.buy(ticker, numOfShares, price);
    assertNotNull(newPos);
    assertEquals(ticker, newPos.getTicker());
    assertEquals(numOfShares + numOfShares, newPos.getNumOfShares());
    assertEquals(valuePaid + (numOfShares * price), newPos.getValuePaid());
    verify(positionDao, times(1)).findById(any());
    verify(positionDao, times(1)).save(any());
  }


  @Test
  void viewPortfolio_populated() {
    List<Position> posList = new ArrayList<>();
    posList.add(new Position());
    posList.add(new Position());
    posList.add(new Position());
    posList.get(0).setTicker("MSFT");
    posList.get(1).setTicker("AMZN");
    posList.get(2).setTicker("LMAO");

    when(positionDao.findAll()).thenReturn(posList);

    Iterable<Position> positions = positionService.viewPortfolio();
    int count = 0;
    for (Position ignored : positions) {
      count++;
    }
    assertEquals(3, count);
  }

  @Test
  void sell_ticker_null() {
    assertThrows(IllegalArgumentException.class, () -> positionService.sell(null));
  }

  @Test
  void sell_ticker_empty() {
    assertThrows(IllegalArgumentException.class, () -> positionService.sell(""));
  }

  @Test
  void sell_found() {
    String ticker = "MSFT";
    Position position = new Position();
    position.setTicker(ticker);
    when(positionDao.findById(ticker)).thenReturn(Optional.of(position));
    assertDoesNotThrow(() -> positionService.sell(ticker));
    verify(positionDao, times(1)).findById(ticker);
    verify(positionDao, times(1)).deleteById(ticker);
  }

  @Test
  void sell_not_found() {
    String ticker = "MSFT";
    when(positionDao.findById(ticker)).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> positionService.sell(ticker));
    verify(positionDao, times(1)).findById(ticker);
    verify(positionDao, never()).deleteById(ticker);
  }

}