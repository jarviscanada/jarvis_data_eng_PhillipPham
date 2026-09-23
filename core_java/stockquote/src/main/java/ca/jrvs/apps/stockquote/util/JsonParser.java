package ca.jrvs.apps.stockquote.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

public class JsonParser {

  /**
   * Converts a Java object to a JSON string
   *
   * @param object            object to convert to JSON
   * @param prettyJson        set true to indent JSON
   * @param includeNullValues set true to enable null values
   * @return JSON string
   * @throws JsonProcessingException error with format conversion
   */
  public static String toJson(Object object, boolean prettyJson, boolean includeNullValues)
      throws JsonProcessingException {
    ObjectMapper m = new ObjectMapper();
    if (!includeNullValues) {
      m.setDefaultPropertyInclusion(Include.NON_NULL);
    }
    if (prettyJson) {
      m.enable(SerializationFeature.INDENT_OUTPUT);
    }
    return m.writeValueAsString(object);
  }

  /**
   * Convert JSON string to Object
   *
   * @param json     JSON string
   * @param objClass object class
   * @param <T>      Type
   * @return Object
   * @throws IOException read error
   */
  public static <T> T toObjectFromJson(String json, Class objClass) throws IOException {
    ObjectMapper m = new ObjectMapper();
    return (T) m.readValue(json, objClass);
  }
}
