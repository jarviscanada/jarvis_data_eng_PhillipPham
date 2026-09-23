package ca.jrvs.apps.stockquote.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.model.Quote;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class QuoteHttpHelperTest {

  public final String JSON_QUOTE =
      "{\n"
          + "  \"Global Quote\": {\n"
          + "    \"01. symbol\": \"MSFT\",\n"
          + "    \"02. open\": \"479.6500\",\n"
          + "    \"03. high\": \"489.2900\",\n"
          + "    \"04. low\": \"479.5500\",\n"
          + "    \"05. price\": \"484.3100\",\n"
          + "    \"06. volume\": \"19784180\",\n"
          + "    \"07. latest trading day\": \"2026-08-19\",\n"
          + "    \"08. previous close\": \"481.6300\",\n"
          + "    \"09. change\": \"2.6800\",\n"
          + "    \"10. change percent\": \"0.5564%\"\n"
          + "  }\n"
          + "}";

  OkHttpClient mockClient = mock();
  Call mockCall = mock();

  QuoteHttpHelper httpHelper;
  Response res;

  @BeforeEach
  void setup() {
    httpHelper = new QuoteHttpHelper("test-api-key", mockClient);
  }

  @Test
  void fetchQuoteInfo_successful_call() throws IOException {
    Request request = new Request.Builder().url("https://mocksite.xxx").build();
    ResponseBody resBody = ResponseBody.create(JSON_QUOTE, MediaType.get("application/json"));
    res = new Response.Builder()
        .code(200)
        .protocol(Protocol.HTTP_1_0)
        .message("message")
        .body(resBody)
        .request(request)
        .build();

    when(mockClient.newCall(any())).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(res);

    Quote q = httpHelper.fetchQuoteInfo("MSFT");

    assertNotNull(q);
    assertEquals("MSFT", q.getTicker());
    assertEquals(19784180, q.getVolume());
    assertEquals(489.2900, q.getHigh(), .1);
    assertNotNull(q.getLatestTradingDay());
    assertNotNull(q.getTimestamp());
  }

  @Test
  void fetchQuoteInfo_empty_ticker() {
    assertThrows(IllegalArgumentException.class, () -> httpHelper.fetchQuoteInfo(""));
  }

  @Test
  void fetchQuoteInfo_invalid_ticker() throws IOException {
    Request request = new Request.Builder().url("https://mocksite.xxx").build();
    ResponseBody resBody = ResponseBody.create("{}", MediaType.get("application/json"));
    res = new Response.Builder()
        .code(200)
        .protocol(Protocol.HTTP_1_0)
        .message("message")
        .body(resBody)
        .request(request)
        .build();

    when(mockClient.newCall(any())).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(res);
    Exception e = assertThrows(IllegalArgumentException.class, () -> httpHelper.fetchQuoteInfo("msft"));
    String failure = "Quote processing failure";
    assertTrue(e.getMessage().contains(failure));
  }
}