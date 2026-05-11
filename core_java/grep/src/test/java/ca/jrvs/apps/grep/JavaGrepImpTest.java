package ca.jrvs.apps.grep;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JavaGrepImpTest {

  JavaGrep jGrep;

  @BeforeEach
  void init() {
    jGrep = new JavaGrepImp();
    // note this is just for local testing, can change
    jGrep.setRegex(".*Romeo.*Juliet.*");
    jGrep.setRootPath("./data");
    jGrep.setOutfile("./out/grep_unit_test.txt");
  }

  @Test
  void containsPattern_simple() {
    jGrep.setRegex("abba");
    assertTrue(jGrep.containsPattern("abba"));
  }

  @Test
  void containsPattern_wildcard() {
    jGrep.setRegex("bobo.*kiki");
    assertTrue(jGrep.containsPattern("boboANDkiki"));
  }

  @Test
  void containsPattern_charclass() {
    jGrep.setRegex("\\D+\\d+\\D+");
    assertTrue(jGrep.containsPattern("alice123bob"));
  }

  @Test
  void listFiles_none() {
    jGrep.setRootPath("./data/empty");
    assertTrue(jGrep.listFiles(jGrep.getRootPath()).isEmpty());
  }

  @Test
  void listFiles_default() {
    assertFalse(jGrep.listFiles(jGrep.getRootPath()).isEmpty());
  }

}