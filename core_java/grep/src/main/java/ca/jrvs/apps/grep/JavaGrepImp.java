package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for JavaGrep interface. Outputs to a file matching regex lines to a user given
 * pattern from lines of files in a directory.
 */
public class JavaGrepImp implements JavaGrep {

  private final Logger logger = LoggerFactory.getLogger("JavaGrep.class");

  private String regex;
  private String rootPath;
  private String outFile;

  @Override
  public void process() throws IOException {
    // TODO: implement
  }

  @Override
  public List<File> listFiles(String rootDir) {
    // TODO: implement
    return Collections.emptyList();
  }

  @Override
  public List<String> readLines(File inputFile) {
    // TODO: implement
    return Collections.emptyList();
  }

  @Override
  public boolean containsPattern(String line) {
    return Pattern.matches(this.regex, line);
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    // TODO: Implement
  }

  @Override
  public String getRootPath() {
    return this.rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    logger.debug("Set root path to {}", rootPath);
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return this.regex;
  }

  @Override
  public void setRegex(String regex) {
    logger.debug("Set regex to {}", regex);
    this.regex = regex;
  }

  @Override
  public String getOutfile() {
    return this.outFile;
  }

  @Override
  public void setOutfile(String outfile) {
    logger.debug("Set outfile to {}", outfile);
    this.outFile = outFile;
  }

  /**
   * Starts processes to simulate grep when called from the command line
   */
  public static void main(String[] args) {

  }
}
